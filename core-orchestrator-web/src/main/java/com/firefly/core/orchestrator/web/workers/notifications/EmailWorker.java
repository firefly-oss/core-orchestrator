package com.firefly.core.orchestrator.web.workers.notifications;

import com.firefly.core.orchestrator.interfaces.dtos.notifications.CreateChallengeRequest;
import com.firefly.core.orchestrator.interfaces.dtos.notifications.SendNotificationRequest;
import com.firefly.core.orchestrator.interfaces.services.NotificationsService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

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
     * This worker delegates to the NotificationsService to send the email and create a challenge request.
     *
     * @param job The activated job containing the email data
     * @return A Mono containing the CreateChallengeRequest with operation ID and verification code
     */
    @JobWorker(type = "send-verification-email-task")
    public Mono<CreateChallengeRequest> sendVerificationEmail(final ActivatedJob job) {
        log.info("Executing send-verification-email-task for job: {}", job.getKey());

        // Get variables from the process
        SendNotificationRequest sendNotificationRequest = job.getVariablesAsType(SendNotificationRequest.class);

        log.info("Delegating verification email sending to: {}", sendNotificationRequest.getTo());

        // Delegate to the notifications service
        return notificationsService.sendVerificationEmail(sendNotificationRequest);
    }

}
