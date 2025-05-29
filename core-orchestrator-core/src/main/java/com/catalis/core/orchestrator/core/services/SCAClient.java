package com.catalis.core.orchestrator.core.services;

import com.catalis.common.sca.sdk.api.ScaChallengeControllerApi;
import com.catalis.common.sca.sdk.api.ScaOperationControllerApi;
import com.catalis.common.sca.sdk.invoker.ApiClient;
import com.catalis.common.sca.sdk.model.SCAChallengeDTO;
import com.catalis.common.sca.sdk.model.SCAOperationDTO;
import com.catalis.common.sca.sdk.model.ValidationResultDTO;
import com.catalis.core.orchestrator.interfaces.dtos.notifications.NotificationRequest;
import com.catalis.core.orchestrator.interfaces.services.SCAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SCAClient implements SCAService {

    private final ScaOperationControllerApi scaOperationApi;
    private final ScaChallengeControllerApi scaChallengeApi;

    /**
     * Creates a new BaseApiClient with the specified API client.
     *
     * @param apiClient the API client to use
     */
    @Autowired
    public SCAClient(ApiClient apiClient) {
        this.scaOperationApi = new ScaOperationControllerApi(apiClient);
        this.scaChallengeApi = new ScaChallengeControllerApi(apiClient);
    }

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

    public Mono<ResponseEntity<SCAChallengeDTO>> createChallenge(Long idOperation, String verificationCode){
        String idempotencyKey = UUID.randomUUID().toString();
        SCAChallengeDTO challengeDTO = new SCAChallengeDTO();
        challengeDTO.setCreatedAt(LocalDateTime.now());
        challengeDTO.setChallengeCode(verificationCode);
        challengeDTO.setExpiresAt(LocalDateTime.now().plusMonths(1));
        return scaChallengeApi.createChallengeWithHttpInfo(idOperation, challengeDTO, idempotencyKey);
    }

    @Override
    public Mono<ResponseEntity<ValidationResultDTO>> validateSCA(Long idOperation, String code) {
        String idempotencyKey = UUID.randomUUID().toString();
        return scaOperationApi.validateSCAWithHttpInfo(idOperation, code, idempotencyKey);
    }

}
