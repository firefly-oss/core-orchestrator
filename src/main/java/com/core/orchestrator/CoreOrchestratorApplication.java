package com.core.orchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.core.orchestrator", "com.catalis.baas"})
public class CoreOrchestratorApplication {
    public static void main(String[] args) {
        SpringApplication.run(CoreOrchestratorApplication.class, args);
    }
}