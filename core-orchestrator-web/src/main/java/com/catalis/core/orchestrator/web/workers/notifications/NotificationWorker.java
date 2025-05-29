package com.catalis.core.orchestrator.web.workers.notifications;

import com.catalis.common.platform.notification.services.sdk.model.EmailResponseDTO;
import com.catalis.common.sca.sdk.model.SCAChallengeDTO;
import com.catalis.common.sca.sdk.model.SCAOperationDTO;
import com.catalis.common.sca.sdk.model.ValidationResultDTO;
import com.catalis.core.orchestrator.interfaces.dtos.notifications.CreateChallengeRequest;
import com.catalis.core.orchestrator.interfaces.dtos.notifications.NotificationRequest;
import com.catalis.core.orchestrator.interfaces.dtos.notifications.SendNotificationRequest;
import com.catalis.core.orchestrator.interfaces.dtos.notifications.ValidateCodeRequest;
import com.catalis.core.orchestrator.interfaces.mappers.EmailMapper;
import com.catalis.core.orchestrator.interfaces.services.NotificationsService;
import com.catalis.core.orchestrator.interfaces.services.SCAService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Worker component that handles email notification-related tasks in Camunda Zeebe workflows.
 * Provides job workers for sending verification emails and creating SCA operations and challenges.
 */
@Component
@Slf4j
public class NotificationWorker {

    private static final String EMAIL_ID = "emailId";
    private static final String OPERATION_ID = "operationId";
    private static final String CHALLENGE_ID = "challengeId";

    private final SCAService scaService;
    private final NotificationsService notificationsService;
    private final EmailMapper emailMapper;

    @Autowired
    public NotificationWorker(SCAService scaService, NotificationsService notificationsService,
                              EmailMapper emailMapper) {
        this.scaService = scaService;
        this.notificationsService = notificationsService;
        this.emailMapper = emailMapper;
    }

    /**
     * Job worker that handles creating SCA operations.
     * This is a mocked implementation that simulates creating an SCA operation.
     *
     * @param job The activated job containing the email data and email ID
     * @return A map containing the SCA operation response
     */
    @JobWorker(type = "create-sca-operation-task")
    public SendNotificationRequest createSCAOperation(final ActivatedJob job) {
        log.info("Executing create-sca-operation-task for job: {}", job.getKey());

        // Get variables from the process
        Map<String, Object> variables = job.getVariablesAsMap();
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
     * @param job The activated job containing the email data, email ID, and operation ID
     * @return A map containing the SCA challenge response
     */
    @JobWorker(type = "create-sca-challenge-task")
    public Map<String, Object> createSCAChallenge(final ActivatedJob job) {
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
        Map<String, Object> result = new HashMap<>();
        result.put(CHALLENGE_ID, scaChallenge.getId());
        result.put("challengeCode", scaChallenge.getChallengeCode());

        return result;
    }

    /**
     * Job worker that handles validating SCA challenges.
     * This worker validates a verification code for a given operation.
     *
     * @param job The activated job containing the operation ID and verification code
     * @return A map containing the validation result
     */
    @JobWorker(type = "validate-sca-challenge-task")
    public Map<String, Object> validateSCAChallenge(final ActivatedJob job) {
        log.info("Executing validate-sca-challenge-task for job: {}", job.getKey());

        // Get variables from the process
        Map<String, Object> variables = job.getVariablesAsMap();
        Long operationId = job.getVariablesAsType(ValidateCodeRequest.class).idOperation();
        String code = job.getVariablesAsType(ValidateCodeRequest.class).code();

        log.info("Validating SCA challenge for operation ID: {} with code: {}", operationId, code);

        // WebClient call
        Mono<ResponseEntity<ValidationResultDTO>> responseMono = scaService.validateSCA(operationId, code);

        // Get the response
        ResponseEntity<ValidationResultDTO> response = responseMono.block();
        ValidationResultDTO validationResultDTO = response.getBody();

        log.info("SCA challenge validation result: {}", validationResultDTO.getSuccess());

        // Prepare result for the process
        Map<String, Object> result = new HashMap<>(variables);
        result.put("validationStatus", validationResultDTO.getSuccess());
        result.put(OPERATION_ID, operationId);

        return result;
    }
}
