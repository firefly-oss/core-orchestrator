package com.catalis.core.orchestrator.web.workers.customers;

import com.catalis.baas.adapter.CustomerAdapter;
import com.catalis.baas.adapter.impl.CustomerAdapterImpl;
import com.catalis.baas.dtos.customers.LegalPersonAdapterDTO;
import com.catalis.baas.dtos.customers.NaturalPersonAdapterDTO;
import com.catalis.baas.dtos.customers.TaxResidenceAdapterDTO;
import com.catalis.core.orchestrator.interfaces.dtos.accounts.LegalPersonRequest;
import com.catalis.core.orchestrator.interfaces.dtos.accounts.NaturalPersonRequest;
import com.catalis.core.orchestrator.interfaces.dtos.accounts.TaxResidenceRequest;
import com.catalis.core.orchestrator.interfaces.dtos.customers.CustomerResponse;
import com.catalis.core.orchestrator.interfaces.mappers.CustomerMapper;
import com.catalis.core.orchestrator.interfaces.mappers.LegalPersonMapper;
import com.catalis.core.orchestrator.interfaces.mappers.NaturalPersonMapper;
import com.catalis.core.orchestrator.interfaces.mappers.TaxResidenceMapper;
import com.google.protobuf.ServiceException;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Worker component that handles customer-related tasks in Camunda Zeebe workflows.
 * Provides job workers for creating legal and natural persons and storing their data.
 */
@Component
@Slf4j
public class CustomerWorker {

    private static final String EXTERNAL_REFERENCE_ID = "externalReferenceId";

    private final CustomerAdapter customerAdapter;
    private final LegalPersonMapper legalPersonMapper;
    private final NaturalPersonMapper naturalPersonMapper;
    private final TaxResidenceMapper taxResidenceMapper;
    private final CustomerMapper customerMapper;

    /**
     * Constructs a new CustomerWorker with the specified customer adapter.
     *
     * @param customerAdapter The adapter used to communicate with the customer service
     */
    @Autowired
    public CustomerWorker(CustomerAdapterImpl customerAdapter,
                          LegalPersonMapper legalPersonMapper,
                          NaturalPersonMapper naturalPersonMapper,
                          TaxResidenceMapper taxResidenceMapper, CustomerMapper customerMapper) {
        this.customerAdapter = customerAdapter;
        this.legalPersonMapper = legalPersonMapper;
        this.naturalPersonMapper = naturalPersonMapper;
        this.taxResidenceMapper = taxResidenceMapper;
        this.customerMapper = customerMapper;
    }

    /**
     * Job worker that handles the creation of legal persons in the external BaaS system.
     *
     * @param job The activated job containing the legal person data
     * @return A map containing the external reference ID
     * @throws ServiceException If there's an error calling the external service
     */
    @JobWorker(type = "baas-create-legal-person")
    public Mono<CustomerResponse> baasCreateLegalPerson(final ActivatedJob job) throws ServiceException {
        log.info("Executing baas-create-legal-person task for job: {}", job.getKey());

        // Get variables from the process
        LegalPersonRequest userData = job.getVariablesAsType(LegalPersonRequest.class);

        log.info("Creating legal person: {}", userData.legalName());

        // Call the external microservice
        Mono<LegalPersonAdapterDTO> legalPersonDTO;
        try {
            legalPersonDTO = customerAdapter.createLegalPerson(legalPersonMapper.requestToDTO(userData))
                    .mapNotNull(ResponseEntity::getBody);
        } catch (WebClientResponseException e) {
            log.error("Error calling external service: {}", e.getMessage());
            throw new ServiceException("Failed to create legal person", e);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            throw new ServiceException("Unexpected error creating legal person", e);
        }
        log.info("External ID retrieved successfully");

        return legalPersonDTO.map(customerMapper::legalPersonDTOToResponse);
    }

    /**
     * Job worker that handles the creation of natural persons in the external BaaS system.
     *
     * @param job The activated job containing the natural person data
     * @return A map containing the external reference ID
     */
    @JobWorker(type = "baas-create-natural-person")
    public Mono<CustomerResponse> baasCreateNaturalPerson(final ActivatedJob job) throws ServiceException {
        log.info("Executing baas-create-natural-person task for job: {}", job.getKey());

        // Get variables from the process
        NaturalPersonRequest userData = job.getVariablesAsType(NaturalPersonRequest.class);

        log.info("Creating natural person: {}", userData.firstname());

        // Call the external microservice
        Mono<NaturalPersonAdapterDTO> naturalPersonDTO;
        try {
            naturalPersonDTO = customerAdapter.createNaturalPerson(naturalPersonMapper.requestToDTO(userData))
                    .mapNotNull(ResponseEntity::getBody);
        } catch (WebClientResponseException e) {
            log.error("Error calling external service: {}", e.getMessage());
            throw new ServiceException("Failed to create natural person", e);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            throw new ServiceException("Unexpected error creating natural person", e);
        }
        log.info("External ID retrieved successfully");

        return naturalPersonDTO.map(customerMapper::naturalPersonDTOToResponse);
    }

