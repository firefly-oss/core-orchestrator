package com.firefly.core.orchestrator.interfaces.dtos.notifications;

import lombok.Builder;

/**
 * Response object for process operations containing the process instance key and status.
 */
@Builder
public record SendNotificationResponse(
        Long idOperation
) {
}