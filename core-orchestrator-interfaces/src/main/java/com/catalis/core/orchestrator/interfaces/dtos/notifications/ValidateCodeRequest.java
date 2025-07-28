package com.catalis.core.orchestrator.interfaces.dtos.notifications;

import lombok.Builder;

/**
 * Request object for validating a verification code.
 */
@Builder
public record ValidateCodeRequest(
        // Required - ID of the operation to validate
        Long idOperation,
        
        // Required - verification code to validate
        String code
) {}