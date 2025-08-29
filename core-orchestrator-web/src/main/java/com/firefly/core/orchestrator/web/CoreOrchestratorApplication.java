package com.firefly.core.orchestrator.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main application class for the Core Orchestrator service.
 * This service orchestrates customer-related processes using Camunda Zeebe.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.firefly.core.orchestrator", "com.firefly.baas"})
public class CoreOrchestratorApplication {
    /**
     * The main entry point for the application.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(CoreOrchestratorApplication.class, args);
    }
}
