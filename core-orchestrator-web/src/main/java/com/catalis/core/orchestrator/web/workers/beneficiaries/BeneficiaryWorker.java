package com.catalis.core.orchestrator.web.workers.beneficiaries;

import com.catalis.baas.adapter.BeneficiaryAdapter;
import com.catalis.core.orchestrator.interfaces.dtos.beneficiaries.BeneficiaryRequest;
import com.catalis.core.orchestrator.interfaces.mappers.BeneficiaryMapper;
import com.google.protobuf.ServiceException;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Worker component that handles beneficiary-related tasks in Camunda Zeebe workflows.
 * Provides job workers for creating beneficiaries.
 */
@Component
public class BeneficiaryWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeneficiaryWorker.class);

    private static final String EXTERNAL_REFERENCE_ID = "externalReferenceId";

    private final BeneficiaryAdapter beneficiaryAdapter;
    private final BeneficiaryMapper beneficiaryMapper;

    /**
     * Default constructor for BeneficiaryWorker.
     */
    public BeneficiaryWorker(BeneficiaryAdapter beneficiaryAdapter, BeneficiaryMapper beneficiaryMapper) {
        this.beneficiaryAdapter = beneficiaryAdapter;
        this.beneficiaryMapper = beneficiaryMapper;
    }

    /**
     * Job worker that handles the creation of beneficiaries in the external BaaS system.
     *
     * @param job The activated job containing the beneficiary data
     * @return A map containing the external reference ID
     * @throws ServiceException If there's an error calling the external service
     */
    @JobWorker(type = "baas-create-beneficiary")
    public Map<String, Object> baasCreateBeneficiary(final ActivatedJob job) throws ServiceException {
        LOGGER.info("Executing baas-create-beneficiary task for job: {}", job.getKey());

        // Get variables from the process
        BeneficiaryRequest beneficiaryData = job.getVariablesAsType(BeneficiaryRequest.class);

        LOGGER.info("Creating beneficiary for user ID: {}", beneficiaryData.userId());

        // Call the external microservice
        String externalId = Objects.requireNonNull(beneficiaryAdapter.createBeneficiary(beneficiaryMapper.requestToDTO(beneficiaryData)).block()).getBody();

        LOGGER.info("Beneficiary created successfully with ID: {}", externalId);

        // Prepare result for the process
        Map<String, Object> result = new HashMap<>();
        result.put(EXTERNAL_REFERENCE_ID, externalId);

        return result;
    }
}