    /**
     * Job worker that handles the creation of tax residence in the external BaaS system.
     *
     * @param job The activated job containing the tax residence data
     * @return A map containing the external reference ID
     * @throws ServiceException If there's an error calling the external service
     */
    @JobWorker(type = "baas-create-tax-residence")
    public Map<String, Object> baasCreateTaxResidence(final ActivatedJob job) throws ServiceException {
        log.info("Executing baas-create-tax-residence task for job: {}", job.getKey());

        // Get variables from the process
        TaxResidenceRequest taxResidenceData = job.getVariablesAsType(TaxResidenceRequest.class);

        log.info("Creating tax residence for userID: {}", taxResidenceData.userId());

        // Call the external microservice
        TaxResidenceAdapterDTO externalId;
        try {
            externalId = Objects.requireNonNull(customerAdapter.createTaxResidence(taxResidenceMapper.requestToDTO(taxResidenceData)).block()).getBody();
        } catch (WebClientResponseException e) {
            log.error("Error calling external service: {}", e.getMessage());
            throw new ServiceException("Failed to create tax residence", e);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            throw new ServiceException("Unexpected error creating tax residence", e);
        }
        log.info("External ID retrieved successfully: {}", externalId);

        // Prepare result for the process
        Map<String, Object> result = new HashMap<>();
        result.put(EXTERNAL_REFERENCE_ID, externalId);

        return result;
    }

    /**
     * Job worker that handles the KYC review process for a user.
     *
     * @param job The activated job containing the user ID
     * @return A map containing the process variables to pass to the next task
     * @throws ServiceException If there's an error calling the external service
     */
    @JobWorker(type = "baas-start-kyc-review")
    public Map<String, Object> baasStartKycReview(final ActivatedJob job) throws ServiceException {
        log.info("Executing baas-start-kyc-review task for job: {}", job.getKey());

        // Get variables from the process
        Map<String, Object> variables = job.getVariablesAsMap();
        Integer userId = (Integer) variables.get("userId");

        log.info("Starting KYC review for user ID: {}", userId);

        // Call the external microservice
        String externalId = Objects.requireNonNull(customerAdapter.requestKYC(userId).block()).getBody();

        // Mock response for now
        log.info("KYC review started successfully with ID: {}", externalId);

        // Prepare result for the process
        Map<String, Object> result = new HashMap<>();
        result.put(EXTERNAL_REFERENCE_ID, externalId);
        result.put("userId", userId);

        return result;
    }

    /**
     * Job worker that handles the KYB review process for a user.
     *
     * @param job The activated job containing the user ID
     * @return A map containing the process variables to pass to the next task
     * @throws ServiceException If there's an error calling the external service
     */
    @JobWorker(type = "baas-start-kyb-review")
    public Map<String, Object> baasStartKybReview(final ActivatedJob job) throws ServiceException {
        log.info("Executing baas-start-kyb-review task for job: {}", job.getKey());

        // Get variables from the process
        Map<String, Object> variables = job.getVariablesAsMap();
        Integer userId = (Integer) variables.get("userId");

        log.info("Starting KYB review for user ID: {}", userId);

        // Call the external microservice
        String externalId = Objects.requireNonNull(customerAdapter.requestKYB(userId).block()).getBody();

        log.info("KYB review started successfully with ID: {}", externalId);

        // Prepare result for the process
        Map<String, Object> result = new HashMap<>();
        result.put(EXTERNAL_REFERENCE_ID, externalId);
        result.put("userId", userId);

        return result;
    }


    /**
     * Job worker that handles storing legal person data in the database.
     * Currently uses a mock implementation for database storage.
     *
     * @param job The activated job containing the legal person data and external ID
     * @return A map containing the process variables to pass to the next task
     */
    @JobWorker(type = "store-legal-person-data")
    public Map<String, Object> storeLegalPersonData(final ActivatedJob job) {
        log.info("Executing store-legal-person-data task for job: {}", job.getKey());

        // Get variables from the process
        LegalPersonAdapterDTO userData = job.getVariablesAsType(LegalPersonAdapterDTO.class);

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
