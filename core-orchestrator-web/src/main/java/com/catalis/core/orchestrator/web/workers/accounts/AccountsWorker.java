package com.catalis.core.orchestrator.web.workers.accounts;

import com.catalis.baas.adapter.AccountAdapter;
import com.catalis.baas.dtos.accounts.AccountAdapterDTO;
import com.catalis.baas.dtos.customers.LegalPersonAdapterDTO;
import com.catalis.core.orchestrator.interfaces.dtos.accounts.AccountRequest;
import com.catalis.core.orchestrator.interfaces.dtos.accounts.AccountResponse;
import com.catalis.core.orchestrator.interfaces.mappers.AccountMapper;
import com.google.protobuf.ServiceException;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Worker component that handles account-related tasks in Camunda Zeebe workflows.
 * Provides job workers for creating accounts.
 */
@Component
@Slf4j
public class AccountsWorker {

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
    public Mono<AccountResponse> baasCreateAccount(final ActivatedJob job) throws ServiceException {
        log.info("Executing baas-create-account task for job: {}", job.getKey());

        // Get variables from the process
        AccountRequest accountData = job.getVariablesAsType(AccountRequest.class);

        log.info("Creating account for user ID: {}", accountData.userId());

        // Call the external microservice
        Mono<AccountAdapterDTO> accountAdapterDTO;
        try {
            accountAdapterDTO = accountAdapter.createAccount(accountMapper.requestToDTO(accountData))
                    .mapNotNull(ResponseEntity::getBody);
        } catch (
                WebClientResponseException e) {
            log.error("Error calling external service: {}", e.getMessage());
            throw new ServiceException("Failed to create account", e);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            throw new ServiceException("Unexpected error creating account", e);
        }
        log.info("External ID retrieved successfully");

        return accountAdapterDTO.map(accountMapper::dtoToResponse);
    }

}