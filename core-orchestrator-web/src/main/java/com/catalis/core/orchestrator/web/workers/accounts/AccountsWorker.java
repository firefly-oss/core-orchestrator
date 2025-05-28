package com.catalis.core.orchestrator.web.workers.accounts;

import com.catalis.baas.adapter.AccountAdapter;
import com.catalis.core.orchestrator.interfaces.dtos.accounts.AccountRequest;
import com.catalis.core.orchestrator.interfaces.mappers.AccountMapper;
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
    private final AccountMapper accountMapper;

    /**
     * Default constructor for AccountsWorker.
     */
    public AccountsWorker(AccountAdapter accountAdapter, AccountMapper accountMapper) {
        this.accountAdapter = accountAdapter;
        this.accountMapper = accountMapper;
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
        AccountRequest accountData = job.getVariablesAsType(AccountRequest.class);

        LOGGER.info("Creating account for user ID: {}", accountData.userId());

        // Call the external microservice
        String externalId = Objects.requireNonNull(accountAdapter.createAccount(accountMapper.requestToDTO(accountData)).block()).getBody();

        LOGGER.info("Account created successfully with ID: {}", externalId);

        // Prepare result for the process
        Map<String, Object> result = new HashMap<>();
        result.put(EXTERNAL_REFERENCE_ID, externalId);

        return result;
    }
}