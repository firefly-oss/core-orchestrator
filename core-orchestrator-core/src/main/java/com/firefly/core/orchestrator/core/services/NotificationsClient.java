package com.firefly.core.orchestrator.core.services;

import com.firefly.common.platform.notification.services.sdk.api.EmailNotificationsApi;
import com.firefly.common.platform.notification.services.sdk.api.SmsNotificationsApi;
import com.firefly.common.platform.notification.services.sdk.invoker.ApiClient;
import com.firefly.common.platform.notification.services.sdk.model.EmailRequestDTO;
import com.firefly.common.platform.notification.services.sdk.model.EmailResponseDTO;
import com.firefly.common.platform.notification.services.sdk.model.SMSRequestDTO;
import com.firefly.common.platform.notification.services.sdk.model.SMSResponseDTO;
import com.firefly.core.orchestrator.interfaces.dtos.notifications.CreateChallengeRequest;
import com.firefly.core.orchestrator.interfaces.dtos.notifications.SendNotificationRequest;
import com.firefly.core.orchestrator.interfaces.services.NotificationsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Random;
import java.util.UUID;

/**
 * Implementation of the NotificationsService interface.
 * Provides methods for sending email and SMS notifications using the Notification Services API.
 */
@Service
@Slf4j
public class NotificationsClient implements NotificationsService {

    private final EmailNotificationsApi emailNotificationsApi;
    private final SmsNotificationsApi smsNotificationsApi;

    /**
     * Creates a new NotificationsClient with the specified API client.
     *
     * @param apiClient the API client to use for email and SMS notifications
     */
    @Autowired
    public NotificationsClient(ApiClient apiClient) {
        this.emailNotificationsApi = new EmailNotificationsApi(apiClient);
        this.smsNotificationsApi = new SmsNotificationsApi(apiClient);
    }

    /**
     * Sends an email notification.
     *
     * @param verificationCode the verification code to include in the email
     * @param sendNotificationRequest the email request data
     * @return a Mono containing the response with the email sending result
     */
    @Override
    public Mono<ResponseEntity<EmailResponseDTO>> sendEmail(String verificationCode, SendNotificationRequest sendNotificationRequest){
        String idempotencyKey = UUID.randomUUID().toString();
        EmailRequestDTO emailRequestDTO = new EmailRequestDTO();
                emailRequestDTO.setFrom("firefly@firefly.com");
                emailRequestDTO.setTo(sendNotificationRequest.getTo());
                emailRequestDTO.setSubject("Firefly Verification Email");
                emailRequestDTO.setHtml("Your verification code is: " + verificationCode + "\n\nThank you for using Firefly!");
        return emailNotificationsApi.sendEmailWithHttpInfo(emailRequestDTO, idempotencyKey);
    }

    /**
     * Sends an SMS notification.
     *
     * @param verificationCode the verification code to include in the SMS
     * @param sendNotificationRequest the SMS request data
     * @return a Mono containing the response with the SMS sending result
     */
    @Override
    public Mono<ResponseEntity<SMSResponseDTO>> sendSMS(String verificationCode, SendNotificationRequest sendNotificationRequest){
        String idempotencyKey = UUID.randomUUID().toString();
        SMSRequestDTO smsRequestDTO = new SMSRequestDTO();
        smsRequestDTO.setMessage("Your verification code is: " + verificationCode + "\n\nThank you for using Firefly!");
        smsRequestDTO.setPhoneNumber(sendNotificationRequest.getTo());
        return smsNotificationsApi.sendSMSWithHttpInfo(smsRequestDTO, idempotencyKey);
    }

    /**
     * Sends a verification email and creates a challenge request.
     * This method generates a verification code, sends the email, and creates a challenge request.
     *
     * @param notificationRequest the email request data
     * @return a Mono containing the challenge request with operation ID and verification code
     */
    @Override
    public Mono<CreateChallengeRequest> sendVerificationEmail(SendNotificationRequest notificationRequest) {
        log.info("Sending verification email to: {}", notificationRequest.getTo());

        // Generate verification code
        String verificationCode = generateVerificationCode();

        // Send email
        return sendEmail(verificationCode, notificationRequest)
                .map(response -> {
                    EmailResponseDTO emailResponse = response.getBody();
                    log.info("Email sent successfully with ID: {}", emailResponse.getMessageId());

                    // Create challenge request
                    CreateChallengeRequest createChallengeRequest = new CreateChallengeRequest();
                    createChallengeRequest.setIdOperation(notificationRequest.getIdOperation());
                    createChallengeRequest.setVerificationCode(verificationCode);

                    return createChallengeRequest;
                });
    }

    /**
     * Sends a verification SMS and creates a challenge request.
     * This method generates a verification code, sends the SMS, and creates a challenge request.
     *
     * @param notificationRequest the SMS request data
     * @return a Mono containing the challenge request with operation ID and verification code
     */
    @Override
    public Mono<CreateChallengeRequest> sendVerificationSMS(SendNotificationRequest notificationRequest) {
        log.info("Sending verification SMS to: {}", notificationRequest.getTo());

        // Generate verification code
        String verificationCode = generateVerificationCode();

        // Send SMS
        return sendSMS(verificationCode, notificationRequest)
                .map(response -> {
                    SMSResponseDTO smsResponse = response.getBody();
                    log.info("SMS sent successfully with ID: {}", smsResponse.getMessageId());

                    // Create challenge request
                    CreateChallengeRequest createChallengeRequest = new CreateChallengeRequest();
                    createChallengeRequest.setIdOperation(notificationRequest.getIdOperation());
                    createChallengeRequest.setVerificationCode(verificationCode);

                    return createChallengeRequest;
                });
    }

    /**
     * Generates a random 6-digit verification code.
     *
     * @return the generated verification code
     */
    private String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }
}
