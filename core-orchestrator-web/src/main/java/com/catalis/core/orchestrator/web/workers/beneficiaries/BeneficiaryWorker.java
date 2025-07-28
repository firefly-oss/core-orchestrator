package com.catalis.core.orchestrator.web.workers.beneficiaries;

import com.catalis.core.orchestrator.interfaces.dtos.beneficiaries.BeneficiaryRequest;
import com.catalis.core.orchestrator.interfaces.dtos.beneficiaries.BeneficiaryResponse;
import com.catalis.core.orchestrator.interfaces.services.BeneficiaryService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Worker component that handles beneficiary-related tasks in Camunda Zeebe workflows.
 * Provides job workers for creating beneficiaries.
 */
@Component
@Slf4j
public class BeneficiaryWorker {

    private static final String EXTERNAL_REFERENCE_ID = "externalReferenceId";

    private final BeneficiaryService beneficiaryService;

    /**
     * Default constructor for BeneficiaryWorker.
     *
     * @param beneficiaryService the service for beneficiary operations
     */
    public BeneficiaryWorker(BeneficiaryService beneficiaryService) {
        this.beneficiaryService = beneficiaryService;
    }

    /**
     * Job worker that handles the creation of beneficiaries in the external BaaS system.
     *
     * @param job The activated job containing the beneficiary data
     * @return A Mono containing a map with the external reference ID
     */
    @JobWorker(type = "baas-create-beneficiary")
    public Mono<BeneficiaryResponse> baasCreateBeneficiary(final ActivatedJob job) {
        log.info("Executing baas-create-beneficiary task for job: {}", job.getKey());

        // Get variables from the process
        BeneficiaryRequest beneficiaryData = job.getVariablesAsType(BeneficiaryRequest.class);

        log.info("Delegating beneficiary creation for user ID: {}", beneficiaryData.userId());

        // Delegate to the beneficiary service
        return beneficiaryService.createBeneficiary(beneficiaryData);
    }
}
