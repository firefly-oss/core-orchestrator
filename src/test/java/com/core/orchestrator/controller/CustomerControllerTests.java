package com.core.orchestrator.controller;

import com.catalis.baas.dtos.customers.LegalPersonAdapterDTO;
import com.catalis.baas.dtos.customers.NaturalPersonAdapterDTO;
import io.camunda.zeebe.client.ZeebeClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the CustomerController class.
 * These tests focus on the exception handling behavior of the controller.
 */
@ExtendWith(MockitoExtension.class)
class CustomerControllerTests {

    @Mock
    private ZeebeClient zeebeClient;

    @InjectMocks
    private CustomerController customerController;

    /**
     * Test handling of exceptions when creating a legal person process.
     */
    @Test
    void startCreateLegalPersonProcess_Exception() {
        // Arrange
        LegalPersonAdapterDTO legalPersonDTO = LegalPersonAdapterDTO.builder()
                .legalName("Test Company").build();

        // Setup to throw an exception when the ZeebeClient is used
        when(zeebeClient.newCreateInstanceCommand()).thenThrow(new RuntimeException("Test exception"));
        
        // Act
        ResponseEntity<Map<String, Object>> response = customerController.startCreateLegalPersonProcess(legalPersonDTO);
        
        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Failed to start process", response.getBody().get("error"));
        assertEquals("Test exception", response.getBody().get("message"));
    }
    
    /**
     * Test handling of exceptions when creating a natural person process.
     */
    @Test
    void startCreateNaturalPersonProcess_Exception() {
        // Arrange
        NaturalPersonAdapterDTO naturalPersonDTO = NaturalPersonAdapterDTO.builder()
                .firstname("John")
                .lastname("Doe").build();

        // Setup to throw an exception when the ZeebeClient is used
        when(zeebeClient.newCreateInstanceCommand()).thenThrow(new RuntimeException("Test exception"));
        
        // Act
        ResponseEntity<Map<String, Object>> response = customerController.startCreateNaturalPersonProcess(naturalPersonDTO);
        
        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Failed to start process", response.getBody().get("error"));
        assertEquals("Test exception", response.getBody().get("message"));
    }
}