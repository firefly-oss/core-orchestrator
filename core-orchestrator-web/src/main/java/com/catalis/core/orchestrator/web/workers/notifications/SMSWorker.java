package com.catalis.core.orchestrator.web.workers.notifications;

import com.catalis.common.platform.notification.services.sdk.model.SMSResponseDTO;
import com.catalis.common.sca.sdk.model.SCAChallengeDTO;
import com.catalis.common.sca.sdk.model.SCAOperationDTO;
import com.catalis.core.orchestrator.interfaces.dtos.notifications.EmailRequest;
import com.catalis.core.orchestrator.interfaces.dtos.notifications.SMSRequest;
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
    private static final String OPERATION_ID = "operationId";
    private static final String CHALLENGE_ID = "challengeId";

    private final SCAService scaService;
    private final NotificationsService notificationsService;
    private final SMSMapper smsMapper;

    @Autowired
    public SMSWorker(SCAService scaService, NotificationsService notificationsService, 
                    SMSMapper smsMapper) {
        this.scaService = scaService;
        this.notificationsService = notificationsService;
        this.smsMapper = smsMapper;
    }

    /**
     * Job worker that handles creating SCA operations for SMS verification.
     * This is a mocked implementation that simulates creating an SCA operation.
     *
     * @param job The activated job containing the SMS data
     * @return A map containing the SCA operation response
     */
    @JobWorker(type = "create-sca-operation-task")
    public Map<String, Object> createSCAOperation(final ActivatedJob job) {
        log.info("Executing create-sca-operation-task for job: {}", job.getKey());

        // Get variables from the process
        Map<String, Object> variables = job.getVariablesAsMap();
        SMSRequest smsData = job.getVariablesAsType(SMSRequest.class);
        log.info("Creating SCA operation for phone number: {}", smsData.to());

        // Convert SMSRequest to EmailRequest since SCAService only accepts EmailRequest
        EmailRequest emailRequest = EmailRequest.builder()
                .from(smsData.from())
                .to(smsData.to())
                .subject("SMS Verification")
                .html(smsData.message())
                .build();

        // WebClient call with converted EmailRequest
        Mono<ResponseEntity<SCAOperationDTO>> responseMono = scaService.createOperation(emailRequest);

        // Get the response
        ResponseEntity<SCAOperationDTO> response = responseMono.block();
        SCAOperationDTO scaOperation = response.getBody();

        log.info("SCA operation created successfully with ID: {}", scaOperation.getId());

        // Prepare result for the process
        Map<String, Object> result = new HashMap<>(variables);
        result.put(OPERATION_ID, scaOperation.getId());
        result.put("email", smsData.to()); // Keeping the email field for consistency

        return result;
    }

    /**
     * Job worker that handles sending verification SMS.
     * This worker sends an SMS with a verification code.
     *
     * @param job The activated job containing the SMS data
     * @return A map containing the SMS response
     */
    @JobWorker(type = "send-verification-sms-task")
    public Map<String, Object> sendVerificationSMS(final ActivatedJob job) {
        log.info("Executing send-verification-sms-task for job: {}", job.getKey());

        // Get variables from the process
        Map<String, Object> variables = job.getVariablesAsMap();
        String phoneNumber = (String) variables.get("email"); // Reusing the email field for phone number

        log.info("Sending verification SMS to: {}", phoneNumber);

        // WebClient call
        String verificationCode = String.format("%06d", new Random().nextInt(1000000));
        SMSRequest smsRequest = SMSRequest.builder()
                .from("Firefly")
                .to(phoneNumber)
                .message("Your Firefly verification code is: " + verificationCode + ". Thank you for using Firefly!")
                .build();
        Mono<ResponseEntity<SMSResponseDTO>> responseMono = notificationsService.sendSMS(smsMapper.requestToDTO(smsRequest));

        // Get the response
        ResponseEntity<SMSResponseDTO> response = responseMono.block();
        SMSResponseDTO smsResponse = response.getBody();

        log.info("SMS sent successfully with ID: {}", smsResponse.getMessageId());

        // Prepare result for the process
        Map<String, Object> result = new HashMap<>();
        result.put(SMS_ID, smsResponse.getMessageId());
        result.put("email", phoneNumber); // Keeping the email field for consistency
        result.put("smsStatus", smsResponse.getStatus());
        result.put("verificationCode", verificationCode);

        return result;
    }

    /**
     * Job worker that handles creating SCA challenges.
     * This is a mocked implementation that simulates creating an SCA challenge.
     *
     * @param job The activated job containing the SMS data, SMS ID, and operation ID
     * @return A map containing the SCA challenge response
     */
    @JobWorker(type = "create-sca-challenge-task")
    public Map<String, Object> createSCAChallenge(final ActivatedJob job) {
        log.info("Executing create-sca-challenge-task for job: {}", job.getKey());

        // Get variables from the process
        Map<String, Object> variables = job.getVariablesAsMap();
        String operationId = (String) variables.get(OPERATION_ID);
        String verificationCode = (String) variables.get("verificationCode");

        log.info("Creating SCA challenge for operation ID: {}", operationId);

        // WebClient call
        Mono<ResponseEntity<SCAChallengeDTO>> responseMono = scaService.createChallenge(operationId, verificationCode);

        // Get the response
        ResponseEntity<SCAChallengeDTO> response = responseMono.block();
        SCAChallengeDTO scaChallenge = response.getBody();

        log.info("SCA challenge created successfully with ID: {}", scaChallenge.getId());

        // Prepare result for the process
        Map<String, Object> result = new HashMap<>(variables);
        result.put(CHALLENGE_ID, scaChallenge.getId());
        result.put("challengeCode", scaChallenge.getChallengeCode());

        return result;
    }
}
