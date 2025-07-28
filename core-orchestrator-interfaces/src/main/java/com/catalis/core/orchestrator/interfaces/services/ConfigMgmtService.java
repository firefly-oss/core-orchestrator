package com.catalis.core.orchestrator.interfaces.services;

import com.catalis.common.config.sdk.model.PaginationResponseProviderProcessDTO;
import com.catalis.common.config.sdk.model.ProviderProcessVersionDTO;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

/**
 * Interface for configuration management operations.
 * Provides methods for retrieving provider processes and their versions.
 */
public interface ConfigMgmtService {


    /**
     * Retrieves processes for a specific provider.
     *
     * @param providerId the ID of the provider for which processes are to be retrieved
     * @return a Mono containing the response entity, which includes a paginated list of provider processes
     */
    Mono<ResponseEntity<PaginationResponseProviderProcessDTO>> getProviderProcesses(Long providerId);

    /**
     * Retrieves a provider process version based on the given process ID.
     *
     * @param providerProcessId the ID of the provider process for which to retrieve the version
     * @return a Mono containing the response entity with the provider process version
     */
    Mono<ResponseEntity<ProviderProcessVersionDTO>> getProviderProcessVersion(Long providerProcessId);
}
