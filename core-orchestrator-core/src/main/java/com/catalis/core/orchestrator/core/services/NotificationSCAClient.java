package com.catalis.core.orchestrator.core.services;

import com.catalis.common.sca.sdk.model.SCAChallengeDTO;
import com.catalis.common.sca.sdk.model.SCAOperationDTO;
import com.catalis.common.sca.sdk.model.ValidationResultDTO;
import com.catalis.core.orchestrator.interfaces.dtos.notifications.*;
import com.catalis.core.orchestrator.interfaces.services.NotificationSCAService;
import com.catalis.core.orchestrator.interfaces.services.SCAService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Implementation of the NotificationSCAService interface.
 * Provides higher-level methods for creating operations, challenges, and validating challenges.
 * Uses the SCAService to perform the actual operations and handles the transformation of the response data.
 */
@Service
@Slf4j
public class NotificationSCAClient implements NotificationSCAService {

    private final SCAService scaService;

    /**
     * Creates a new NotificationSCAClient with the specified SCA service.
     *
     * @param scaService the service used to perform SCA operations
     */
    @Autowired
    public NotificationSCAClient(SCAService scaService) {
        this.scaService = scaService;
    }

    /**
     * Creates a new SCA operation and prepares a notification request.
     *
     * @param notificationRequest the notification request data used to create the operation
     * @return a Mono containing the SendNotificationRequest with operation ID and recipient
     */
    @Override
    public Mono<SendNotificationRequest> createSCAOperation(NotificationRequest notificationRequest) {
        log.info("Creating SCA operation for email: {}", notificationRequest.to());

        return scaService.createOperation(notificationRequest)
                .map(ResponseEntity::getBody)
                .map(scaOperation -> {
                    log.info("SCA operation created successfully with ID: {}", scaOperation.getId());

                    // Prepare result for the process
                    SendNotificationRequest sendNotificationRequest = new SendNotificationRequest();
                    sendNotificationRequest.setIdOperation(scaOperation.getId());
                    sendNotificationRequest.setTo(notificationRequest.to());
                    return sendNotificationRequest;
                });
    }

    /**
     * Creates a challenge for a given operation ID and verification code.
     *
     * @param createChallengeRequest the request containing operation ID and verification code
     * @return a Mono containing the SendNotificationResponse with operation ID
     */
    @Override
    public Mono<SendNotificationResponse> createSCAChallenge(CreateChallengeRequest createChallengeRequest) {
        log.info("Creating SCA challenge for operation ID: {}", createChallengeRequest.getIdOperation());

        return scaService.createChallenge(createChallengeRequest.getIdOperation(), createChallengeRequest.getVerificationCode())
                .map(ResponseEntity::getBody)
                .map(scaChallenge -> {
                    log.info("SCA challenge created successfully with ID: {}", scaChallenge.getId());

                    // Prepare result for the process
                    return SendNotificationResponse.builder()
                            .idOperation(scaChallenge.getScaOperationId())
                            .build();
                });
    }

    /**
     * Validates a challenge for a given operation ID and verification code.
     *
     * @param validateCodeRequest the request containing operation ID and verification code
     * @return a Mono containing the ValidateSCAResponse with validation status and operation ID
     */
    @Override
    public Mono<ValidateSCAResponse> validateSCAChallenge(ValidateCodeRequest validateCodeRequest) {
        log.info("Validating SCA challenge for operation ID: {} with code: {}", validateCodeRequest.idOperation(), validateCodeRequest.code());

        return scaService.validateSCA(validateCodeRequest.idOperation(), validateCodeRequest.code())
                .map(ResponseEntity::getBody)
                .map(validationResultDTO -> {
                    log.info("SCA challenge validation result: {}", validationResultDTO.getSuccess());

                    // Prepare result for the process
                    return ValidateSCAResponse.builder()
                            .operationId(validateCodeRequest.idOperation())
                            .validationStatus(validationResultDTO.getSuccess())
                            .build();
                });
    }
}
