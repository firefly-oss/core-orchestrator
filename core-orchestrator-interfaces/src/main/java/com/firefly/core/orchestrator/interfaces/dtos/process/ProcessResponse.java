package com.firefly.core.orchestrator.interfaces.dtos.process;

/**
 * Response object for process operations containing the process instance key and status.
 */
public record ProcessResponse(
        Long processInstanceKey,
        String status
) {
}