package com.catalis.core.orchestrator.interfaces.mappers;

import com.catalis.baas.dtos.accounts.AccountAdapterDTO;
import com.catalis.baas.dtos.customers.LegalPersonAdapterDTO;
import com.catalis.core.orchestrator.interfaces.dtos.accounts.AccountRequest;
import com.catalis.core.orchestrator.interfaces.dtos.accounts.LegalPersonRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for converting between account-related DTOs and API request/response objects.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        implementationName = "OrchestratorLegalPersonMapperImpl")
public abstract class LegalPersonMapper {

    public abstract LegalPersonAdapterDTO requestToDTO(LegalPersonRequest request);

}
