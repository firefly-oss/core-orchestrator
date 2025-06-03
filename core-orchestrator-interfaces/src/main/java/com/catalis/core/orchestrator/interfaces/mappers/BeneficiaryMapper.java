package com.catalis.core.orchestrator.interfaces.mappers;

import com.catalis.baas.dtos.beneficiaries.BeneficiaryAdapterDTO;
import com.catalis.core.orchestrator.interfaces.dtos.beneficiaries.BeneficiaryRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for converting between beneficiary-related DTOs and API request/response objects.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        implementationName = "OrchestratorBeneficiaryMapperImpl")
public abstract class BeneficiaryMapper {

    /**
     * Maps a BeneficiaryRequest to a BeneficiaryAdapterDTO.
     *
     * @param request The BeneficiaryRequest to map
     * @return The mapped BeneficiaryAdapterDTO
     */
    public abstract BeneficiaryAdapterDTO requestToDTO(BeneficiaryRequest request);

}