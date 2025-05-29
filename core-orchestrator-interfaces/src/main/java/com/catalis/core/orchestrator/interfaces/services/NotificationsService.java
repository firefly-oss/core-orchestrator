package com.catalis.core.orchestrator.interfaces.services;

import com.catalis.common.platform.notification.services.sdk.model.EmailRequestDTO;
import com.catalis.common.platform.notification.services.sdk.model.EmailResponseDTO;
import com.catalis.common.platform.notification.services.sdk.model.SMSRequestDTO;
import com.catalis.common.platform.notification.services.sdk.model.SMSResponseDTO;
import com.catalis.core.orchestrator.interfaces.dtos.notifications.NotificationRequest;
import com.catalis.core.orchestrator.interfaces.dtos.notifications.SendNotificationRequest;
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
     * @param notificationRequest the email request data
     * @return a Mono containing the response with the email sending result
     */
    Mono<ResponseEntity<EmailResponseDTO>> sendEmail(String verificationCode, SendNotificationRequest notificationRequest);

    /**
     * Sends an SMS notification.
     *
     * @param notificationRequest the SMS request data
     * @return a Mono containing the response with the SMS sending result
     */
    Mono<ResponseEntity<SMSResponseDTO>> sendSMS(String verificationCode, SendNotificationRequest notificationRequest);
}
