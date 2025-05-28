package com.core.orchestrator.client.impl;

import com.catalis.common.platform.notification.services.sdk.api.EmailNotificationsApi;
import com.catalis.common.platform.notification.services.sdk.api.SmsNotificationsApi;
import com.catalis.common.platform.notification.services.sdk.invoker.ApiClient;
import com.catalis.common.platform.notification.services.sdk.model.EmailRequestDTO;
import com.catalis.common.platform.notification.services.sdk.model.EmailResponseDTO;
import com.catalis.common.platform.notification.services.sdk.model.SMSRequestDTO;
import com.catalis.common.platform.notification.services.sdk.model.SMSResponseDTO;
import com.core.orchestrator.client.NotificationsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class NotificationsClient implements NotificationsService {

    private final EmailNotificationsApi emailNotificationsApi;
    private final SmsNotificationsApi smsNotificationsApi;
    private final ObjectMapper objectMapper;

    /**
     * Creates a new BaseApiClient with the specified API client.
     *
     * @param apiClient the API client to use
     */
    @Autowired
    public NotificationsClient(ApiClient apiClient, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.emailNotificationsApi = new EmailNotificationsApi(apiClient);
        this.smsNotificationsApi = new SmsNotificationsApi(apiClient);
    }

    public Mono<ResponseEntity<EmailResponseDTO>> sendEmail(EmailRequestDTO emailRequestDTO){
        String idempotencyKey = UUID.randomUUID().toString();
        return emailNotificationsApi.sendEmailWithHttpInfo(emailRequestDTO, idempotencyKey);
    }

    public Mono<ResponseEntity<SMSResponseDTO>> sendSMS(SMSRequestDTO smsRequestDTO){
        String idempotencyKey = UUID.randomUUID().toString();
        return smsNotificationsApi.sendSMSWithHttpInfo(smsRequestDTO, idempotencyKey);
    }

}
