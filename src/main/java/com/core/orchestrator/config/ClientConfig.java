package com.core.orchestrator.config;

import com.core.orchestrator.client.NotificationsService;
import com.core.orchestrator.client.SCAService;
import com.core.orchestrator.client.ClientFactory;
import com.core.orchestrator.config.properties.NotificationsProperties;
import com.core.orchestrator.config.properties.ScaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for API clients.
 * Creates and configures API clients using the properties from the configuration.
 */
@Configuration
public class ClientConfig {

    private final ScaProperties scaProperties;
    private final NotificationsProperties notificationsProperties;
    private final ClientFactory clientFactory;

    public ClientConfig(ScaProperties scaProperties, NotificationsProperties notificationsProperties, ClientFactory clientFactory) {
        this.scaProperties = scaProperties;
        this.notificationsProperties = notificationsProperties;
        this.clientFactory = clientFactory;
    }

    @Bean
    public com.catalis.common.sca.sdk.invoker.ApiClient scaApiClient() {
        com.catalis.common.sca.sdk.invoker.ApiClient apiClient = new com.catalis.common.sca.sdk.invoker.ApiClient();
        apiClient.setBasePath(scaProperties.getBasePath());
        return apiClient;
    }

    @Bean
    public com.catalis.common.platform.notification.services.sdk.invoker.ApiClient notificationApiClient() {
        com.catalis.common.platform.notification.services.sdk.invoker.ApiClient apiClient = new com.catalis.common.platform.notification.services.sdk.invoker.ApiClient();
        apiClient.setBasePath(notificationsProperties.getBasePath());
        return apiClient;
    }

    @Bean
    public SCAService scaClient() {
        return clientFactory.createSCAClient();
    }

    @Bean
    public NotificationsService notificationsClient() {
        return clientFactory.createNotificationsClient();
    }

}
