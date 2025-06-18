package com.catalis.core.orchestrator.web.workers.notifications;

import com.catalis.core.orchestrator.interfaces.dtos.notifications.CreateChallengeRequest;
import com.catalis.core.orchestrator.interfaces.dtos.notifications.SendNotificationRequest;
import com.catalis.core.orchestrator.interfaces.services.NotificationsService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Worker component that handles SMS notification-related tasks in Camunda Zeebe workflows.
 * Provides job workers for sending verification SMS and creating SCA operations and challenges.
 * Uses NotificationsService to send SMS messages.
 */
@Component
@Slf4j
public class SMSWorker {

    private final NotificationsService notificationsService;

    @Autowired
    public SMSWorker(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    /**
     * Job worker that handles sending verification SMS.
     * This worker delegates to the NotificationsService to send the SMS and create a challenge request.
     *
     * @param job The activated job containing the SMS data
     * @return A Mono containing the CreateChallengeRequest with operation ID and verification code
     */
    @JobWorker(type = "send-verification-sms-task")
    public Mono<CreateChallengeRequest> sendVerificationSMS(final ActivatedJob job) {
        log.info("Executing send-verification-sms-task for job: {}", job.getKey());

        // Get variables from the process
        SendNotificationRequest sendNotificationRequest = job.getVariablesAsType(SendNotificationRequest.class);

        log.info("Delegating verification SMS sending to: {}", sendNotificationRequest.getTo());

        // Delegate to the notifications service
        return notificationsService.sendVerificationSMS(sendNotificationRequest);
    }

}
