package com.firefly.core.orchestrator.interfaces.dtos.documents;

import lombok.Builder;

@Builder
public record DocumentRequest(
        // Required
        String name,
        
        // Other fields that might be needed
        String type,
        
        String content,
        
        String format,
        
        Integer userId,
        
        String accessTag
) {}