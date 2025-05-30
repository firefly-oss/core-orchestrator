package com.catalis.core.orchestrator.interfaces.dtos.notifications;

import lombok.Builder;

/**
 * Response object for SCA validation operations containing the validation status and operation ID.
 */
@Builder
public record ValidateSCAResponse(
        Boolean validationStatus,
        Long operationId
) {
}