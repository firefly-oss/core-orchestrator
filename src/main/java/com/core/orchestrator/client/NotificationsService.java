package com.core.orchestrator.client;

import com.catalis.common.platform.notification.services.sdk.model.EmailRequestDTO;
import com.catalis.common.platform.notification.services.sdk.model.EmailResponseDTO;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

/**
 * Interface for notification client operations.
 * Provides methods for sending email notifications.
 */
public interface NotificationsService {

    /**
     * Sends an email notification.
     *
     * @param emailRequestDTO the email request data
     * @return a Mono containing the response with the email sending result
     */
    Mono<ResponseEntity<EmailResponseDTO>> sendEmail(EmailRequestDTO emailRequestDTO);
}