package com.firefly.core.orchestrator.interfaces.services;

import com.firefly.core.orchestrator.interfaces.dtos.notifications.*;
import reactor.core.publisher.Mono;

/**
 * Interface for notification SCA (Strong Customer Authentication) operations.
 * Provides higher-level methods for creating operations, challenges, and validating challenges.
 */
public interface NotificationSCAService {

    /**
     * Creates a new SCA operation and prepares a notification request.
     *
     * @param notificationRequest the notification request data used to create the operation
     * @return a Mono containing the SendNotificationRequest with operation ID and recipient
     */
    Mono<SendNotificationRequest> createSCAOperation(NotificationRequest notificationRequest);

    /**
     * Creates a challenge for a given operation ID and verification code.
     *
     * @param createChallengeRequest the request containing operation ID and verification code
     * @return a Mono containing the SendNotificationResponse with operation ID
     */
    Mono<SendNotificationResponse> createSCAChallenge(CreateChallengeRequest createChallengeRequest);

    /**
     * Validates a challenge for a given operation ID and verification code.
     *
     * @param validateCodeRequest the request containing operation ID and verification code
     * @return a Mono containing the ValidateSCAResponse with validation status and operation ID
     */
    Mono<ValidateSCAResponse> validateSCAChallenge(ValidateCodeRequest validateCodeRequest);
}
