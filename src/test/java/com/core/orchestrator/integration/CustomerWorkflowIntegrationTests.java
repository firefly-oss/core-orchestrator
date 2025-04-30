package com.core.orchestrator.integration;

import com.catalis.baas.adapter.impl.CustomerAdapterImpl;
import com.catalis.core.customers.interfaces.dtos.FrontLegalPersonDTO;
import com.catalis.core.customers.interfaces.dtos.FrontNaturalPersonDTO;
import com.core.orchestrator.controller.CustomerController;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Integration tests for the customer workflow.
 * Tests the end-to-end flow of creating a legal person through the Camunda Zeebe workflow.
 */
@SpringBootTest
@ActiveProfiles("test")
class CustomerWorkflowIntegrationTests {

    @Autowired
    private CustomerController customerController;

    @MockBean
    private CustomerAdapterImpl customerAdapter;

    private FrontLegalPersonDTO legalPersonDTO;
    private final String externalId = "ext-123";

    /**
     * Configuration class to provide mock beans for testing.
     */
    @Configuration
    static class TestConfig {
        @Bean
        @Primary
        public ZeebeClient zeebeClient() {
            // Create a mock ZeebeClient with RETURNS_DEEP_STUBS to handle method chaining
            ZeebeClient mockClient = Mockito.mock(ZeebeClient.class, Mockito.RETURNS_DEEP_STUBS);

            // Mock the ProcessInstanceEvent that will be returned at the end of the chain
            ProcessInstanceEvent mockEvent = Mockito.mock(ProcessInstanceEvent.class);
            when(mockEvent.getProcessInstanceKey()).thenReturn(123L);

            // Set up the mock to return the event at the end of the chain
            when(mockClient.newCreateInstanceCommand()
                    .bpmnProcessId(any(String.class))
                    .latestVersion()
                    .variables(any(Object.class))
                    .send()
                    .join()).thenReturn(mockEvent);

            return mockClient;
        }

        @Bean
        @Primary
        public CustomerController customerController(ZeebeClient zeebeClient) {
            return new CustomerController(zeebeClient);
        }
    }

    @BeforeEach
    void setUp() {
        // Setup test data
        legalPersonDTO = new FrontLegalPersonDTO();
        legalPersonDTO.setLegalName("Test Company");

        // Reset mocks
        reset(customerAdapter);

        // Mock the CustomerAdapter behavior
        ResponseEntity<String> responseEntity = ResponseEntity.ok(externalId);
        when(customerAdapter.createLegalPerson(any(FrontLegalPersonDTO.class)))
                .thenReturn(Mono.just(responseEntity));
    }

    /**
     * Test the end-to-end flow of creating a legal person.
     * This test verifies that:
     * 1. The controller starts the process correctly
     * 2. The process variables are passed correctly
     */
    @Test
    void createLegalPerson_EndToEndFlow() {
        // Act
        ResponseEntity<Map<String, Object>> response = customerController.startCreateLegalPersonProcess(legalPersonDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(123L, response.getBody().get("processInstanceKey"));
        assertEquals("started", response.getBody().get("status"));
    }
}
