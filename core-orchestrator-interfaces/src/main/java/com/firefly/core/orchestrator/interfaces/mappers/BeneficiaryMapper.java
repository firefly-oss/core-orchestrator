package com.firefly.core.orchestrator.interfaces.mappers;

import com.firefly.baas.dtos.beneficiaries.BeneficiaryAdapterDTO;
import com.firefly.core.orchestrator.interfaces.dtos.beneficiaries.BeneficiaryRequest;
import com.firefly.core.orchestrator.interfaces.dtos.beneficiaries.BeneficiaryResponse;
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

    /**
     * Creates a BeneficiaryResponse from an external reference ID.
     *
     * @param externalReferenceId The external reference ID
     * @return The BeneficiaryResponse
     */
    public BeneficiaryResponse toResponse(String externalReferenceId) {
        return BeneficiaryResponse.builder()
                .externalReferenceId(externalReferenceId)
                .build();
    }
}
