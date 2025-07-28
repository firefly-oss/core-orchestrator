package com.catalis.core.orchestrator.interfaces.services;

import com.catalis.core.orchestrator.interfaces.dtos.accounts.AccountRequest;
import com.catalis.core.orchestrator.interfaces.dtos.accounts.AccountResponse;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

/**
 * Interface for account-related operations.
 * Provides methods for creating and managing accounts.
 */
public interface AccountService {

    /**
     * Creates an account in the external BaaS system.
     *
     * @param accountRequest the account data to create
     * @return a Mono containing the response with the created account data
     */
    Mono<AccountResponse> createAccount(AccountRequest accountRequest);
}