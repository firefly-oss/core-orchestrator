package com.catalis.core.orchestrator.interfaces.dtos.notifications;

import lombok.Builder;

@Builder
public record NotificationRequest(
        String to,
        Long idOperation
) {}