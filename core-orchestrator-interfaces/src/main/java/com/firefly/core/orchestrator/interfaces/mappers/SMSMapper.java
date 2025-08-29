package com.firefly.core.orchestrator.interfaces.mappers;

import com.firefly.common.platform.notification.services.sdk.model.SMSRequestDTO;
import com.firefly.core.orchestrator.interfaces.dtos.notifications.SMSRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for converting between SMS-related DTOs and API request/response objects.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        implementationName = "OrchestratorSMSMapperImpl")
public abstract class SMSMapper {

    public abstract SMSRequestDTO requestToDTO(SMSRequest request);

}