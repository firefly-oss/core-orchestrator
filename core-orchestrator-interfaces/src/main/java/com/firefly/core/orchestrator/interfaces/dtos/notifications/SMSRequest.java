package com.firefly.core.orchestrator.interfaces.dtos.notifications;

import lombok.Builder;

@Builder
public record SMSRequest(
        // Required - recipient's phone number
        String to,
        
        // Required - sender's phone number or ID
        String from,
        
        // Required - message content
        String message
) {}