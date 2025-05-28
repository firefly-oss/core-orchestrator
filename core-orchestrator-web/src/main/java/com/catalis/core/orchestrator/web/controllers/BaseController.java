package com.catalis.core.orchestrator.web.controllers;

import io.camunda.zeebe.client.ZeebeClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller that handles customer-related API endpoints.
 * Provides endpoints for creating legal and natural persons by starting Camunda Zeebe processes.
 */
@RestController
@Slf4j
public class BaseController {

    public static final String CREATE_DOCUMENT = "create-document";
    public static final String CREATE_LEGAL_PERSON = "create-legal-person";
    public static final String CREATE_NATURAL_PERSON = "create-natural-person";
    public static final String CREATE_TAX_RESIDENCE = "create-tax-residence-process";
    public static final String USER_KYC_REVIEW = "user-kyc-review-process";
    public static final String USER_KYB_REVIEW = "user-kyb-review-process";
    public static final String CREATE_ACCOUNT = "create-account-process";
    public static final String SEND_VERIFICATION_EMAIL = "send-verification-email";
    public static final String SEND_VERIFICATION_SMS = "send-verification-sms";
    public static final String VALIDATE_VERIFICATION_CODE = "validate-verification-code";
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
        log.info("Starting {} process", processId);

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
