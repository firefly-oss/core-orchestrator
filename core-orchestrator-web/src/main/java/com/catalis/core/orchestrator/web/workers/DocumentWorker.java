package com.catalis.core.orchestrator.web.workers;

import com.catalis.baas.adapter.DocumentAdapter;
import com.catalis.baas.dtos.documents.DocumentAdapterDTO;
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
 * Worker component that handles customer-related tasks in Camunda Zeebe workflows.
 * Provides job workers for creating legal and natural persons and storing their data.
 */
@Component
@Slf4j
public class DocumentWorker {
    
    private static final String EXTERNAL_REFERENCE_ID = "externalReferenceId";
    
    private final DocumentAdapter documentAdapter;


    /**
     * Constructs a new DocumentWorker with the specified document adapter.
     *
     * @param documentAdapter The adapter used to communicate with the document service
     */
    @Autowired
    public DocumentWorker(DocumentAdapter documentAdapter) {
        this.documentAdapter = documentAdapter;
    }

    /**
     * Job worker that handles the creation of legal persons in the external BaaS system.
     *
     * @param job The activated job containing the legal person data
     * @return A map containing the external reference ID
     * @throws ServiceException If there's an error calling the external service
     */
    @JobWorker(type = "baas-create-document")
    public Map<String, Object> baasCreateDocument(final ActivatedJob job) throws ServiceException {
        log.info("Executing baas-create-document task for job: {}", job.getKey());

        // Get variables from the process
        DocumentAdapterDTO documentData = job.getVariablesAsType(DocumentAdapterDTO.class);

        log.info("Creating document: {}", documentData.name());

        // Call the external microservice
        Mono<String> externalId;
        try {
            externalId = documentAdapter.createDocument(documentData)
                    .mapNotNull(ResponseEntity::getBody);
        } catch (WebClientResponseException e) {
            log.error("Error calling external service: {}", e.getMessage());
            throw new ServiceException("Failed to create document", e);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            throw new ServiceException("Unexpected error creating document", e);
        }
        log.info("External ID retrieved successfully");

        // Prepare result for the process
        Map<String, Object> result = new HashMap<>();
        result.put(EXTERNAL_REFERENCE_ID, externalId);

        return result;
    }

}
