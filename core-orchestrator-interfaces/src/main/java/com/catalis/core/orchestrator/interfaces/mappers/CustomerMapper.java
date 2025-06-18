package com.catalis.core.orchestrator.interfaces.mappers;

import com.catalis.baas.dtos.customers.LegalPersonAdapterDTO;
import com.catalis.baas.dtos.customers.NaturalPersonAdapterDTO;
import com.catalis.core.orchestrator.interfaces.dtos.customers.CustomerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for converting between customer-related DTOs and API request/response objects.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        implementationName = "OrchestratorCustomerMapperImpl")
public abstract class CustomerMapper {

    public abstract CustomerResponse legalPersonDTOToResponse(LegalPersonAdapterDTO legalPersonAdapterDTO);
    public abstract CustomerResponse naturalPersonDTOToResponse(NaturalPersonAdapterDTO naturalPersonAdapterDTO);

}
