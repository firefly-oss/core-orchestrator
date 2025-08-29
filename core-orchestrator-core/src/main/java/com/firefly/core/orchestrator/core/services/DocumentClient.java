package com.firefly.core.orchestrator.core.services;

import com.firefly.baas.adapter.DocumentAdapter;
import com.firefly.baas.dtos.documents.DocumentAdapterDTO;
import com.firefly.core.orchestrator.interfaces.dtos.documents.DocumentRequest;
import com.firefly.core.orchestrator.interfaces.dtos.documents.DocumentResponse;
import com.firefly.core.orchestrator.interfaces.mappers.DocumentMapper;
import com.firefly.core.orchestrator.interfaces.services.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Implementation of the DocumentService interface.
 * Provides methods for creating and managing documents using the BaaS adapter.
 */
@Service
@Slf4j
public class DocumentClient implements DocumentService {

    private final DocumentAdapter documentAdapter;
    private final DocumentMapper documentMapper;

    /**
     * Creates a new DocumentClient with the specified adapter and mapper.
     *
     * @param documentAdapter the adapter for interacting with the BaaS system
     * @param documentMapper the mapper for converting between DTOs
     */
    @Autowired
    public DocumentClient(DocumentAdapter documentAdapter, DocumentMapper documentMapper) {
        this.documentAdapter = documentAdapter;
        this.documentMapper = documentMapper;
    }

    /**
     * Creates a document in the external BaaS system.
     *
     * @param documentRequest the document data to create
     * @return a Mono containing the response with the created document data
     */
    @Override
    public Mono<DocumentResponse> createDocument(DocumentRequest documentRequest) {
        log.info("Creating document: {}", documentRequest.name());

        // Call the external microservice
        return documentAdapter.createDocument(documentMapper.requestToDTO(documentRequest))
                .mapNotNull(ResponseEntity::getBody)
                .map(documentMapper::dtoToResponse)
                .doOnError(WebClientResponseException.class, e -> 
                    log.error("Error calling external service: {}", e.getMessage()))
                .doOnError(Exception.class, e -> 
                    log.error("Unexpected error: {}", e.getMessage()));
    }
}