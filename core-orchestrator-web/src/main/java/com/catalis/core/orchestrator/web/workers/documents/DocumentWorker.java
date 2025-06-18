package com.catalis.core.orchestrator.web.workers.documents;

import com.catalis.baas.adapter.DocumentAdapter;
import com.catalis.baas.dtos.documents.DocumentAdapterDTO;
import com.catalis.core.orchestrator.interfaces.dtos.documents.DocumentRequest;
import com.catalis.core.orchestrator.interfaces.dtos.documents.DocumentResponse;
import com.catalis.core.orchestrator.interfaces.mappers.DocumentMapper;
import com.google.protobuf.ServiceException;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Worker component that handles document-related tasks in Camunda Zeebe workflows.
 * Provides job workers for creating documents in the external system.
 */
@Component
@Slf4j
public class DocumentWorker {

    private static final String EXTERNAL_REFERENCE_ID = "externalReferenceId";

    private final DocumentAdapter documentAdapter;
    private final DocumentMapper documentMapper;

    /**
     * Constructs a new DocumentWorker with the specified document adapter and mapper.
     *
     * @param documentAdapter The adapter used to communicate with the document service
     * @param documentMapper The mapper used to convert between DTOs and request objects
     */
    @Autowired
    public DocumentWorker(DocumentAdapter documentAdapter, DocumentMapper documentMapper) {
        this.documentAdapter = documentAdapter;
        this.documentMapper = documentMapper;
    }

    /**
     * Job worker that handles the creation of documents in the external BaaS system.
     *
     * @param job The activated job containing the document data
     * @return A map containing the external reference ID
     * @throws ServiceException If there's an error calling the external service
     */
    @JobWorker(type = "baas-create-document")
    public Mono<DocumentResponse> baasCreateDocument(final ActivatedJob job) throws ServiceException {
        log.info("Executing baas-create-document task for job: {}", job.getKey());

        // Get variables from the process
        DocumentRequest documentData = job.getVariablesAsType(DocumentRequest.class);

        log.info("Creating document: {}", documentData.name());

        // Call the external microservice
        Mono<DocumentAdapterDTO> documentAdapterDTO;
        try {
            documentAdapterDTO = documentAdapter.createDocument(documentMapper.requestToDTO(documentData))
                    .mapNotNull(ResponseEntity::getBody);
        } catch (WebClientResponseException e) {
            log.error("Error calling external service: {}", e.getMessage());
            throw new ServiceException("Failed to create document", e);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            throw new ServiceException("Unexpected error creating document", e);
        }
        log.info("External ID retrieved successfully");

        return documentAdapterDTO.map(documentMapper::dtoToResponse);
    }

}
