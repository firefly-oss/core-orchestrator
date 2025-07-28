package com.catalis.core.orchestrator.interfaces.services;

import com.catalis.core.orchestrator.interfaces.dtos.beneficiaries.BeneficiaryRequest;
import com.catalis.core.orchestrator.interfaces.dtos.beneficiaries.BeneficiaryResponse;
import reactor.core.publisher.Mono;

/**
 * Interface for beneficiary-related operations.
 * Provides methods for creating and managing beneficiaries.
 */
public interface BeneficiaryService {

    /**
     * Creates a beneficiary in the external BaaS system.
     *
     * @param beneficiaryRequest the beneficiary data to create
     * @return a Mono containing the response with the created beneficiary's external reference ID
     */
    Mono<BeneficiaryResponse> createBeneficiary(BeneficiaryRequest beneficiaryRequest);
}