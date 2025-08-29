package com.firefly.core.orchestrator.interfaces.mappers;

import com.firefly.baas.dtos.documents.DocumentAdapterDTO;
import com.firefly.core.orchestrator.interfaces.dtos.documents.DocumentRequest;
import com.firefly.core.orchestrator.interfaces.dtos.documents.DocumentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for converting between document-related DTOs and API request/response objects.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        implementationName = "OrchestratorDocumentMapperImpl")
public abstract class DocumentMapper {

    public abstract DocumentAdapterDTO requestToDTO(DocumentRequest request);
    public abstract DocumentResponse dtoToResponse(DocumentAdapterDTO documentDTO);

}