package com.catalis.core.orchestrator.interfaces.dtos.customers;

import lombok.Builder;

/**
 * Request DTO for creating a beneficiary.
 */
@Builder
public record CustomerResponse(
        // Required
        int userTypeId

) {}