package com.catalis.core.orchestrator.interfaces.dtos.notifications;

/**
 * Response object for SCA validation operations containing the validation status and operation ID.
 */
public record ValidateSCAResponse(
        Boolean validationStatus,
        Long operationId
) {
}