package com.catalis.core.orchestrator.interfaces.services;

import com.catalis.common.sca.sdk.model.SCAChallengeDTO;
import com.catalis.common.sca.sdk.model.SCAOperationDTO;
import com.catalis.common.sca.sdk.model.ValidationResultDTO;
import com.catalis.core.orchestrator.interfaces.dtos.notifications.EmailRequest;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

/**
 * Interface for SCA (Strong Customer Authentication) client operations.
 * Provides methods for creating operations, challenges, and validating challenges.
 */
public interface SCAService {

    /**
     * Creates a new SCA operation.
     *
     * @param emailRequest the email request data used to create the operation
     * @return a Mono containing the response with the created operation
     */
    Mono<ResponseEntity<SCAOperationDTO>> createOperation(EmailRequest emailRequest);

    /**
     * Creates a challenge for a given operation ID.
     *
     * @param operationId      the ID of the operation to create a challenge for
     * @param verificationCode the verificationCode
     * @return a Mono containing the response with the created challenge
     */
    Mono<ResponseEntity<SCAChallengeDTO>> createChallenge(String operationId, String verificationCode);

    /**
     * Validates a challenge for a given operation ID and verification code.
     *
     * @param operationId the ID of the operation to validate
     * @param code the verification code to validate
     * @return a Mono containing the response with the validation result
     */
    Mono<ResponseEntity<ValidationResultDTO>> validateSCA(Long operationId, String code);
}
