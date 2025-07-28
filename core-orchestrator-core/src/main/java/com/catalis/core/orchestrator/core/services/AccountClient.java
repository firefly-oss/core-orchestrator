package com.catalis.core.orchestrator.core.services;

import com.catalis.baas.adapter.AccountAdapter;
import com.catalis.baas.dtos.accounts.AccountAdapterDTO;
import com.catalis.core.orchestrator.interfaces.dtos.accounts.AccountRequest;
import com.catalis.core.orchestrator.interfaces.dtos.accounts.AccountResponse;
import com.catalis.core.orchestrator.interfaces.mappers.AccountMapper;
import com.catalis.core.orchestrator.interfaces.services.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Implementation of the AccountService interface.
 * Provides methods for creating and managing accounts using the BaaS adapter.
 */
@Service
@Slf4j
public class AccountClient implements AccountService {

    private final AccountAdapter accountAdapter;
    private final AccountMapper accountMapper;

    /**
     * Creates a new AccountClient with the specified adapter and mapper.
     *
     * @param accountAdapter the adapter for interacting with the BaaS system
     * @param accountMapper the mapper for converting between DTOs
     */
    @Autowired
    public AccountClient(AccountAdapter accountAdapter, AccountMapper accountMapper) {
        this.accountAdapter = accountAdapter;
        this.accountMapper = accountMapper;
    }

    /**
     * Creates an account in the external BaaS system.
     *
     * @param accountRequest the account data to create
     * @return a Mono containing the response with the created account data
     */
    @Override
    public Mono<AccountResponse> createAccount(AccountRequest accountRequest) {
        log.info("Creating account for user ID: {}", accountRequest.userId());

        // Call the external microservice
        return accountAdapter.createAccount(accountMapper.requestToDTO(accountRequest))
                .mapNotNull(ResponseEntity::getBody)
                .map(accountMapper::dtoToResponse);

    }
}
