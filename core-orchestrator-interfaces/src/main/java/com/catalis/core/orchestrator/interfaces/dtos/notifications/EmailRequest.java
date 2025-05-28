package com.catalis.core.orchestrator.interfaces.dtos.notifications;

import lombok.Builder;

@Builder
public record EmailRequest(
        // Required
        String to,
        
        // Required
        String from,
        
        // Required
        String subject,
        
        // Required
        String html
) {}