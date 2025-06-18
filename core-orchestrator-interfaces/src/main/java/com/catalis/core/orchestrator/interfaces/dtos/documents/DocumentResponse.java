package com.catalis.core.orchestrator.interfaces.dtos.documents;

import lombok.Builder;

@Builder
public record DocumentResponse(
        Integer userId,

        byte[] fileContentBase64,

        Integer documentTypeId,

        String name
) {}