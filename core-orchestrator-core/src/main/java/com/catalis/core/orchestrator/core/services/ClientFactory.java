package com.catalis.core.orchestrator.core.services;

import com.catalis.core.orchestrator.interfaces.properties.NotificationsProperties;
import com.catalis.core.orchestrator.interfaces.properties.ScaProperties;
import com.catalis.core.orchestrator.interfaces.services.NotificationsService;
import com.catalis.core.orchestrator.interfaces.services.SCAService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

/**
 * Default implementation of the ClientFactory interface.
 * Creates client service instances using the appropriate API clients and dependencies.
 */
@Component
public class ClientFactory{

    private final ScaProperties scaProperties;
    private final NotificationsProperties notificationsProperties;
    private final ObjectMapper objectMapper;

    public ClientFactory(ScaProperties scaProperties,
                         NotificationsProperties notificationsProperties,
                         ObjectMapper objectMapper) {
        this.scaProperties = scaProperties;
        this.notificationsProperties = notificationsProperties;
        this.objectMapper = objectMapper;
    }

    /**
     * Creates and returns an SCA service client.
     *
     * @return A configured SCA service client
     */
    public SCAService createSCAClient() {
        com.catalis.common.sca.sdk.invoker.ApiClient apiClient = new com.catalis.common.sca.sdk.invoker.ApiClient();
        apiClient.setBasePath(scaProperties.getBasePath());
        return new SCAClient(apiClient, objectMapper);
    }

    /**
     * Creates and returns a Notifications service client.
     *
     * @return A configured Notifications service client
     */
    public NotificationsService createNotificationsClient() {
        com.catalis.common.platform.notification.services.sdk.invoker.ApiClient apiClient = new com.catalis.common.platform.notification.services.sdk.invoker.ApiClient();
        apiClient.setBasePath(notificationsProperties.getBasePath());
        return new NotificationsClient(apiClient, objectMapper);
    }
}
