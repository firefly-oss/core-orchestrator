package com.core.orchestrator.worker;

import com.catalis.baas.adapter.AccountAdapter;
import com.catalis.baas.dtos.accounts.AccountAdapterDTO;
import com.catalis.baas.dtos.customers.TaxResidenceAdapterDTO;
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
 * Worker component that handles account-related tasks in Camunda Zeebe workflows.
 * Provides job workers for creating accounts.
 */
@Component
public class AccountsWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountsWorker.class);

    private static final String EXTERNAL_REFERENCE_ID = "externalReferenceId";

    private final AccountAdapter accountAdapter;

    /**
     * Default constructor for AccountsWorker.
     */
    public AccountsWorker(AccountAdapter accountAdapter) {
        this.accountAdapter = accountAdapter;
    }

    /**
     * Job worker that handles the creation of accounts in the external BaaS system.
     *
     * @param job The activated job containing the account data
     * @return A map containing the external reference ID
     * @throws ServiceException If there's an error calling the external service
     */
    @JobWorker(type = "baas-create-account")
    public Map<String, Object> baasCreateAccount(final ActivatedJob job) throws ServiceException {
        LOGGER.info("Executing baas-create-account task for job: {}", job.getKey());

        // Get variables from the process
        AccountAdapterDTO accountData = job.getVariablesAsType(AccountAdapterDTO.class);

        LOGGER.info("Creating account for user ID: {}", accountData.userId());

        // Call the external microservice
        String externalId = Objects.requireNonNull(accountAdapter.createAccount(accountData).block()).getBody();

        LOGGER.info("Account created successfully with ID: {}", externalId);

        // Prepare result for the process
        Map<String, Object> result = new HashMap<>();
        result.put(EXTERNAL_REFERENCE_ID, externalId);

        return result;
    }
}