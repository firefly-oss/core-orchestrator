package com.core.orchestrator.worker;

import com.catalis.common.platform.notification.services.sdk.model.EmailRequestDTO;
import com.catalis.common.platform.notification.services.sdk.model.EmailResponseDTO;
import com.catalis.common.sca.sdk.model.SCAChallengeDTO;
import com.catalis.common.sca.sdk.model.SCAOperationDTO;
import com.core.orchestrator.client.NotificationsService;
import com.core.orchestrator.client.SCAService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Worker component that handles notification-related tasks in Camunda Zeebe workflows.
 * Provides job workers for sending verification emails and creating SCA operations and challenges.
 */
@Component
@Slf4j
public class NotificationWorker {

    private static final String EMAIL_ID = "emailId";
    private static final String OPERATION_ID = "operationId";
    private static final String CHALLENGE_ID = "challengeId";

    private final SCAService scaService;
    private final NotificationsService notificationsService;

    @Autowired
    public NotificationWorker(SCAService scaService, NotificationsService notificationsService) {
        this.scaService = scaService;
        this.notificationsService = notificationsService;
    }


    /**
     * Job worker that handles creating SCA operations.
     * This is a mocked implementation that simulates creating an SCA operation.
     *
     * @param job The activated job containing the email data and email ID
     * @return A map containing the SCA operation response
     */
    @JobWorker(type = "create-sca-operation-task")
    public Map<String, Object> createSCAOperation(final ActivatedJob job) {
        log.info("Executing create-sca-operation-task for job: {}", job.getKey());

        // Get variables from the process
        Map<String, Object> variables = job.getVariablesAsMap();
        String email = (String) variables.get("to");

        log.info("Creating SCA operation for email: {}", email);

        // WebClient call
        SCAOperationDTO scaOperationDTO = new SCAOperationDTO();
        scaOperationDTO.setCreatedAt(LocalDateTime.now());
        scaOperationDTO.setStatus(SCAOperationDTO.StatusEnum.PENDING);
        scaOperationDTO.setOperationType(SCAOperationDTO.OperationTypeEnum.ONBOARDING);
        scaOperationDTO.setReferenceId(UUID.randomUUID().toString());
        Mono<ResponseEntity<SCAOperationDTO>> responseMono = scaService.createOperation(scaOperationDTO);

        // Get the response
        ResponseEntity<SCAOperationDTO> response = responseMono.block();
        SCAOperationDTO scaOperation = response.getBody();

        log.info("SCA operation created successfully with ID: {}", scaOperation.getId());

        // Prepare result for the process
        Map<String, Object> result = new HashMap<>(variables);
        result.put(OPERATION_ID, scaOperation.getId());
        result.put("email", email);

        return result;
    }

    /**
     * Job worker that handles sending verification emails.
     * This is a mocked implementation that simulates sending an email.
     *
     * @param job The activated job containing the email data
     * @return A map containing the email response
     */
    @JobWorker(type = "send-verification-email-task")
    public Map<String, Object> sendVerificationEmail(final ActivatedJob job) {
        log.info("Executing send-verification-email-task for job: {}", job.getKey());

        // Get variables from the process
        Map<String, Object> variables = job.getVariablesAsMap();
        String email = (String) variables.get("email");

        log.info("Sending verification email to: {}", email);

        // WebClient call
        String verificationCode = String.format("%06d", new Random().nextInt(1000000));
        EmailRequestDTO emailRequest = new EmailRequestDTO();
        emailRequest.setFrom("firefly@firefly.com");
        emailRequest.setTo(email);
        emailRequest.setSubject("Firefly Verification Email");
        emailRequest.setHtml("Your verification code is: " + verificationCode + "\n\nThank you for using Firefly!");
        Mono<ResponseEntity<EmailResponseDTO>> responseMono = notificationsService.sendEmail(emailRequest);

        // Get the response
        ResponseEntity<EmailResponseDTO> response = responseMono.block();
        EmailResponseDTO emailResponse = response.getBody();

        log.info("Email sent successfully with ID: {}", emailResponse.getMessageId());

        // Prepare result for the process
        Map<String, Object> result = new HashMap<>();
        result.put(EMAIL_ID, emailResponse.getMessageId());
        result.put("email", email);
        result.put("emailStatus", emailResponse.getStatus());
        result.put("verificationCode", verificationCode);

        return result;
    }

    /**
     * Job worker that handles creating SCA challenges.
     * This is a mocked implementation that simulates creating an SCA challenge.
     *
     * @param job The activated job containing the email data, email ID, and operation ID
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
