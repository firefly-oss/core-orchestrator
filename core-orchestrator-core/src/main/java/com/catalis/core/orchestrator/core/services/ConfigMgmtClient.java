package com.catalis.core.orchestrator.core.services;

import com.catalis.common.config.sdk.api.ProviderProcessVersionsApi;
import com.catalis.common.config.sdk.api.ProvidersApi;
import com.catalis.common.config.sdk.invoker.ApiClient;
import com.catalis.common.config.sdk.model.FilterRequestProviderProcessDTO;
import com.catalis.common.config.sdk.model.PaginationResponseProviderProcessDTO;
import com.catalis.common.config.sdk.model.ProviderProcessDTO;
import com.catalis.common.config.sdk.model.ProviderProcessVersionDTO;
import com.catalis.core.orchestrator.interfaces.services.ConfigMgmtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Implementation of the ConfigMgmtService interface.
 * Provides methods for retrieving provider processes and their versions from the Configuration Management API.
 */
@Service
public class ConfigMgmtClient implements ConfigMgmtService {

    private final ProvidersApi providersApi;
    private final ProviderProcessVersionsApi providerProcessVersionsApi;

    /**
     * Creates a new ConfigMgmtClient with the specified API client.
     *
     * @param apiClient the API client to use for configuration management operations
     */
    @Autowired
    public ConfigMgmtClient(ApiClient apiClient) {
        this.providersApi = new ProvidersApi(apiClient);
        this.providerProcessVersionsApi = new ProviderProcessVersionsApi(apiClient);

    }

    /**
     * Retrieves provider processes based on the given provider ID.
     *
     * @param providerId the ID of the provider for which processes are to be retrieved
     * @return a Mono containing the response entity, which includes a paginated list of provider processes
     */
    @Override
    public Mono<ResponseEntity<PaginationResponseProviderProcessDTO>> getProviderProcesses(Long providerId){
        // Create a new filter DTO
        FilterRequestProviderProcessDTO filterRequestProviderProcessDTO = new FilterRequestProviderProcessDTO();
        filterRequestProviderProcessDTO.setFilters(new ProviderProcessDTO());

        // Generate a random xIdempotencyKey
        String xIdempotencyKey = UUID.randomUUID().toString();

        return providersApi.filterProviderProcessesWithHttpInfo(providerId, filterRequestProviderProcessDTO, xIdempotencyKey);
    }

    /**
     * Retrieves a provider process version based on the given process ID.
     *
     * @param providerProcessId the ID of the provider process for which to retrieve the version
     * @return a Mono containing the response entity with the provider process version
     */
    @Override
    public Mono<ResponseEntity<ProviderProcessVersionDTO>> getProviderProcessVersion(Long providerProcessId) {

        // Generate a random xIdempotencyKey
        String xIdempotencyKey = UUID.randomUUID().toString();

        return providerProcessVersionsApi.getProviderProcessVersionById1WithHttpInfo(providerProcessId);
    }


}
