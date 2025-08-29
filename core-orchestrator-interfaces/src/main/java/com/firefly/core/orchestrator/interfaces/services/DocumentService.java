package com.firefly.core.orchestrator.interfaces.services;

import com.firefly.core.orchestrator.interfaces.dtos.documents.DocumentRequest;
import com.firefly.core.orchestrator.interfaces.dtos.documents.DocumentResponse;
import reactor.core.publisher.Mono;

/**
 * Interface for document-related operations.
 * Provides methods for creating and managing documents.
 */
public interface DocumentService {

    /**
     * Creates a document in the external BaaS system.
     *
     * @param documentRequest the document data to create
     * @return a Mono containing the response with the created document data
     */
    Mono<DocumentResponse> createDocument(DocumentRequest documentRequest);
}