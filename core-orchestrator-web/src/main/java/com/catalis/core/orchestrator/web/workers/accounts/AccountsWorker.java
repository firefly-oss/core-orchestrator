package com.catalis.core.orchestrator.web.workers.accounts;

import com.catalis.core.orchestrator.interfaces.dtos.accounts.AccountRequest;
import com.catalis.core.orchestrator.interfaces.dtos.accounts.AccountResponse;
import com.catalis.core.orchestrator.interfaces.services.AccountService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Worker component that handles account-related tasks in Camunda Zeebe workflows.
 * Provides job workers for creating accounts.
 */
@Component
@Slf4j
public class AccountsWorker {

    private final AccountService accountService;

    /**
     * Default constructor for AccountsWorker.
     */
    public AccountsWorker(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Job worker that handles the creation of accounts in the external BaaS system.
     *
     * @param job The activated job containing the account data
     * @return A Mono containing the account response
     */
    @JobWorker(type = "baas-create-account")
    public Mono<AccountResponse> baasCreateAccount(final ActivatedJob job) {
        log.info("Executing baas-create-account task for job: {}", job.getKey());

        // Get variables from the process
        AccountRequest accountData = job.getVariablesAsType(AccountRequest.class);

        log.info("Delegating account creation for user ID: {}", accountData.userId());

        // Delegate to the account service
        return accountService.createAccount(accountData);
    }

}
