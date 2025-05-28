package com.core.orchestrator.controller;

import com.catalis.common.platform.notification.services.sdk.model.EmailRequestDTO;
import io.camunda.zeebe.client.ZeebeClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller that handles notification-related API endpoints.
 * Provides endpoints for sending verification emails.
 */
@RestController
@RequestMapping("/api/v1/notifications")
@Slf4j
public class NotificationController extends BaseController {

    /**
     * Constructs a new NotificationController with the specified Zeebe client.
     *
     * @param zeebeClient The client used to interact with the Camunda Zeebe workflow engine
     */
    @Autowired
    public NotificationController(ZeebeClient zeebeClient) {
        super(zeebeClient);
    }

    /**
     * Starts a process to send a verification email.
     *
     * @param emailRequest The email data to be processed
     * @return A response containing the process instance key and status
     */
    @PostMapping(value = "/send-verification-email")
    public ResponseEntity<Map<String, Object>> startSendVerificationEmailProcess(@RequestBody EmailRequestDTO emailRequest) {
        log.info("Starting send-verification-email process with email: {}", emailRequest.getTo());

        try {
            return startProcess(SEND_VERIFICATION_EMAIL, emailRequest);
        } catch (Exception e) {
            log.error("Error starting process: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to start process", "message", e.getMessage()));
        }
    }
}
