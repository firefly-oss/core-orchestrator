package com.catalis.core.orchestrator.interfaces.mappers;

import com.catalis.common.platform.notification.services.sdk.model.EmailRequestDTO;
import com.catalis.core.orchestrator.interfaces.dtos.notifications.NotificationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for converting between email-related DTOs and API request/response objects.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        implementationName = "OrchestratorEmailMapperImpl")
public abstract class EmailMapper {

    public abstract EmailRequestDTO requestToDTO(NotificationRequest request);


}