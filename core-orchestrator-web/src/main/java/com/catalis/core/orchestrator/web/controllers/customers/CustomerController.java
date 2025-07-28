package com.catalis.core.orchestrator.web.controllers.customers;

import com.catalis.core.orchestrator.interfaces.dtos.accounts.LegalPersonRequest;
import com.catalis.core.orchestrator.interfaces.dtos.accounts.NaturalPersonRequest;
import com.catalis.core.orchestrator.interfaces.dtos.accounts.TaxResidenceRequest;
import com.catalis.core.orchestrator.interfaces.dtos.process.ProcessResponse;
import com.catalis.core.orchestrator.web.controllers.BaseController;
import com.catalis.core.orchestrator.web.utils.ProcessCompletionRegistry;
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
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller that handles customer-related API endpoints.
 * Provides endpoints for creating legal and natural persons by starting Camunda Zeebe processes.
 */
@RestController
@RequestMapping("/api/v1/customers")
@Slf4j
@Tag(name = "Customers", description = "API endpoints for customer operations")
public class CustomerController extends BaseController {
    /**
     * Constructs a new CustomerController with the specified Zeebe client.
     *
     * @param zeebeClient The client used to interact with the Camunda Zeebe workflow engine
     */
    @Autowired
    public CustomerController(ZeebeClient zeebeClient, ProcessCompletionRegistry processCompletionRegistry) {
        super(zeebeClient, processCompletionRegistry);
    }

    /**
     * Starts a process to create a legal person.
     *
     * @param userData The legal person data to be processed
     * @return A response containing the process instance key and status
     */
    @Operation(
        operationId = "createLegalPerson",
        summary = "Create a legal person",
        description = "Starts a process to create a legal person with the provided data"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Process started successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProcessResponse.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(mediaType = "application/json")
        )
    })
    @PostMapping(value = "/create-legal-person")
    public ResponseEntity<ProcessResponse> startCreateLegalPersonProcess(
        @Parameter(description = "Legal person creation request details") 
        @RequestBody LegalPersonRequest userData) {
        log.info("Starting create-legal-person process with data: {}", userData);

        try {
            ProcessResponse response = startProcess(CREATE_LEGAL_PERSON, userData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error starting process: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Starts a process to create a natural person.
     *
     * @param userData The natural person data to be processed
     * @return A response containing the process instance key and status
     */
    @Operation(
        operationId = "createNaturalPerson",
        summary = "Create a natural person",
        description = "Starts a process to create a natural person with the provided data"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Process started successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProcessResponse.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(mediaType = "application/json")
        )
    })
    @PostMapping(value = "/create-natural-person")
    public ResponseEntity<ProcessResponse> startCreateNaturalPersonProcess(
        @Parameter(description = "Natural person creation request details") 
        @RequestBody NaturalPersonRequest userData) {
        log.info("Starting create-natural-person process with data: {}", userData);

        try {
            ProcessResponse response = startProcess(CREATE_NATURAL_PERSON, userData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error starting process: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Starts a process to create a tax residence.
     *
     * @param userData The tax residence data to be processed
     * @return A response containing the process instance key and status
     */
    @Operation(
        operationId = "createTaxResidence",
        summary = "Create a tax residence",
        description = "Starts a process to create a tax residence with the provided data"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Process started successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProcessResponse.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(mediaType = "application/json")
        )
    })
    @PostMapping(value = "/create-tax-residence")
    public ResponseEntity<ProcessResponse> startCreateTaxResidenceProcess(
        @Parameter(description = "Tax residence creation request details") 
        @RequestBody TaxResidenceRequest userData) {
        log.info("Starting create-tax-residence-process with data: {}", userData);

        try {
            ProcessResponse response = startProcess(CREATE_TAX_RESIDENCE, userData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error starting process: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Starts a process to review KYC for a user.
     *
     * @param userId The ID of the user to review
     * @return A response containing the process instance key and status
     */
    @Operation(
        operationId = "startKycReview",
        summary = "Start KYC review process",
        description = "Starts a process to review KYC (Know Your Customer) for a user"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Process started successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProcessResponse.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(mediaType = "application/json")
        )
    })
    @PostMapping(value = "/{userId}/kycreview")
    public ResponseEntity<ProcessResponse> startKycReviewProcess(
        @Parameter(description = "ID of the user to review") 
        @PathVariable Integer userId) {
        log.info("Starting KYC review process for user ID: {}", userId);

        try {
            Map<String, Object> variables = Map.of("userId", userId);
            ProcessResponse response = startProcess(USER_KYC_REVIEW, variables);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error starting KYC review process: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Starts a process to review KYB for a user.
     *
     * @param userId The ID of the user to review
     * @return A response containing the process instance key and status
     */
    @Operation(
        operationId = "startKybReview",
        summary = "Start KYB review process",
        description = "Starts a process to review KYB (Know Your Business) for a user"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Process started successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProcessResponse.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(mediaType = "application/json")
        )
    })
    @PostMapping(value = "/{userId}/kybreview")
    public ResponseEntity<ProcessResponse> startKybReviewProcess(
        @Parameter(description = "ID of the user to review") 
        @PathVariable Integer userId) {
        log.info("Starting KYB review process for user ID: {}", userId);

        try {
            Map<String, Object> variables = Map.of("userId", userId);
            ProcessResponse response = startProcess(USER_KYB_REVIEW, variables);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error starting KYB review process: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
