package com.catalis.core.orchestrator.interfaces.services;

import com.catalis.baas.dtos.customers.LegalPersonAdapterDTO;
import com.catalis.baas.dtos.customers.NaturalPersonAdapterDTO;
import com.catalis.baas.dtos.customers.TaxResidenceAdapterDTO;
import com.catalis.core.orchestrator.interfaces.dtos.accounts.LegalPersonRequest;
import com.catalis.core.orchestrator.interfaces.dtos.accounts.NaturalPersonRequest;
import com.catalis.core.orchestrator.interfaces.dtos.accounts.TaxResidenceRequest;
import com.catalis.core.orchestrator.interfaces.dtos.customers.CustomerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Interface for customer-related operations.
 * Provides methods for creating and managing customers.
 */
public interface CustomerService {

    /**
     * Creates a legal person in the external BaaS system.
     *
     * @param legalPersonRequest the legal person data to create
     * @return a Mono containing the response with the created legal person data
     */
    Mono<CustomerResponse> createLegalPerson(LegalPersonRequest legalPersonRequest);

    /**
     * Creates a natural person in the external BaaS system.
     *
     * @param naturalPersonRequest the natural person data to create
     * @return a Mono containing the response with the created natural person data
     */
    Mono<CustomerResponse> createNaturalPerson(NaturalPersonRequest naturalPersonRequest);

    /**
     * Creates a tax residence in the external BaaS system.
     *
     * @param taxResidenceRequest the tax residence data to create
     * @return a Mono containing the tax residence adapter DTO
     */
    Mono<TaxResidenceAdapterDTO> createTaxResidence(TaxResidenceRequest taxResidenceRequest);

    /**
     * Starts a KYC review process for a user.
     *
     * @param userId the ID of the user to review
     * @return a Mono containing the external reference ID
     */
    Mono<String> startKycReview(Integer userId);

    /**
     * Starts a KYB review process for a user.
     *
     * @param userId the ID of the user to review
     * @return a Mono containing the external reference ID
     */
    Mono<String> startKybReview(Integer userId);

    /**
     * Stores legal person data in the database.
     *
     * @param userData the legal person data to store
     * @return a map containing the status of the operation
     */
    Map<String, Object> storeLegalPersonData(LegalPersonAdapterDTO userData);
}