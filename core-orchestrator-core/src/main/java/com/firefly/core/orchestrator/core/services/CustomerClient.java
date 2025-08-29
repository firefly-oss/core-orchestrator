package com.firefly.core.orchestrator.core.services;

import com.firefly.baas.adapter.CustomerAdapter;
import com.firefly.baas.dtos.customers.LegalPersonAdapterDTO;
import com.firefly.baas.dtos.customers.NaturalPersonAdapterDTO;
import com.firefly.baas.dtos.customers.TaxResidenceAdapterDTO;
import com.firefly.core.orchestrator.interfaces.dtos.accounts.LegalPersonRequest;
import com.firefly.core.orchestrator.interfaces.dtos.accounts.NaturalPersonRequest;
import com.firefly.core.orchestrator.interfaces.dtos.accounts.TaxResidenceRequest;
import com.firefly.core.orchestrator.interfaces.dtos.customers.CustomerResponse;
import com.firefly.core.orchestrator.interfaces.mappers.CustomerMapper;
import com.firefly.core.orchestrator.interfaces.mappers.LegalPersonMapper;
import com.firefly.core.orchestrator.interfaces.mappers.NaturalPersonMapper;
import com.firefly.core.orchestrator.interfaces.mappers.TaxResidenceMapper;
import com.firefly.core.orchestrator.interfaces.services.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Implementation of the CustomerService interface.
 * Provides methods for creating and managing customers using the BaaS adapter.
 */
@Service
@Slf4j
public class CustomerClient implements CustomerService {

    private final CustomerAdapter customerAdapter;
    private final LegalPersonMapper legalPersonMapper;
    private final NaturalPersonMapper naturalPersonMapper;
    private final TaxResidenceMapper taxResidenceMapper;
    private final CustomerMapper customerMapper;

    /**
     * Creates a new CustomerClient with the specified adapter and mappers.
     *
     * @param customerAdapter the adapter for interacting with the BaaS system
     * @param legalPersonMapper the mapper for converting between legal person DTOs
     * @param naturalPersonMapper the mapper for converting between natural person DTOs
     * @param taxResidenceMapper the mapper for converting between tax residence DTOs
     * @param customerMapper the mapper for converting between customer DTOs
     */
    @Autowired
    public CustomerClient(CustomerAdapter customerAdapter,
                          LegalPersonMapper legalPersonMapper,
                          NaturalPersonMapper naturalPersonMapper,
                          TaxResidenceMapper taxResidenceMapper,
                          CustomerMapper customerMapper) {
        this.customerAdapter = customerAdapter;
        this.legalPersonMapper = legalPersonMapper;
        this.naturalPersonMapper = naturalPersonMapper;
        this.taxResidenceMapper = taxResidenceMapper;
        this.customerMapper = customerMapper;
    }

    /**
     * Creates a legal person in the external BaaS system.
     *
     * @param legalPersonRequest the legal person data to create
     * @return a Mono containing the response with the created legal person data
     */
    @Override
    public Mono<CustomerResponse> createLegalPerson(LegalPersonRequest legalPersonRequest) {
        log.info("Creating legal person: {}", legalPersonRequest.legalName());

        // Call the external microservice
        return customerAdapter.createLegalPerson(legalPersonMapper.requestToDTO(legalPersonRequest))
                .mapNotNull(ResponseEntity::getBody)
                .map(customerMapper::legalPersonDTOToResponse);
    }

    /**
     * Creates a natural person in the external BaaS system.
     *
     * @param naturalPersonRequest the natural person data to create
     * @return a Mono containing the response with the created natural person data
     */
    @Override
    public Mono<CustomerResponse> createNaturalPerson(NaturalPersonRequest naturalPersonRequest) {
        log.info("Creating natural person: {}", naturalPersonRequest.firstname());

        // Call the external microservice
        return customerAdapter.createNaturalPerson(naturalPersonMapper.requestToDTO(naturalPersonRequest))
                .mapNotNull(ResponseEntity::getBody)
                .map(customerMapper::naturalPersonDTOToResponse);
    }

    /**
     * Creates a tax residence in the external BaaS system.
     *
     * @param taxResidenceRequest the tax residence data to create
     * @return a Mono containing the tax residence adapter DTO
     */
    @Override
    public Mono<TaxResidenceAdapterDTO> createTaxResidence(TaxResidenceRequest taxResidenceRequest) {
        log.info("Creating tax residence for userID: {}", taxResidenceRequest.userId());

        // Call the external microservice
        return customerAdapter.createTaxResidence(taxResidenceMapper.requestToDTO(taxResidenceRequest))
                .mapNotNull(ResponseEntity::getBody);
    }

    /**
     * Starts a KYC review process for a user.
     *
     * @param userId the ID of the user to review
     * @return a Mono containing the external reference ID
     */
    @Override
    public Mono<String> startKycReview(Integer userId) {
        log.info("Starting KYC review for user ID: {}", userId);

        // Call the external microservice
        return customerAdapter.requestKYC(userId)
                .mapNotNull(ResponseEntity::getBody);
    }

    /**
     * Starts a KYB review process for a user.
     *
     * @param userId the ID of the user to review
     * @return a Mono containing the external reference ID
     */
    @Override
    public Mono<String> startKybReview(Integer userId) {
        log.info("Starting KYB review for user ID: {}", userId);

        // Call the external microservice
        return customerAdapter.requestKYB(userId)
                .mapNotNull(ResponseEntity::getBody);
    }

    /**
     * Stores legal person data in the database.
     *
     * @param userData the legal person data to store
     * @return a map containing the status of the operation
     */
    @Override
    public Map<String, Object> storeLegalPersonData(LegalPersonAdapterDTO userData) {
        log.info("Storing legal person data for: {} with external ID: {}", userData.legalName());

        // Mock database storage
        mockDatabaseStore(userData);

        log.info("Legal person data stored successfully in database");

        // Return the variables to pass them to the next task
        Map<String, Object> variables = new HashMap<>();
        variables.put("status", "ok");
        return variables;
    }

    // Mock method to simulate database storage
    private void mockDatabaseStore(LegalPersonAdapterDTO userData) {
        // This is a mock method that simulates storing data in a database
        // In a real implementation, this would connect to a database and store the data
        log.info("MOCK DB: Storing legal person {} with external ID {} in database", userData.legalName());
    }
}