package com.firefly.core.orchestrator.interfaces.mappers;

import com.firefly.baas.dtos.accounts.AccountAdapterDTO;
import com.firefly.core.orchestrator.interfaces.dtos.accounts.AccountRequest;
import com.firefly.core.orchestrator.interfaces.dtos.accounts.AccountResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for converting between account-related DTOs and API request/response objects.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        implementationName = "OrchestratorAccountMapperImpl")
public abstract class AccountMapper {

    public abstract AccountAdapterDTO requestToDTO(AccountRequest request);

    public abstract AccountResponse dtoToResponse(AccountAdapterDTO request);


}
