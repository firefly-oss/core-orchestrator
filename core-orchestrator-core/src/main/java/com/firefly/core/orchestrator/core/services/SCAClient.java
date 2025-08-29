package com.firefly.core.orchestrator.core.services;

import com.firefly.common.sca.sdk.api.ScaChallengeControllerApi;
import com.firefly.common.sca.sdk.api.ScaOperationControllerApi;
import com.firefly.common.sca.sdk.invoker.ApiClient;
import com.firefly.common.sca.sdk.model.SCAChallengeDTO;
import com.firefly.common.sca.sdk.model.SCAOperationDTO;
import com.firefly.common.sca.sdk.model.ValidationResultDTO;
import com.firefly.core.orchestrator.interfaces.dtos.notifications.NotificationRequest;
import com.firefly.core.orchestrator.interfaces.services.SCAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementation of the SCAService interface.
 * Provides methods for creating and validating SCA operations and challenges using the SCA API.
 */
@Service
public class SCAClient implements SCAService {

    private final ScaOperationControllerApi scaOperationApi;
    private final ScaChallengeControllerApi scaChallengeApi;

    /**
     * Creates a new SCAClient with the specified API client.
     *
     * @param apiClient the API client to use for SCA operations and challenges
     */
    @Autowired
    public SCAClient(ApiClient apiClient) {
        this.scaOperationApi = new ScaOperationControllerApi(apiClient);
        this.scaChallengeApi = new ScaChallengeControllerApi(apiClient);
    }

    /**
     * Creates a new SCA operation.
     *
     * @param notificationRequest the notification request containing recipient information
     * @return a Mono containing the response with the created SCA operation
     */
    @Override
    public Mono<ResponseEntity<SCAOperationDTO>> createOperation(NotificationRequest notificationRequest){
        String idempotencyKey = UUID.randomUUID().toString();

        // Create SCAOperationDTO from EmailRequest
        SCAOperationDTO scaOperationDTO = new SCAOperationDTO();
        scaOperationDTO.setCreatedAt(LocalDateTime.now());
        scaOperationDTO.setStatus(SCAOperationDTO.StatusEnum.PENDING);
        scaOperationDTO.setOperationType(SCAOperationDTO.OperationTypeEnum.ONBOARDING);
        scaOperationDTO.setReferenceId(UUID.randomUUID().toString());

        return scaOperationApi.createOperationWithHttpInfo(scaOperationDTO, idempotencyKey);
    }

    /**
     * Creates a new SCA challenge for an operation.
     *
     * @param idOperation the ID of the operation to create a challenge for
     * @param verificationCode the verification code to use for the challenge
     * @return a Mono containing the response with the created SCA challenge
     */
    @Override
    public Mono<ResponseEntity<SCAChallengeDTO>> createChallenge(Long idOperation, String verificationCode){
        String idempotencyKey = UUID.randomUUID().toString();
        SCAChallengeDTO challengeDTO = new SCAChallengeDTO();
        challengeDTO.setCreatedAt(LocalDateTime.now());
        challengeDTO.setChallengeCode(verificationCode);
        challengeDTO.setExpiresAt(LocalDateTime.now().plusMonths(1));
        return scaChallengeApi.createChallengeWithHttpInfo(idOperation, challengeDTO, idempotencyKey);
    }

    /**
     * Validates an SCA challenge code for an operation.
     *
     * @param idOperation the ID of the operation to validate
     * @param code the verification code to validate
     * @return a Mono containing the response with the validation result
     */
    @Override
    public Mono<ResponseEntity<ValidationResultDTO>> validateSCA(Long idOperation, String code) {
        String idempotencyKey = UUID.randomUUID().toString();
        return scaOperationApi.validateSCAWithHttpInfo(idOperation, code, idempotencyKey);
    }

}
