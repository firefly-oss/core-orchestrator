package com.firefly.core.orchestrator.interfaces.dtos.notifications;

import lombok.Data;

@Data
public class CreateChallengeRequest {
    private Long idOperation;
    private String verificationCode;
}