package com.catalis.core.orchestrator.core.services;

import com.catalis.common.platform.notification.services.sdk.api.EmailNotificationsApi;
import com.catalis.common.platform.notification.services.sdk.api.SmsNotificationsApi;
import com.catalis.common.platform.notification.services.sdk.invoker.ApiClient;
import com.catalis.common.platform.notification.services.sdk.model.EmailRequestDTO;
import com.catalis.common.platform.notification.services.sdk.model.EmailResponseDTO;
import com.catalis.common.platform.notification.services.sdk.model.SMSRequestDTO;
import com.catalis.common.platform.notification.services.sdk.model.SMSResponseDTO;
import com.catalis.core.orchestrator.interfaces.dtos.notifications.NotificationRequest;
import com.catalis.core.orchestrator.interfaces.dtos.notifications.SendNotificationRequest;
import com.catalis.core.orchestrator.interfaces.services.NotificationsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Random;
import java.util.UUID;

@Service
public class NotificationsClient implements NotificationsService {

    private final EmailNotificationsApi emailNotificationsApi;
    private final SmsNotificationsApi smsNotificationsApi;

    /**
     * Creates a new BaseApiClient with the specified API client.
     *
     * @param apiClient the API client to use
     */
    @Autowired
    public NotificationsClient(ApiClient apiClient) {
        this.emailNotificationsApi = new EmailNotificationsApi(apiClient);
        this.smsNotificationsApi = new SmsNotificationsApi(apiClient);
    }

    public Mono<ResponseEntity<EmailResponseDTO>> sendEmail(String verificationCode, SendNotificationRequest sendNotificationRequest){
        String idempotencyKey = UUID.randomUUID().toString();
        EmailRequestDTO emailRequestDTO = new EmailRequestDTO();
                emailRequestDTO.setFrom("firefly@firefly.com");
                emailRequestDTO.setTo(sendNotificationRequest.getTo());
                emailRequestDTO.setSubject("Firefly Verification Email");
                emailRequestDTO.setHtml("Your verification code is: " + verificationCode + "\n\nThank you for using Firefly!");
        return emailNotificationsApi.sendEmailWithHttpInfo(emailRequestDTO, idempotencyKey);
    }

    public Mono<ResponseEntity<SMSResponseDTO>> sendSMS(String verificationCode, SendNotificationRequest sendNotificationRequest){
        String idempotencyKey = UUID.randomUUID().toString();
        SMSRequestDTO smsRequestDTO = new SMSRequestDTO();
        smsRequestDTO.setMessage("Your verification code is: " + verificationCode + "\n\nThank you for using Firefly!");
        smsRequestDTO.setPhoneNumber(sendNotificationRequest.getTo());
        return smsNotificationsApi.sendSMSWithHttpInfo(smsRequestDTO, idempotencyKey);
    }

}
