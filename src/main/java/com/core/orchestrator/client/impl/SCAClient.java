package com.core.orchestrator.client.impl;

import com.catalis.common.sca.sdk.api.ScaChallengeControllerApi;
import com.catalis.common.sca.sdk.api.ScaOperationControllerApi;
import com.catalis.common.sca.sdk.invoker.ApiClient;
import com.catalis.common.sca.sdk.model.SCAChallengeDTO;
import com.catalis.common.sca.sdk.model.SCAOperationDTO;
import com.core.orchestrator.client.SCAService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

public class SCAClient implements SCAService {

    private final ScaOperationControllerApi scaOperationApi;
    private final ScaChallengeControllerApi scaChallengeApi;
    private final ObjectMapper objectMapper;

    /**
     * Creates a new BaseApiClient with the specified API client.
     *
     * @param apiClient the API client to use
     */
    @Autowired
    public SCAClient(ApiClient apiClient, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.scaOperationApi = new ScaOperationControllerApi(apiClient);
        this.scaChallengeApi = new ScaChallengeControllerApi(apiClient);
    }

    public Mono<ResponseEntity<SCAOperationDTO>> createOperation(SCAOperationDTO operationDTO){
        String idempotencyKey = UUID.randomUUID().toString();
        return scaOperationApi.createOperationWithHttpInfo(operationDTO, idempotencyKey);
    }

    public Mono<ResponseEntity<SCAChallengeDTO>> createChallenge(String operationId, String verificationCode){
        String idempotencyKey = UUID.randomUUID().toString();
        SCAChallengeDTO challengeDTO = new SCAChallengeDTO();
        challengeDTO.setCreatedAt(LocalDateTime.now());
        challengeDTO.setChallengeCode(verificationCode);
        Long operationIdLong = Long.parseLong(operationId);
        return scaChallengeApi.createChallengeWithHttpInfo(operationIdLong, challengeDTO, idempotencyKey);
    }


}
