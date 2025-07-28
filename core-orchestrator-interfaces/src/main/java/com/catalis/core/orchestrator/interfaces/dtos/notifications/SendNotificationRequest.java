package com.catalis.core.orchestrator.interfaces.dtos.notifications;

import lombok.Data;

@Data
public class SendNotificationRequest {
    private String to;
    private Long idOperation;
}