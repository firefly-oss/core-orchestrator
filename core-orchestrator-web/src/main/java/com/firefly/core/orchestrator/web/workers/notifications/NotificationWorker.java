package com.firefly.core.orchestrator.web.workers.notifications;

import com.firefly.core.orchestrator.interfaces.dtos.notifications.*;
import com.firefly.core.orchestrator.interfaces.services.NotificationSCAService;
import com.firefly.core.orchestrator.web.utils.ProcessCompletionRegistry;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Worker component that handles general notification-related tasks in Camunda Zeebe workflows.
 * Provides job workers for creating and validating SCA operations and challenges.
 * Delegates to NotificationSCAService for business logic.
 */
@Component
@Slf4j
public class NotificationWorker {

    private final NotificationSCAService notificationSCAService;
    private final ProcessCompletionRegistry processCompletionRegistry;

    @Autowired
    public NotificationWorker(NotificationSCAService notificationSCAService, ProcessCompletionRegistry processCompletionRegistry) {
        this.notificationSCAService = notificationSCAService;
        this.processCompletionRegistry = processCompletionRegistry;
    }

    /**
     * Job worker that handles creating SCA operations.
     * This worker delegates to the NotificationSCAService to create an SCA operation.
     *
     * @param job The activated job containing the notification data
     * @return A Mono containing the SendNotificationRequest with operation ID and recipient
     */
    @JobWorker(type = "create-sca-operation-task")
    public Mono<SendNotificationRequest> createSCAOperation(final ActivatedJob job) {
        log.info("Executing create-sca-operation-task for job: {}", job.getKey());

        // Get variables from the process
        NotificationRequest notificationRequest = job.getVariablesAsType(NotificationRequest.class);

        log.info("Delegating SCA operation creation for email: {}", notificationRequest.to());

        // Delegate to the notification SCA service
        return notificationSCAService.createSCAOperation(notificationRequest);
    }

    /**
     * Job worker that handles creating SCA challenges.
     * This worker delegates to the NotificationSCAService to create an SCA challenge.
     *
     * @param job The activated job containing the CreateChallengeRequest with operation ID and verification code
     * @return A Mono containing the SendNotificationResponse with operation ID
     */
    @JobWorker(type = "create-sca-challenge-task")
    public Mono<SendNotificationResponse> createSCAChallenge(final ActivatedJob job) {
        log.info("Executing create-sca-challenge-task for job: {}", job.getKey());

        // Get variables from the process
        CreateChallengeRequest createChallengeRequest = job.getVariablesAsType(CreateChallengeRequest.class);

        log.info("Delegating SCA challenge creation for operation ID: {}", createChallengeRequest.getIdOperation());

        // Delegate to the notification SCA service
        return notificationSCAService.createSCAChallenge(createChallengeRequest)
                .doOnNext(result -> {
                    // Complete the process in the registry
                    processCompletionRegistry.completeProcess(job.getProcessInstanceKey(), result);
                });
    }

    /**
     * Job worker that handles validating SCA challenges.
     * This worker delegates to the NotificationSCAService to validate an SCA challenge.
     *
     * @param job The activated job containing the operation ID and verification code
     * @return A Mono containing the ValidateSCAResponse with validation status and operation ID
     */
    @JobWorker(type = "validate-sca-challenge-task")
    public Mono<ValidateSCAResponse> validateSCAChallenge(final ActivatedJob job) {
        log.info("Executing validate-sca-challenge-task for job: {}", job.getKey());

        // Get variables from the process
        ValidateCodeRequest validateCodeRequest = job.getVariablesAsType(ValidateCodeRequest.class);

        log.info("Delegating SCA challenge validation for operation ID: {} with code: {}", 
                validateCodeRequest.idOperation(), validateCodeRequest.code());

        // Delegate to the notification SCA service
        return notificationSCAService.validateSCAChallenge(validateCodeRequest)
                .doOnNext(result -> {
                    // Complete the process in the registry
                    processCompletionRegistry.completeProcess(job.getProcessInstanceKey(), result);
                });
    }
}
