package com.catalis.core.orchestrator.interfaces.dtos.beneficiaries;

import lombok.Builder;

/**
 * Request DTO for creating a beneficiary.
 */
@Builder
public record BeneficiaryRequest(
        // Required
        String userId,
        
        // Required
        String name,
        
        // Required
        String nickName,
        
        // Required
        String address,
        
        // Required
        String iban,
        
        // Required
        String bic,
        
        // Required
        Boolean usableForSct
) {}