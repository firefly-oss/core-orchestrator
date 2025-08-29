package com.firefly.core.orchestrator.interfaces.dtos.accounts;

import lombok.Builder;

@Builder
public record TaxResidenceRequest(
        // Required
        int userId,
        
        // Required
        String country,
        
        // Required
        String taxIdentificationNumber,
        
        String reasonNoTIN,
        
        String accessTag
) {}