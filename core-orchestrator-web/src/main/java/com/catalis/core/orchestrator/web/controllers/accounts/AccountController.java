package com.catalis.core.orchestrator.web.controllers.accounts;

import com.catalis.baas.dtos.accounts.AccountAdapterDTO;
import com.catalis.baas.dtos.customers.TaxResidenceAdapterDTO;
import com.catalis.core.orchestrator.interfaces.dtos.accounts.AccountRequest;
import com.catalis.core.orchestrator.web.controllers.BaseController;
import io.camunda.zeebe.client.ZeebeClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller that handles account-related API endpoints.
 * Provides endpoints for creating accounts by starting Camunda Zeebe processes.
 */
@RestController
@RequestMapping("/api/v1/accounts")
@Slf4j
public class AccountController extends BaseController {
    /**
     * Constructs a new AccountController with the specified Zeebe client.
     *
     * @param zeebeClient The client used to interact with the Camunda Zeebe workflow engine
     */
    @Autowired
    public AccountController(ZeebeClient zeebeClient) {
        super(zeebeClient);
    }

    /**
     * Starts a process to create an account.
     *
     * @param accountData The account data to be processed
     * @return A response containing the process instance key and status
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> startCreateAccountProcess(@RequestBody AccountRequest accountData) {
        log.info("Starting create-account process with data: {}", accountData);

        try {
            return startProcess(CREATE_ACCOUNT, accountData);
        } catch (Exception e) {
            log.error("Error starting process: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to start process", "message", e.getMessage()));
        }
    }
}