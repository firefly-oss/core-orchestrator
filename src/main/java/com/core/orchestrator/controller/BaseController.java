package com.core.orchestrator.controller;

import io.camunda.zeebe.client.ZeebeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller that handles customer-related API endpoints.
 * Provides endpoints for creating legal and natural persons by starting Camunda Zeebe processes.
 */
@RestController
public class BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);
    public static final String CREATE_DOCUMENT = "create-document";
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
    public BaseController(ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    /**
     * Helper method to start a Zeebe process with the given process ID and variables.
     *
     * @param processId The ID of the process to start
     * @param variables The variables to pass to the process
     * @param <T> The type of the variables
     * @return A response containing the process instance key and status
     */
    protected <T> ResponseEntity<Map<String, Object>> startProcess(String processId, T variables) {
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
