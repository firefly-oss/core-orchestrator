package com.catalis.core.orchestrator.web.workers.notifications;

import com.catalis.common.sca.sdk.model.SCAChallengeDTO;
import com.catalis.common.sca.sdk.model.SCAOperationDTO;
import com.catalis.common.sca.sdk.model.ValidationResultDTO;
import com.catalis.core.orchestrator.interfaces.dtos.notifications.*;
import com.catalis.core.orchestrator.interfaces.services.SCAService;
import com.catalis.core.orchestrator.web.utils.ProcessCompletionRegistry;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Worker component that handles general notification-related tasks in Camunda Zeebe workflows.
 * Provides job workers for creating and validating SCA operations and challenges.
 * Uses SCAService to create and validate SCA operations and challenges.
 * Uses NotificationsService to send notifications.
 * Uses EmailMapper to map email-related data.
 */
@Component
@Slf4j
public class NotificationWorker {

    private static final String EMAIL_ID = "emailId";
    private static final String OPERATION_ID = "operationId";
    private static final String CHALLENGE_ID = "challengeId";

    private final SCAService scaService;
    private final ProcessCompletionRegistry processCompletionRegistry;

    @Autowired
    public NotificationWorker(SCAService scaService, ProcessCompletionRegistry processCompletionRegistry) {
        this.scaService = scaService;
        this.processCompletionRegistry = processCompletionRegistry;
    }

    /**
     * Job worker that handles creating SCA operations.
     * This is a mocked implementation that simulates creating an SCA operation.
     *
     * @param job The activated job containing the notification data
     * @return A SendNotificationRequest containing the operation ID and recipient
     */
    @JobWorker(type = "create-sca-operation-task")
    public SendNotificationRequest createSCAOperation(final ActivatedJob job) {
        log.info("Executing create-sca-operation-task for job: {}", job.getKey());

        // Get variables from the process
        NotificationRequest notificationRequest = job.getVariablesAsType(NotificationRequest.class);
        log.info("Creating SCA operation for email: {}", notificationRequest.to());

        // WebClient call - pass EmailRequest directly to scaService
        Mono<ResponseEntity<SCAOperationDTO>> responseMono = scaService.createOperation(notificationRequest);

        // Get the response
        ResponseEntity<SCAOperationDTO> response = responseMono.block();
        SCAOperationDTO scaOperation = response.getBody();

        log.info("SCA operation created successfully with ID: {}", scaOperation.getId());

        // Prepare result for the process
        SendNotificationRequest sendNotificationRequest = new SendNotificationRequest();
        sendNotificationRequest.setIdOperation(scaOperation.getId());
        sendNotificationRequest.setTo(notificationRequest.to());
        return sendNotificationRequest;
    }

    /**
     * Job worker that handles creating SCA challenges.
     * This is a mocked implementation that simulates creating an SCA challenge.
     *
     * @param job The activated job containing the CreateChallengeRequest with operation ID and verification code
     * @return A map containing the SCA challenge response
     */
    @JobWorker(type = "create-sca-challenge-task")
    public SendNotificationResponse createSCAChallenge(final ActivatedJob job) {
        log.info("Executing create-sca-challenge-task for job: {}", job.getKey());

        // Get variables from the process
        CreateChallengeRequest createChallengeRequest = job.getVariablesAsType(CreateChallengeRequest.class);

        log.info("Creating SCA challenge for operation ID: {}", createChallengeRequest.getIdOperation());

        // WebClient call
        Mono<ResponseEntity<SCAChallengeDTO>> responseMono = scaService.createChallenge(createChallengeRequest.getIdOperation(), createChallengeRequest.getVerificationCode());

        // Get the response
        ResponseEntity<SCAChallengeDTO> response = responseMono.block();
        SCAChallengeDTO scaChallenge = response.getBody();

        log.info("SCA challenge created successfully with ID: {}", scaChallenge.getId());

        // Prepare result for the process
        SendNotificationResponse result = SendNotificationResponse.builder()
                .idOperation(scaChallenge.getScaOperationId())
                .build();
        processCompletionRegistry.completeProcess(job.getProcessInstanceKey(), result);
        return result;
    }

    /**
     * Job worker that handles validating SCA challenges.
     * This worker validates a verification code for a given operation.
     *
     * @param job The activated job containing the operation ID and verification code
     * @return A ValidateSCAResponse containing the validation status and operation ID
     */
    @JobWorker(type = "validate-sca-challenge-task")
    public ValidateSCAResponse validateSCAChallenge(final ActivatedJob job) {
        log.info("Executing validate-sca-challenge-task for job: {}", job.getKey());

        // Get variables from the process
        Long operationId = job.getVariablesAsType(ValidateCodeRequest.class).idOperation();
        String code = job.getVariablesAsType(ValidateCodeRequest.class).code();

        log.info("Validating SCA challenge for operation ID: {} with code: {}", operationId, code);

        // WebClient call
        Mono<ResponseEntity<ValidationResultDTO>> responseMono = scaService.validateSCA(operationId, code);

        // Get the response
        ResponseEntity<ValidationResultDTO> response = responseMono.block();
        ValidationResultDTO validationResultDTO = response.getBody();

        log.info("SCA challenge validation result: {}", validationResultDTO.getSuccess());
        // Complete the future in the registry to notify the controller
        // that the process has completed
        ValidateSCAResponse result = ValidateSCAResponse.builder()
                .operationId(operationId)
                .validationStatus(validationResultDTO.getSuccess()).
                build();
        processCompletionRegistry.completeProcess(job.getProcessInstanceKey(), result);

        // Prepare result for the process
        return result;
    }
}
