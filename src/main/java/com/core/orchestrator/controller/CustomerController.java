package com.core.orchestrator.controller;

import com.catalis.baas.dtos.customers.LegalPersonAdapterDTO;
import com.catalis.baas.dtos.customers.NaturalPersonAdapterDTO;
import io.camunda.zeebe.client.ZeebeClient;
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
 * REST controller that handles customer-related API endpoints.
 * Provides endpoints for creating legal and natural persons by starting Camunda Zeebe processes.
 */
@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController extends BaseController{

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerController.class);

    /**
     * Constructs a new CustomerController with the specified Zeebe client.
     *
     * @param zeebeClient The client used to interact with the Camunda Zeebe workflow engine
     */
    @Autowired
    public CustomerController(ZeebeClient zeebeClient) {
        super(zeebeClient);
    }

    /**
     * Starts a process to create a legal person.
     *
     * @param userData The legal person data to be processed
     * @return A response containing the process instance key and status
     */
    @PostMapping(value = "/create-legal-person")
    public ResponseEntity<Map<String, Object>> startCreateLegalPersonProcess(@RequestBody LegalPersonAdapterDTO userData) {
        LOGGER.info("Starting create-legal-person process with data: {}", userData);

        try {
            return startProcess(CREATE_LEGAL_PERSON, userData);
        } catch (Exception e) {
            LOGGER.error("Error starting process: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to start process", "message", e.getMessage()));
        }
    }

    /**
     * Starts a process to create a natural person.
     *
     * @param userData The natural person data to be processed
     * @return A response containing the process instance key and status
     */
    @PostMapping(value = "/create-natural-person")
    public ResponseEntity<Map<String, Object>> startCreateNaturalPersonProcess(@RequestBody NaturalPersonAdapterDTO userData) {
        LOGGER.info("Starting create-natural-person process with data: {}", userData);

        try {
            return startProcess(CREATE_NATURAL_PERSON, userData);
        } catch (Exception e) {
            LOGGER.error("Error starting process: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to start process", "message", e.getMessage()));
        }
    }

}
