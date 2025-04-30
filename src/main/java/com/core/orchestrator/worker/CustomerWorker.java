package com.core.orchestrator.worker;

import com.catalis.baas.adapter.CustomerAdapter;
import com.catalis.baas.adapter.impl.CustomerAdapterImpl;
import com.catalis.core.customers.interfaces.dtos.FrontLegalPersonDTO;
import com.catalis.core.customers.interfaces.dtos.FrontNaturalPersonDTO;
import com.google.protobuf.ServiceException;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class CustomerWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerWorker.class);

    private static final String EXTERNAL_REFERENCE_ID = "externalReferenceId";


    private final CustomerAdapter customerAdapter;

    /**
     * Constructs a new CustomerWorker with the specified customer adapter.
     *
     * @param customerAdapter The adapter used to communicate with the customer service
     */
    @Autowired
    public CustomerWorker(CustomerAdapterImpl customerAdapter) {
        this.customerAdapter = customerAdapter;
    }

    /**
     * Job worker that handles the creation of legal persons in the external BaaS system.
     *
     * @param job The activated job containing the legal person data
     * @return A map containing the external reference ID
     * @throws ServiceException If there's an error calling the external service
     */
    @JobWorker(type = "baas-create-legal-person")
    public Map<String, Object> baasCreateLegalPerson(final ActivatedJob job) throws ServiceException {
        LOGGER.info("Executing baas-create-legal-person task for job: {}", job.getKey());

        // Get variables from the process
        FrontLegalPersonDTO userData = job.getVariablesAsType(FrontLegalPersonDTO.class);

        LOGGER.info("Creating legal person: {}", userData.getLegalName());

        // Call the external microservice
        Mono<String> externalId;
        try {
            externalId = customerAdapter.createLegalPerson(userData)
                    .mapNotNull(ResponseEntity::getBody);
        } catch (WebClientResponseException e) {
            LOGGER.error("Error calling external service: {}", e.getMessage());
            throw new ServiceException("Failed to create legal person", e);
        } catch (Exception e) {
            LOGGER.error("Unexpected error: {}", e.getMessage());
            throw new ServiceException("Unexpected error creating legal person", e);
        }
        LOGGER.info("External ID retrieved successfully");

        // Prepare result for the process
        Map<String, Object> result = new HashMap<>();
        result.put(EXTERNAL_REFERENCE_ID, externalId);

        return result;
    }

    /**
     * Job worker that handles the creation of natural persons in the external BaaS system.
     *
     * @param job The activated job containing the natural person data
     * @return A map containing the external reference ID
     */
    @JobWorker(type = "baas-create-natural-person")
    public Map<String, Object> baasCreateNaturalPerson(final ActivatedJob job) {
        LOGGER.info("Executing baas-create-natural-person task for job: {}", job.getKey());

        // Get variables from the process
        FrontNaturalPersonDTO userData = job.getVariablesAsType(FrontNaturalPersonDTO.class);

        LOGGER.info("Creating natural person: {}", userData.getFirstname());

        // Call the external microservice
        // Note: Using createLegalPerson for now, in a real implementation this would call a method specific to natural persons
        String externalId = Objects.requireNonNull(customerAdapter.createNaturalPerson(userData).block()).getBody();

        LOGGER.info("External ID retrieved successfully: {}", externalId);

        // Prepare result for the process
        Map<String, Object> result = new HashMap<>();
        result.put(EXTERNAL_REFERENCE_ID, externalId);

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
        LOGGER.info("Executing store-legal-person-data task for job: {}", job.getKey());

        // Get variables from the process
        Map<String, Object> variables = job.getVariablesAsMap();
        String externalId = (String) variables.get(EXTERNAL_REFERENCE_ID);
        FrontLegalPersonDTO userData = job.getVariablesAsType(FrontLegalPersonDTO.class);

        LOGGER.info("Storing legal person data for: {} with external ID: {}", userData.getLegalName(), externalId);

        // Mock database storage
        mockDatabaseStore(userData, externalId);

        LOGGER.info("Legal person data stored successfully in database");

        // Return the variables to pass them to the next task
        return variables;
    }

    // Mock method to simulate database storage
    private void mockDatabaseStore(FrontLegalPersonDTO userData, String externalId) {
        // This is a mock method that simulates storing data in a database
        // In a real implementation, this would connect to a database and store the data
        LOGGER.info("MOCK DB: Storing legal person {} with external ID {} in database", userData.getLegalName(), externalId);
    }
}
