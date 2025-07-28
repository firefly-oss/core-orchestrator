package com.catalis.core.orchestrator.interfaces.dtos.accounts;

import lombok.Builder;

@Builder
public record AccountRequest (
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