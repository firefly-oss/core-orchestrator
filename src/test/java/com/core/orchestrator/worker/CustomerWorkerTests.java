package com.core.orchestrator.worker;

import com.catalis.baas.adapter.impl.CustomerAdapterImpl;
import com.catalis.baas.dtos.customers.LegalPersonAdapterDTO;
import com.catalis.baas.dtos.customers.NaturalPersonAdapterDTO;
import com.catalis.baas.dtos.customers.TaxResidenceAdapterDTO;
import com.google.protobuf.ServiceException;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the CustomerWorker class.
 */
@ExtendWith(MockitoExtension.class)
class CustomerWorkerTests {

    @Mock
    private CustomerAdapterImpl customerAdapter;

    @Mock
    private ActivatedJob job;

    @InjectMocks
    private CustomerWorker customerWorker;

    private LegalPersonAdapterDTO legalPersonDTO;
    private NaturalPersonAdapterDTO naturalPersonDTO;
    private TaxResidenceAdapterDTO taxResidenceDTO;
    private final String externalId = "ext-123";

    @BeforeEach
    void setUp() {
        // Setup test data
        legalPersonDTO = LegalPersonAdapterDTO.builder()
                .legalName("Test Company").build();

        naturalPersonDTO = NaturalPersonAdapterDTO.builder()
                .firstname("John")
                .lastname("Doe").build();

        taxResidenceDTO = TaxResidenceAdapterDTO.builder()
                .userId(123)
                .country("Spain")
                .build();

        // Setup job mock
        when(job.getKey()).thenReturn(123L);
    }

    /**
     * Test successful creation of a legal person.
     */
    @Test
    void baasCreateLegalPerson_Success() throws ServiceException {
        // Arrange
        when(job.getVariablesAsType(LegalPersonAdapterDTO.class)).thenReturn(legalPersonDTO);

        ResponseEntity<String> responseEntity = ResponseEntity.ok(externalId);
        when(customerAdapter.createLegalPerson(any(LegalPersonAdapterDTO.class)))
                .thenReturn(Mono.just(responseEntity));

        // Act
        Map<String, Object> result = customerWorker.baasCreateLegalPerson(job);

        // Assert
        assertNotNull(result);
        Object resultValue = result.get("externalReferenceId");
        assertNotNull(resultValue);
        assertTrue(resultValue instanceof Mono);

        // Verify the Mono contains the expected value
        Mono<String> monoResult = (Mono<String>) resultValue;
        assertEquals(externalId, monoResult.block());

        verify(customerAdapter).createLegalPerson(legalPersonDTO);
    }

    /**
     * Test handling of WebClientResponseException when creating a legal person.
     */
    @Test
    void baasCreateLegalPerson_WebClientResponseException() {
        // Arrange
        when(job.getVariablesAsType(LegalPersonAdapterDTO.class)).thenReturn(legalPersonDTO);

        WebClientResponseException exception = mock(WebClientResponseException.class);
        when(exception.getMessage()).thenReturn("API error");
        when(customerAdapter.createLegalPerson(any(LegalPersonAdapterDTO.class)))
                .thenThrow(exception);

        // Act & Assert
        ServiceException thrown = assertThrows(
                ServiceException.class,
                () -> customerWorker.baasCreateLegalPerson(job)
        );

        assertEquals("Failed to create legal person", thrown.getMessage());
        verify(customerAdapter).createLegalPerson(legalPersonDTO);
    }

    /**
     * Test handling of general exceptions when creating a legal person.
     */
    @Test
    void baasCreateLegalPerson_GeneralException() {
        // Arrange
        when(job.getVariablesAsType(LegalPersonAdapterDTO.class)).thenReturn(legalPersonDTO);

        RuntimeException exception = new RuntimeException("Test error");
        when(customerAdapter.createLegalPerson(any(LegalPersonAdapterDTO.class)))
                .thenThrow(exception);

        // Act & Assert
        ServiceException thrown = assertThrows(
                ServiceException.class,
                () -> customerWorker.baasCreateLegalPerson(job)
        );

        assertEquals("Unexpected error creating legal person", thrown.getMessage());
        verify(customerAdapter).createLegalPerson(legalPersonDTO);
    }

