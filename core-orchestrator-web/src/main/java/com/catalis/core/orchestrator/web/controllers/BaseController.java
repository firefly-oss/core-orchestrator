package com.catalis.core.orchestrator.web.controllers;

import com.catalis.core.orchestrator.interfaces.dtos.process.ProcessResponse;
import com.catalis.core.orchestrator.web.utils.ProcessCompletionRegistry;
import io.camunda.zeebe.client.ZeebeClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * REST controller that handles customer-related API endpoints.
 * Provides endpoints for creating legal and natural persons by starting Camunda Zeebe processes.
 */
@Component
@Slf4j
public class BaseController {

    public static final String CREATE_DOCUMENT = "create-document";
    public static final String CREATE_LEGAL_PERSON = "create-legal-person";
    public static final String CREATE_NATURAL_PERSON = "create-natural-person";
    public static final String CREATE_TAX_RESIDENCE = "create-tax-residence-process";
    public static final String USER_KYC_REVIEW = "user-kyc-review-process";
    public static final String USER_KYB_REVIEW = "user-kyb-review-process";
    public static final String CREATE_ACCOUNT = "create-account-process";
    public static final String CREATE_BENEFICIARY = "create-beneficiary-process";
    public static final String SEND_VERIFICATION_EMAIL = "send-verification-email";
    public static final String SEND_VERIFICATION_SMS = "send-verification-sms";
    public static final String VALIDATE_VERIFICATION_CODE = "validate-verification-code";
    public static final String STARTED = "started";
    private static final int TIMEOUT_SECONDS = 30;

    private final ZeebeClient zeebeClient;
    private final ProcessCompletionRegistry processCompletionRegistry;

    /**
     * Constructs a new CustomerController with the specified Zeebe client.
     *
     * @param zeebeClient The client used to interact with the Camunda Zeebe workflow engine
     */
    @Autowired
    public BaseController(ZeebeClient zeebeClient, ProcessCompletionRegistry processCompletionRegistry) {
        this.zeebeClient = zeebeClient;
        this.processCompletionRegistry = processCompletionRegistry;
    }

    /**
     * Helper method to start a Zeebe process with the given process ID and variables.
     *
     * @param processId The ID of the process to start
     * @param variables The variables to pass to the process
     * @param <T> The type of the variables
     * @return A ProcessResponse containing the process instance key and status
     */
    protected <T> ProcessResponse startProcess(String processId, T variables) {
        log.info("Starting {} process", processId);

        final var processInstanceEvent = zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId(processId)
                .latestVersion()
                .variables(variables)
                .send()
                .join();

        return new ProcessResponse(
                processInstanceEvent.getProcessInstanceKey(),
                STARTED
        );
    }

    /**
     * Waits for the process to complete using the process completion registry.
     *
     * @param processInstanceKey The key of the process instance to wait for
     * @param <T> The type of the result that will be returned when the process completes
     * @return The result of the process execution
     * @throws ExecutionException If an error occurs during execution
     * @throws InterruptedException If the thread is interrupted
     * @throws TimeoutException If the process execution times out
     */
    protected <T> T waitForProcessCompletion(long processInstanceKey) throws ExecutionException, InterruptedException, TimeoutException {
        log.info("Waiting for process instance {} to complete", processInstanceKey);

        try {
            // Register the process instance with the registry and get a future
            // that will be completed when the process completes
            CompletableFuture<T> completionFuture = processCompletionRegistry.registerProcess(processInstanceKey);

            // Wait for the process to complete with a timeout
            return completionFuture.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            // Clean up the registry in case of an error
            processCompletionRegistry.removeProcess(processInstanceKey);
            throw e;
        }
    }
}
