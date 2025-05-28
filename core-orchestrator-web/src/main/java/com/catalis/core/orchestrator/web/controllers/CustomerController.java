package com.catalis.core.orchestrator.web.controllers;

import com.catalis.baas.dtos.customers.LegalPersonAdapterDTO;
import com.catalis.baas.dtos.customers.NaturalPersonAdapterDTO;
import com.catalis.baas.dtos.customers.TaxResidenceAdapterDTO;
import com.catalis.core.orchestrator.interfaces.dtos.accounts.LegalPersonRequest;
import com.catalis.core.orchestrator.interfaces.dtos.accounts.NaturalPersonRequest;
import com.catalis.core.orchestrator.interfaces.dtos.accounts.TaxResidenceRequest;
import io.camunda.zeebe.client.ZeebeClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller that handles customer-related API endpoints.
 * Provides endpoints for creating legal and natural persons by starting Camunda Zeebe processes.
 */
@RestController
@RequestMapping("/api/v1/customers")
@Slf4j
public class CustomerController extends BaseController{
    /**
     * Constructs a new CustomerController with the specified Zeebe client.
     *
     * @param zeebeClient The client used to interact with the Camunda Zeebe workflow engine
     */
    @Autowired
    public CustomerController(ZeebeClient zeebeClient) {
        super(zeebeClient);
    }

    /**
     * Starts a process to create a legal person.
     *
     * @param userData The legal person data to be processed
     * @return A response containing the process instance key and status
     */
    @PostMapping(value = "/create-legal-person")
    public ResponseEntity<Map<String, Object>> startCreateLegalPersonProcess(@RequestBody LegalPersonRequest userData) {
        log.info("Starting create-legal-person process with data: {}", userData);

        try {
            return startProcess(CREATE_LEGAL_PERSON, userData);
        } catch (Exception e) {
            log.error("Error starting process: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to start process", "message", e.getMessage()));
        }
    }

    /**
     * Starts a process to create a natural person.
     *
     * @param userData The natural person data to be processed
     * @return A response containing the process instance key and status
     */
    @PostMapping(value = "/create-natural-person")
    public ResponseEntity<Map<String, Object>> startCreateNaturalPersonProcess(@RequestBody NaturalPersonRequest userData) {
        log.info("Starting create-natural-person process with data: {}", userData);

        try {
            return startProcess(CREATE_NATURAL_PERSON, userData);
        } catch (Exception e) {
            log.error("Error starting process: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to start process", "message", e.getMessage()));
        }
    }

    /**
     * Starts a process to create a tax residence.
     *
     * @param userData The tax residence data to be processed
     * @return A response containing the process instance key and status
     */
    @PostMapping(value = "/create-tax-residence")
    public ResponseEntity<Map<String, Object>> startCreateTaxResidenceProcess(@RequestBody TaxResidenceRequest userData) {
        log.info("Starting create-tax-residence-process with data: {}", userData);

        try {
            return startProcess(CREATE_TAX_RESIDENCE, userData);
        } catch (Exception e) {
            log.error("Error starting process: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to start process", "message", e.getMessage()));
        }
    }

    /**
     * Starts a process to review KYC for a user.
     *
     * @param userId The ID of the user to review
     * @return A response containing the process instance key and status
     */
    @PostMapping(value = "/{userId}/kycreview")
    public ResponseEntity<Map<String, Object>> startKycReviewProcess(@PathVariable Integer userId) {
        log.info("Starting KYC review process for user ID: {}", userId);

        try {
            Map<String, Object> variables = Map.of("userId", userId);
            return startProcess(USER_KYC_REVIEW, variables);
        } catch (Exception e) {
            log.error("Error starting KYC review process: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to start KYC review process", "message", e.getMessage()));
        }
    }

    /**
     * Starts a process to review KYB for a user.
     *
     * @param userId The ID of the user to review
     * @return A response containing the process instance key and status
     */
    @PostMapping(value = "/{userId}/kybreview")
    public ResponseEntity<Map<String, Object>> startKybReviewProcess(@PathVariable Integer userId) {
        log.info("Starting KYB review process for user ID: {}", userId);

        try {
            Map<String, Object> variables = Map.of("userId", userId);
            return startProcess(USER_KYB_REVIEW, variables);
        } catch (Exception e) {
            log.error("Error starting KYB review process: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to start KYB review process", "message", e.getMessage()));
        }
    }

}
