package com.core.orchestrator.controller;

import com.catalis.core.customers.interfaces.dtos.FrontLegalPersonDTO;
import com.catalis.core.customers.interfaces.dtos.person.v1.LegalPersonDTO;
import io.camunda.zeebe.client.ZeebeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/customers")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final ZeebeClient zeebeClient;

    @Autowired
    public UserController(ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    @PostMapping(value = "/create-legal-person")
    public ResponseEntity<Map<String, Object>> startCreateLegalPersonProcess(@RequestBody FrontLegalPersonDTO userData) {
        LOGGER.info("Starting create-legal-person process with data: {}", userData);

        // Iniciar el proceso en Camunda
        final var processInstanceEvent = zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId("create-legal-person")
                .latestVersion()
                .variables(userData)
                .send()
                .join();

        // Preparar respuesta
        Map<String, Object> response = Map.of(
                "processInstanceKey", processInstanceEvent.getProcessInstanceKey(),
                "status", "started"
        );

        return ResponseEntity.ok(response);
    }
}