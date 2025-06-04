package com.catalis.core.orchestrator.interfaces.mappers;

import com.catalis.baas.dtos.accounts.AccountAdapterDTO;
import com.catalis.core.orchestrator.interfaces.dtos.accounts.AccountRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for converting between account-related DTOs and API request/response objects.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        implementationName = "OrchestratorAccountMapperImpl")
public abstract class AccountMapper {

    public abstract AccountAdapterDTO requestToDTO(AccountRequest request);

}
