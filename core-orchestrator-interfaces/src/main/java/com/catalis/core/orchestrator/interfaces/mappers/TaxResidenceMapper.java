package com.catalis.core.orchestrator.interfaces.mappers;

import com.catalis.baas.dtos.customers.TaxResidenceAdapterDTO;
import com.catalis.core.orchestrator.interfaces.dtos.accounts.TaxResidenceRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for converting between tax residence-related DTOs and API request/response objects.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        implementationName = "OrchestratorTaxResidenceMapperImpl")
public abstract class TaxResidenceMapper {

    public abstract TaxResidenceAdapterDTO requestToDTO(TaxResidenceRequest request);

}