    /**
     * Test successful creation of a natural person.
     */
    @Test
    void baasCreateNaturalPerson_Success() {
        // Arrange
        when(job.getVariablesAsType(NaturalPersonAdapterDTO.class)).thenReturn(naturalPersonDTO);

        ResponseEntity<String> responseEntity = ResponseEntity.ok(externalId);
        when(customerAdapter.createNaturalPerson(any(NaturalPersonAdapterDTO.class)))
                .thenReturn(Mono.just(responseEntity));

        // Act
        Map<String, Object> result = customerWorker.baasCreateNaturalPerson(job);

        // Assert
        assertNotNull(result);
        assertEquals(externalId, result.get("externalReferenceId"));
        verify(customerAdapter).createNaturalPerson(naturalPersonDTO);
    }

    /**
     * Test storing legal person data.
     */
    @Test
    void storeLegalPersonData_Success() {
        // Arrange
        when(job.getVariablesAsType(LegalPersonAdapterDTO.class)).thenReturn(legalPersonDTO);

        Map<String, Object> variables = new HashMap<>();
        variables.put("externalReferenceId", externalId);
        when(job.getVariablesAsMap()).thenReturn(variables);

        // Act
        Map<String, Object> result = customerWorker.storeLegalPersonData(job);

        // Assert
        assertNotNull(result);
        assertEquals(externalId, result.get("externalReferenceId"));
        // We can't directly verify the mockDatabaseStore method as it's private,
        // but we can verify that the job methods were called
        verify(job).getVariablesAsMap();
        verify(job).getVariablesAsType(LegalPersonAdapterDTO.class);
    }

    /**
     * Test successful creation of a tax residence.
     */
    @Test
    void baasCreateTaxResidence_Success() throws ServiceException {
        // Arrange
        when(job.getVariablesAsType(TaxResidenceAdapterDTO.class)).thenReturn(taxResidenceDTO);

        ResponseEntity<String> responseEntity = ResponseEntity.ok(externalId);
        when(customerAdapter.createTaxResidence(any(TaxResidenceAdapterDTO.class)))
                .thenReturn(Mono.just(responseEntity));

        // Act
        Map<String, Object> result = customerWorker.baasCreateTaxResidence(job);

        // Assert
        assertNotNull(result);
        assertEquals(externalId, result.get("externalReferenceId"));
        verify(customerAdapter).createTaxResidence(taxResidenceDTO);
    }

    /**
     * Test handling of WebClientResponseException when creating a tax residence.
     */
    @Test
    void baasCreateTaxResidence_WebClientResponseException() {
        // Arrange
        when(job.getVariablesAsType(TaxResidenceAdapterDTO.class)).thenReturn(taxResidenceDTO);

        WebClientResponseException exception = mock(WebClientResponseException.class);
        when(exception.getMessage()).thenReturn("API error");
        when(customerAdapter.createTaxResidence(any(TaxResidenceAdapterDTO.class)))
                .thenThrow(exception);

        // Act & Assert
        ServiceException thrown = assertThrows(
                ServiceException.class,
                () -> customerWorker.baasCreateTaxResidence(job)
        );

        assertEquals("Failed to create tax residence", thrown.getMessage());
        verify(customerAdapter).createTaxResidence(taxResidenceDTO);
    }

    /**
     * Test handling of general exceptions when creating a tax residence.
     */
    @Test
    void baasCreateTaxResidence_GeneralException() {
        // Arrange
        when(job.getVariablesAsType(TaxResidenceAdapterDTO.class)).thenReturn(taxResidenceDTO);

        RuntimeException exception = new RuntimeException("Test error");
        when(customerAdapter.createTaxResidence(any(TaxResidenceAdapterDTO.class)))
                .thenThrow(exception);

        // Act & Assert
        ServiceException thrown = assertThrows(
                ServiceException.class,
                () -> customerWorker.baasCreateTaxResidence(job)
        );

        assertEquals("Unexpected error creating tax residence", thrown.getMessage());
        verify(customerAdapter).createTaxResidence(taxResidenceDTO);
    }
}
