package com.catalis.core.orchestrator.web.controllers.documents;

import com.catalis.baas.dtos.documents.DocumentAdapterDTO;
import com.catalis.core.orchestrator.interfaces.dtos.documents.DocumentRequest;
import com.catalis.core.orchestrator.web.controllers.BaseController;
import io.camunda.zeebe.client.ZeebeClient;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller that handles document-related API endpoints.
 * Provides endpoints for creating documents by starting Camunda Zeebe processes.
 */
@RestController
@RequestMapping("/api/v1/documents")
@Slf4j
public class DocumentController extends BaseController {

    /**
     * Constructs a new DocumentController with the specified Zeebe client.
     *
     * @param zeebeClient The client used to interact with the Camunda Zeebe workflow engine
     */
    @Autowired
    public DocumentController(ZeebeClient zeebeClient) {
        super(zeebeClient);
    }

    /**
     * Starts a process to create a document.
     *
     * @param documentData The document data to be processed
     * @return A response containing the process instance key and status
     */
    @PostMapping(value = "/create-document")
    public ResponseEntity<Map<String, Object>> startCreateDocumentProcess(@RequestBody DocumentRequest documentData) {
        log.info("Starting create-document process with data: {}", documentData);

        try {
            return startProcess(CREATE_DOCUMENT, documentData);
        } catch (Exception e) {
            log.error("Error starting process: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to start process", "message", e.getMessage()));
        }
    }
}