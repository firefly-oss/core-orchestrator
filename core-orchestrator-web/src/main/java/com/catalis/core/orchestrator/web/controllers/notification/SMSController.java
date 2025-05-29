package com.catalis.core.orchestrator.web.controllers.notification;

import com.catalis.core.orchestrator.interfaces.dtos.notifications.NotificationRequest;
import com.catalis.core.orchestrator.interfaces.dtos.notifications.SMSRequest;
import com.catalis.core.orchestrator.interfaces.dtos.notifications.ValidateCodeRequest;
import com.catalis.core.orchestrator.web.controllers.BaseController;
import io.camunda.zeebe.client.ZeebeClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * REST controller that handles SMS notification-related API endpoints.
 * Provides endpoints for sending verification SMS.
 */
@RestController
@RequestMapping("/api/v1/notifications/sms")
@Slf4j
@Tag(name = "SMS Notifications", description = "API endpoints for SMS notification operations")
public class SMSController extends BaseController {

    /**
     * Constructs a new SMSController with the specified Zeebe client.
     *
     * @param zeebeClient The client used to interact with the Camunda Zeebe workflow engine
     */
    @Autowired
    public SMSController(ZeebeClient zeebeClient) {
        super(zeebeClient);
    }

    /**
     * Starts a process to send a verification SMS.
     *
     * @param notificationRequest The notification data to be processed
     * @return A response containing the process instance key and status
     */
    @Operation(
        operationId = "sendVerificationSMS",
        summary = "Send verification SMS",
        description = "Starts a process to send a verification SMS to the specified recipient"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Process started successfully",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(mediaType = "application/json")
        )
    })
    @PostMapping(value = "/send-verification")
    public ResponseEntity<Map<String, Object>> startSendVerificationSMSProcess(
        @Parameter(description = "SMS notification request details") 
        @RequestBody NotificationRequest notificationRequest) {
        log.info("Starting send-verification-sms process with phone number: {}", notificationRequest.to());

        try {
            return startProcess(SEND_VERIFICATION_SMS, notificationRequest);
        } catch (Exception e) {
            log.error("Error starting process: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to start process", "message", e.getMessage()));
        }
    }

    /**
     * Starts a process to validate a verification code.
     *
     * @param validateCodeRequest The validation data containing operation ID and verification code
     * @return A response containing the process instance key and status
     */
    @Operation(
        operationId = "validateSMSVerificationCode",
        summary = "Validate SMS verification code",
        description = "Starts a process to validate a verification code sent via SMS"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Validation process started successfully",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(mediaType = "application/json")
        )
    })
    @PostMapping(value = "/validate-code")
    public ResponseEntity<Map<String, Object>> validateCode(
        @Parameter(description = "Verification code validation request details") 
        @RequestBody ValidateCodeRequest validateCodeRequest) {
        log.info("Starting validate-verification-email process for operation ID: {}", validateCodeRequest.idOperation());

        try {
            return startProcess(VALIDATE_VERIFICATION_CODE, validateCodeRequest);
        } catch (Exception e) {
            log.error("Error starting validation process: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to start validation process", "message", e.getMessage()));
        }
    }
}
