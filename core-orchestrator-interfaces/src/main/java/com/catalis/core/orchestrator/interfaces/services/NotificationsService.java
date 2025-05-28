package com.catalis.core.orchestrator.interfaces.services;

import com.catalis.common.platform.notification.services.sdk.model.EmailRequestDTO;
import com.catalis.common.platform.notification.services.sdk.model.EmailResponseDTO;
import com.catalis.common.platform.notification.services.sdk.model.SMSRequestDTO;
import com.catalis.common.platform.notification.services.sdk.model.SMSResponseDTO;
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
     * @param emailRequestDTO the email request data
     * @return a Mono containing the response with the email sending result
     */
    Mono<ResponseEntity<EmailResponseDTO>> sendEmail(EmailRequestDTO emailRequestDTO);

    /**
     * Sends an SMS notification.
     *
     * @param smsRequestDTO the SMS request data
     * @return a Mono containing the response with the SMS sending result
     */
    Mono<ResponseEntity<SMSResponseDTO>> sendSMS(SMSRequestDTO smsRequestDTO);
}
