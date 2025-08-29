package com.firefly.core.orchestrator.interfaces.dtos.beneficiaries;

import lombok.Builder;

/**
 * Response DTO for beneficiary operations.
 */
@Builder
public record BeneficiaryResponse(
        // The external reference ID of the created beneficiary
        String externalReferenceId
) {}