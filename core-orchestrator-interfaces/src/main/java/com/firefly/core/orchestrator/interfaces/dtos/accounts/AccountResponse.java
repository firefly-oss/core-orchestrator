package com.firefly.core.orchestrator.interfaces.dtos.accounts;

import lombok.Builder;

/**
 * Request DTO for creating a beneficiary.
 */
@Builder
public record AccountResponse(
        // Required
        String walletTypeId,

        // Required
        Integer tariffId,

        // Required
        Integer userId,

        // Required
        String currency,

        // Required
        String eventName,

        String accessTag
) {}