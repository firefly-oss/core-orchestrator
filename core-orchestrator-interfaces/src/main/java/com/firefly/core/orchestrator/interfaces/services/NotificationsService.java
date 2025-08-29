package com.firefly.core.orchestrator.interfaces.services;

import com.firefly.common.platform.notification.services.sdk.model.EmailResponseDTO;
import com.firefly.common.platform.notification.services.sdk.model.SMSResponseDTO;
import com.firefly.core.orchestrator.interfaces.dtos.notifications.CreateChallengeRequest;
import com.firefly.core.orchestrator.interfaces.dtos.notifications.SendNotificationRequest;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

/**
 * Interface for notification client operations.
 * Provides methods for sending email and SMS notifications.
 */
public interface NotificationsService {

    /**
     * Sends an email notification.
     *
     * @param verificationCode the verification code to include in the email
     * @param notificationRequest the email request data
     * @return a Mono containing the response with the email sending result
     */
    Mono<ResponseEntity<EmailResponseDTO>> sendEmail(String verificationCode, SendNotificationRequest notificationRequest);

    /**
     * Sends an SMS notification.
     *
     * @param verificationCode the verification code to include in the SMS
     * @param notificationRequest the SMS request data
     * @return a Mono containing the response with the SMS sending result
     */
    Mono<ResponseEntity<SMSResponseDTO>> sendSMS(String verificationCode, SendNotificationRequest notificationRequest);

    /**
     * Sends a verification email and creates a challenge request.
     * This method generates a verification code, sends the email, and creates a challenge request.
     *
     * @param notificationRequest the email request data
     * @return a Mono containing the challenge request with operation ID and verification code
     */
    Mono<CreateChallengeRequest> sendVerificationEmail(SendNotificationRequest notificationRequest);

    /**
     * Sends a verification SMS and creates a challenge request.
     * This method generates a verification code, sends the SMS, and creates a challenge request.
     *
     * @param notificationRequest the SMS request data
     * @return a Mono containing the challenge request with operation ID and verification code
     */
    Mono<CreateChallengeRequest> sendVerificationSMS(SendNotificationRequest notificationRequest);
}
