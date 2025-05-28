package com.catalis.core.orchestrator.web.workers.notifications;

import com.catalis.common.platform.notification.services.sdk.model.EmailResponseDTO;
import com.catalis.common.sca.sdk.model.SCAChallengeDTO;
import com.catalis.common.sca.sdk.model.SCAOperationDTO;
import com.catalis.core.orchestrator.interfaces.dtos.notifications.EmailRequest;
import com.catalis.core.orchestrator.interfaces.mappers.EmailMapper;
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
 * Worker component that handles email notification-related tasks in Camunda Zeebe workflows.
 * Provides job workers for sending verification emails and creating SCA operations and challenges.
 */
@Component
@Slf4j
public class EmailWorker {

    private static final String EMAIL_ID = "emailId";
    private static final String OPERATION_ID = "operationId";
    private static final String CHALLENGE_ID = "challengeId";

    private final SCAService scaService;
    private final NotificationsService notificationsService;
    private final EmailMapper emailMapper;

    @Autowired
    public EmailWorker(SCAService scaService, NotificationsService notificationsService, 
                      EmailMapper emailMapper) {
        this.scaService = scaService;
        this.notificationsService = notificationsService;
        this.emailMapper = emailMapper;
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
        EmailRequest emailData = job.getVariablesAsType(EmailRequest.class);
        log.info("Creating SCA operation for email: {}", emailData.to());

        // WebClient call - pass EmailRequest directly to scaService
        Mono<ResponseEntity<SCAOperationDTO>> responseMono = scaService.createOperation(emailData);

        // Get the response
        ResponseEntity<SCAOperationDTO> response = responseMono.block();
        SCAOperationDTO scaOperation = response.getBody();

        log.info("SCA operation created successfully with ID: {}", scaOperation.getId());

        // Prepare result for the process
        Map<String, Object> result = new HashMap<>(variables);
        result.put(OPERATION_ID, scaOperation.getId());
        result.put("email", emailData.to());

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
        EmailRequest emailRequest = EmailRequest.builder()
                .from("firefly@firefly.com")
                .to(email)
                .subject("Firefly Verification Email")
                .html("Your verification code is: " + verificationCode + "\n\nThank you for using Firefly!")
                .build();
        Mono<ResponseEntity<EmailResponseDTO>> responseMono = notificationsService.sendEmail(emailMapper.requestToDTO(emailRequest));

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