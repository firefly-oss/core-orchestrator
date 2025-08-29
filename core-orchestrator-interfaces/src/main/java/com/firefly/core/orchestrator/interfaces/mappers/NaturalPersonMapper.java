package com.firefly.core.orchestrator.interfaces.mappers;

import com.firefly.baas.dtos.customers.NaturalPersonAdapterDTO;
import com.firefly.core.orchestrator.interfaces.dtos.accounts.NaturalPersonRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for converting between natural person-related DTOs and API request/response objects.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        implementationName = "OrchestratorNaturalPersonMapperImpl")
public abstract class NaturalPersonMapper {

    public abstract NaturalPersonAdapterDTO requestToDTO(NaturalPersonRequest request);

}