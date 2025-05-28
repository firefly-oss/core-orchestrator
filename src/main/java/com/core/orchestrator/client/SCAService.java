package com.core.orchestrator.client;

import com.catalis.common.sca.sdk.model.SCAChallengeDTO;
import com.catalis.common.sca.sdk.model.SCAOperationDTO;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

/**
 * Interface for SCA (Strong Customer Authentication) client operations.
 * Provides methods for creating operations and challenges.
 */
public interface SCAService {

    /**
     * Creates a new SCA operation.
     *
     * @param operationDTO the operation data to create
     * @return a Mono containing the response with the created operation
     */
    Mono<ResponseEntity<SCAOperationDTO>> createOperation(SCAOperationDTO operationDTO);

    /**
     * Creates a challenge for a given operation ID.
     *
     * @param operationId      the ID of the operation to create a challenge for
     * @param verificationCode the verificationCode
     * @return a Mono containing the response with the created challenge
     */
    Mono<ResponseEntity<SCAChallengeDTO>> createChallenge(String operationId, String verificationCode);
}