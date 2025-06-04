package com.catalis.core.orchestrator.web.workers.notifications;

import com.catalis.common.platform.notification.services.sdk.model.EmailResponseDTO;
import com.catalis.core.orchestrator.interfaces.dtos.notifications.CreateChallengeRequest;
import com.catalis.core.orchestrator.interfaces.dtos.notifications.SendNotificationRequest;
import com.catalis.core.orchestrator.interfaces.services.NotificationsService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Random;

/**
 * Worker component that handles email notification-related tasks in Camunda Zeebe workflows.
 * Provides job workers for sending verification emails and creating SCA operations and challenges.
 * Uses NotificationsService to send email messages.
 */
@Component
@Slf4j
public class EmailWorker {

    private final NotificationsService notificationsService;

    @Autowired
    public EmailWorker(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    /**
     * Job worker that handles sending verification emails.
     * This is a mocked implementation that simulates sending an email.
     *
     * @param job The activated job containing the email data
     * @return A CreateChallengeRequest containing the operation ID and verification code
     */
    @JobWorker(type = "send-verification-email-task")
    public CreateChallengeRequest sendVerificationEmail(final ActivatedJob job) {
        log.info("Executing send-verification-email-task for job: {}", job.getKey());

        // Get variables from the process
        SendNotificationRequest sendNotificationRequest = job.getVariablesAsType(SendNotificationRequest.class);

        log.info("Sending verification email to: {}", sendNotificationRequest.getTo());

        // WebClient call
        String verificationCode = String.format("%06d", new Random().nextInt(1000000));
        Mono<ResponseEntity<EmailResponseDTO>> responseMono = notificationsService.sendEmail(verificationCode, sendNotificationRequest);

        // Get the response
        ResponseEntity<EmailResponseDTO> response = responseMono.block();
        EmailResponseDTO emailResponse = response.getBody();

        log.info("Email sent successfully with ID: {}", emailResponse.getMessageId());

        // Prepare result for the process
        CreateChallengeRequest createChallengeRequest = new CreateChallengeRequest();
        createChallengeRequest.setIdOperation(sendNotificationRequest.getIdOperation());
        createChallengeRequest.setVerificationCode(verificationCode);

        return createChallengeRequest;
    }

}
