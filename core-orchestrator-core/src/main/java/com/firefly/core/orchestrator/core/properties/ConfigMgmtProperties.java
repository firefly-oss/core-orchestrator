package com.firefly.core.orchestrator.core.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for Configuration Management API.
 * Maps the properties defined in application.yaml under api-configuration.config-mgmt.
 */
@Configuration
@ConfigurationProperties(prefix = "api-configuration.config-mgmt")
@Getter
@Setter
public class ConfigMgmtProperties {

    private String basePath;

}
