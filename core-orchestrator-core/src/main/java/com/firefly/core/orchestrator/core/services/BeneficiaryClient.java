package com.firefly.core.orchestrator.core.services;

import com.firefly.baas.adapter.BeneficiaryAdapter;
import com.firefly.core.orchestrator.interfaces.dtos.beneficiaries.BeneficiaryRequest;
import com.firefly.core.orchestrator.interfaces.dtos.beneficiaries.BeneficiaryResponse;
import com.firefly.core.orchestrator.interfaces.mappers.BeneficiaryMapper;
import com.firefly.core.orchestrator.interfaces.services.BeneficiaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Implementation of the BeneficiaryService interface.
 * Provides methods for creating and managing beneficiaries using the BaaS adapter.
 */
@Service
@Slf4j
public class BeneficiaryClient implements BeneficiaryService {

    private final BeneficiaryAdapter beneficiaryAdapter;
    private final BeneficiaryMapper beneficiaryMapper;

    /**
     * Creates a new BeneficiaryClient with the specified adapter and mapper.
     *
     * @param beneficiaryAdapter the adapter for interacting with the BaaS system
     * @param beneficiaryMapper the mapper for converting between DTOs
     */
    @Autowired
    public BeneficiaryClient(BeneficiaryAdapter beneficiaryAdapter, BeneficiaryMapper beneficiaryMapper) {
        this.beneficiaryAdapter = beneficiaryAdapter;
        this.beneficiaryMapper = beneficiaryMapper;
    }

    /**
     * Creates a beneficiary in the external BaaS system.
     *
     * @param beneficiaryRequest the beneficiary data to create
     * @return a Mono containing the response with the created beneficiary's external reference ID
     */
    @Override
    public Mono<BeneficiaryResponse> createBeneficiary(BeneficiaryRequest beneficiaryRequest) {
        log.info("Creating beneficiary for user ID: {}", beneficiaryRequest.userId());

        // Call the external microservice
        return beneficiaryAdapter.createBeneficiary(beneficiaryMapper.requestToDTO(beneficiaryRequest))
                .mapNotNull(HttpEntity::getBody)
                .map(beneficiaryMapper::toResponse);
    }
}