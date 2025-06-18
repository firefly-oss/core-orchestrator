package com.catalis.core.orchestrator.web.workers.documents;

import com.catalis.core.orchestrator.interfaces.dtos.documents.DocumentRequest;
import com.catalis.core.orchestrator.interfaces.dtos.documents.DocumentResponse;
import com.catalis.core.orchestrator.interfaces.services.DocumentService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Worker component that handles document-related tasks in Camunda Zeebe workflows.
 * Provides job workers for creating documents in the external system.
 */
@Component
@Slf4j
public class DocumentWorker {

    private final DocumentService documentService;

    /**
     * Constructs a new DocumentWorker with the specified document service.
     *
     * @param documentService The service used to communicate with the document service
     */
    public DocumentWorker(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Job worker that handles the creation of documents in the external BaaS system.
     *
     * @param job The activated job containing the document data
     * @return A Mono containing the document response
     */
    @JobWorker(type = "baas-create-document")
    public Mono<DocumentResponse> baasCreateDocument(final ActivatedJob job) {
        log.info("Executing baas-create-document task for job: {}", job.getKey());

        // Get variables from the process
        DocumentRequest documentData = job.getVariablesAsType(DocumentRequest.class);

        log.info("Delegating document creation: {}", documentData.name());

        // Delegate to the document service
        return documentService.createDocument(documentData);
    }

}
