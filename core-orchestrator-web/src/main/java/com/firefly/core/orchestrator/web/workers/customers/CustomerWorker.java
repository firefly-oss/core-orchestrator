package com.firefly.core.orchestrator.web.workers.customers;

import com.firefly.baas.dtos.customers.LegalPersonAdapterDTO;
import com.firefly.baas.dtos.customers.TaxResidenceAdapterDTO;
import com.firefly.core.orchestrator.interfaces.dtos.accounts.LegalPersonRequest;
import com.firefly.core.orchestrator.interfaces.dtos.accounts.NaturalPersonRequest;
import com.firefly.core.orchestrator.interfaces.dtos.accounts.TaxResidenceRequest;
import com.firefly.core.orchestrator.interfaces.dtos.customers.CustomerResponse;
import com.firefly.core.orchestrator.interfaces.services.CustomerService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Worker component that handles customer-related tasks in Camunda Zeebe workflows.
 * Provides job workers for creating legal and natural persons and storing their data.
 */
@Component
@Slf4j
public class CustomerWorker {

    private static final String EXTERNAL_REFERENCE_ID = "externalReferenceId";

    private final CustomerService customerService;

    /**
     * Constructs a new CustomerWorker with the specified customer service.
     *
     * @param customerService The service used to communicate with the customer service
     */
    public CustomerWorker(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Job worker that handles the creation of legal persons in the external BaaS system.
     *
     * @param job The activated job containing the legal person data
     * @return A Mono containing the customer response
     */
    @JobWorker(type = "baas-create-legal-person")
    public Mono<CustomerResponse> baasCreateLegalPerson(final ActivatedJob job) {
        log.info("Executing baas-create-legal-person task for job: {}", job.getKey());

        // Get variables from the process
        LegalPersonRequest userData = job.getVariablesAsType(LegalPersonRequest.class);

        log.info("Delegating legal person creation: {}", userData.legalName());

        // Delegate to the customer service
        return customerService.createLegalPerson(userData);
    }

    /**
     * Job worker that handles the creation of natural persons in the external BaaS system.
     *
     * @param job The activated job containing the natural person data
     * @return A Mono containing the customer response
     */
    @JobWorker(type = "baas-create-natural-person")
    public Mono<CustomerResponse> baasCreateNaturalPerson(final ActivatedJob job) {
        log.info("Executing baas-create-natural-person task for job: {}", job.getKey());

        // Get variables from the process
        NaturalPersonRequest userData = job.getVariablesAsType(NaturalPersonRequest.class);

        log.info("Delegating natural person creation: {}", userData.firstname());

        // Delegate to the customer service
        return customerService.createNaturalPerson(userData);
    }

    /**
     * Job worker that handles the creation of tax residence in the external BaaS system.
     *
     * @param job The activated job containing the tax residence data
     * @return A map containing the external reference ID
     */
    @JobWorker(type = "baas-create-tax-residence")
    public Mono<Map<String, Object>> baasCreateTaxResidence(final ActivatedJob job) {
        log.info("Executing baas-create-tax-residence task for job: {}", job.getKey());

        // Get variables from the process
        TaxResidenceRequest taxResidenceData = job.getVariablesAsType(TaxResidenceRequest.class);

        log.info("Delegating tax residence creation for userID: {}", taxResidenceData.userId());

        // Delegate to the customer service
        return customerService.createTaxResidence(taxResidenceData)
                .map(externalId -> {
                    // Prepare result for the process
                    Map<String, Object> result = new HashMap<>();
                    result.put(EXTERNAL_REFERENCE_ID, externalId);
                    return result;
                });
    }

    /**
     * Job worker that handles the KYC review process for a user.
     *
     * @param job The activated job containing the user ID
     * @return A map containing the process variables to pass to the next task
     */
    @JobWorker(type = "baas-start-kyc-review")
    public Mono<Map<String, Object>> baasStartKycReview(final ActivatedJob job) {
        log.info("Executing baas-start-kyc-review task for job: {}", job.getKey());

        // Get variables from the process
        Map<String, Object> variables = job.getVariablesAsMap();
        Integer userId = (Integer) variables.get("userId");

        log.info("Delegating KYC review for user ID: {}", userId);

        // Delegate to the customer service
        return customerService.startKycReview(userId)
                .map(externalId -> {
                    // Prepare result for the process
                    Map<String, Object> result = new HashMap<>();
                    result.put(EXTERNAL_REFERENCE_ID, externalId);
                    result.put("userId", userId);
                    return result;
                });
    }

    /**
     * Job worker that handles the KYB review process for a user.
     *
     * @param job The activated job containing the user ID
     * @return A map containing the process variables to pass to the next task
     */
    @JobWorker(type = "baas-start-kyb-review")
    public Mono<Map<String, Object>> baasStartKybReview(final ActivatedJob job) {
        log.info("Executing baas-start-kyb-review task for job: {}", job.getKey());

        // Get variables from the process
        Map<String, Object> variables = job.getVariablesAsMap();
        Integer userId = (Integer) variables.get("userId");

        log.info("Delegating KYB review for user ID: {}", userId);

        // Delegate to the customer service
        return customerService.startKybReview(userId)
                .map(externalId -> {
                    // Prepare result for the process
                    Map<String, Object> result = new HashMap<>();
                    result.put(EXTERNAL_REFERENCE_ID, externalId);
                    result.put("userId", userId);
                    return result;
                });
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

        log.info("Delegating storage of legal person data for: {}", userData.legalName());

        // Delegate to the customer service
        return customerService.storeLegalPersonData(userData);
    }
}