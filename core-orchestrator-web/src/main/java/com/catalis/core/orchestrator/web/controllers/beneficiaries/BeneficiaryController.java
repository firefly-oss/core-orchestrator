package com.catalis.core.orchestrator.web.controllers.beneficiaries;

import com.catalis.core.orchestrator.interfaces.dtos.beneficiaries.BeneficiaryRequest;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that handles beneficiary-related API endpoints.
 * Provides endpoints for creating beneficiaries by starting Camunda Zeebe processes.
 */
@RestController
@RequestMapping("/api/v1/beneficiaries")
@Slf4j
@Tag(name = "Beneficiaries", description = "API endpoints for beneficiary operations")
public class BeneficiaryController extends BaseController {

    /**
     * Constructs a new BeneficiaryController with the specified Zeebe client.
     *
     * @param zeebeClient The client used to interact with the Camunda Zeebe workflow engine
     * @param processCompletionRegistry The registry for tracking process completion
     */
    @Autowired
    public BeneficiaryController(ZeebeClient zeebeClient, ProcessCompletionRegistry processCompletionRegistry) {
        super(zeebeClient, processCompletionRegistry);
    }

    /**
     * Starts a process to create a beneficiary.
     *
     * @param beneficiaryData The beneficiary data to be processed
     * @return A response containing the process instance key and status
     */
    @Operation(
        operationId = "createBeneficiary",
        summary = "Create a new beneficiary",
        description = "Starts a process to create a new beneficiary with the provided data"
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
    @PostMapping
    public ResponseEntity<ProcessResponse> startCreateBeneficiaryProcess(
        @Parameter(description = "Beneficiary creation request details") 
        @RequestBody BeneficiaryRequest beneficiaryData) {
        log.info("Starting create-beneficiary process with data: {}", beneficiaryData);

        try {
            ProcessResponse response = startProcess(CREATE_BENEFICIARY, beneficiaryData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error starting process: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
