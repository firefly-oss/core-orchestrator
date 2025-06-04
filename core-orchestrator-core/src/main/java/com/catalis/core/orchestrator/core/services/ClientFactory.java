package com.catalis.core.orchestrator.core.services;

import com.catalis.core.orchestrator.core.properties.ConfigMgmtProperties;
import com.catalis.core.orchestrator.core.properties.NotificationsProperties;
import com.catalis.core.orchestrator.core.properties.ScaProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Default implementation of the ClientFactory interface.
 * Creates client service instances using the appropriate API clients and dependencies.
 */
@Component
public class ClientFactory{

    private final ScaProperties scaProperties;
    private final NotificationsProperties notificationsProperties;
    private final ConfigMgmtProperties configMgmtProperties;
    private final ObjectMapper objectMapper;

    @Autowired
    public ClientFactory(ScaProperties scaProperties,
                         NotificationsProperties notificationsProperties, ConfigMgmtProperties configMgmtProperties,
                         ObjectMapper objectMapper) {
        this.scaProperties = scaProperties;
        this.notificationsProperties = notificationsProperties;
        this.configMgmtProperties = configMgmtProperties;
        this.objectMapper = objectMapper;
    }

    /**
     * Creates and returns an SCA service client.
     *
     * @return A configured SCA service client
     */
    @Bean
    public com.catalis.common.sca.sdk.invoker.ApiClient createSCAClient() {
        com.catalis.common.sca.sdk.invoker.ApiClient apiClient = new com.catalis.common.sca.sdk.invoker.ApiClient();
        apiClient.setBasePath(scaProperties.getBasePath());
        return apiClient;
    }

    /**
     * Creates and returns a Notifications service client.
     *
     * @return A configured Notifications service client
     */
    @Bean
    public com.catalis.common.platform.notification.services.sdk.invoker.ApiClient createNotificationsClient() {
        com.catalis.common.platform.notification.services.sdk.invoker.ApiClient apiClient = new com.catalis.common.platform.notification.services.sdk.invoker.ApiClient();
        apiClient.setBasePath(notificationsProperties.getBasePath());
        return apiClient;
    }

    /**
     * Creates and returns an API client for the configuration management service.
     *
     * @return A configured configuration management service API client
     */
    @Bean
    public com.catalis.common.config.sdk.invoker.ApiClient createConfigMgmtClient() {
        com.catalis.common.config.sdk.invoker.ApiClient apiClient = new com.catalis.common.config.sdk.invoker.ApiClient();
        apiClient.setBasePath(configMgmtProperties.getBasePath());
        return apiClient;
    }
}
