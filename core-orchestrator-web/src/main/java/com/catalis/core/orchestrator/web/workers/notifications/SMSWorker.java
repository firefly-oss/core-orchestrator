package com.catalis.core.orchestrator.web.workers.notifications;

import com.catalis.common.platform.notification.services.sdk.model.SMSResponseDTO;
import com.catalis.common.sca.sdk.model.SCAChallengeDTO;
import com.catalis.common.sca.sdk.model.SCAOperationDTO;
import com.catalis.core.orchestrator.interfaces.dtos.notifications.CreateChallengeRequest;
import com.catalis.core.orchestrator.interfaces.dtos.notifications.NotificationRequest;
import com.catalis.core.orchestrator.interfaces.dtos.notifications.SMSRequest;
import com.catalis.core.orchestrator.interfaces.dtos.notifications.SendNotificationRequest;
import com.catalis.core.orchestrator.interfaces.mappers.SMSMapper;
import com.catalis.core.orchestrator.interfaces.services.NotificationsService;
import com.catalis.core.orchestrator.interfaces.services.SCAService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Worker component that handles SMS notification-related tasks in Camunda Zeebe workflows.
 * Provides job workers for sending verification SMS and creating SCA operations and challenges.
 */
@Component
@Slf4j
public class SMSWorker {

    private static final String SMS_ID = "smsId";

    private final NotificationsService notificationsService;

    @Autowired
    public SMSWorker(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    /**
     * Job worker that handles sending verification SMS.
     * This worker sends an SMS with a verification code.
     *
     * @param job The activated job containing the SMS data
     * @return A map containing the SMS response
     */
    @JobWorker(type = "send-verification-sms-task")
    public CreateChallengeRequest sendVerificationSMS(final ActivatedJob job) {
        log.info("Executing send-verification-sms-task for job: {}", job.getKey());

        // Get variables from the process
        SendNotificationRequest sendNotificationRequest = job.getVariablesAsType(SendNotificationRequest.class);

        log.info("Sending verification SMS to: {}", sendNotificationRequest.getTo());

        // WebClient call
        String verificationCode = String.format("%06d", new Random().nextInt(1000000));
        Mono<ResponseEntity<SMSResponseDTO>> responseMono = notificationsService.sendSMS(verificationCode, sendNotificationRequest);

        // Get the response
        ResponseEntity<SMSResponseDTO> response = responseMono.block();
        SMSResponseDTO smsResponse = response.getBody();

        log.info("SMS sent successfully with ID: {}", smsResponse.getMessageId());

        // Prepare result for the process
        CreateChallengeRequest createChallengeRequest = new CreateChallengeRequest();
        createChallengeRequest.setIdOperation(sendNotificationRequest.getIdOperation());
        createChallengeRequest.setVerificationCode(verificationCode);

        return createChallengeRequest;
    }

}
