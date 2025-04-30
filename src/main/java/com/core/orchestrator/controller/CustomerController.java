package com.core.orchestrator.controller;

import com.catalis.core.customers.interfaces.dtos.FrontLegalPersonDTO;
import com.catalis.core.customers.interfaces.dtos.FrontNaturalPersonDTO;
import io.camunda.zeebe.client.ZeebeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * REST controller that handles customer-related API endpoints.
 * Provides endpoints for creating legal and natural persons by starting Camunda Zeebe processes.
 */
@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerController.class);
    public static final String CREATE_LEGAL_PERSON = "create-legal-person";
    public static final String CREATE_NATURAL_PERSON = "create-natural-person";
    public static final String PROCESS_INSTANCE_KEY = "processInstanceKey";
    public static final String STATUS = "status";
    public static final String STARTED = "started";

    private final ZeebeClient zeebeClient;

    /**
     * Constructs a new CustomerController with the specified Zeebe client.
     *
     * @param zeebeClient The client used to interact with the Camunda Zeebe workflow engine
     */
    @Autowired
    public CustomerController(ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    /**
     * Starts a process to create a legal person.
     *
     * @param userData The legal person data to be processed
     * @return A response containing the process instance key and status
     */
    @PostMapping(value = "/create-legal-person")
    public ResponseEntity<Map<String, Object>> startCreateLegalPersonProcess(@RequestBody FrontLegalPersonDTO userData) {
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
    public ResponseEntity<Map<String, Object>> startCreateNaturalPersonProcess(@RequestBody FrontNaturalPersonDTO userData) {
        LOGGER.info("Starting create-natural-person process with data: {}", userData);

        try {
            return startProcess(CREATE_NATURAL_PERSON, userData);
        } catch (Exception e) {
            LOGGER.error("Error starting process: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to start process", "message", e.getMessage()));
        }
    }

    /**
     * Helper method to start a Zeebe process with the given process ID and variables.
     *
     * @param processId The ID of the process to start
     * @param variables The variables to pass to the process
     * @param <T> The type of the variables
     * @return A response containing the process instance key and status
     */
    private <T> ResponseEntity<Map<String, Object>> startProcess(String processId, T variables) {
        LOGGER.info("Starting {} process", processId);

        final var processInstanceEvent = zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId(processId)
                .latestVersion()
                .variables(variables)
                .send()
                .join();

        Map<String, Object> response = Map.of(
                PROCESS_INSTANCE_KEY, processInstanceEvent.getProcessInstanceKey(),
                STATUS, STARTED
        );

        return ResponseEntity.ok(response);
    }
}
