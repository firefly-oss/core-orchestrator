package com.catalis.core.orchestrator.interfaces.mappers;

import com.catalis.baas.dtos.documents.DocumentAdapterDTO;
import com.catalis.core.orchestrator.interfaces.dtos.documents.DocumentRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for converting between document-related DTOs and API request/response objects.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        implementationName = "OrchestratorDocumentMapperImpl")
public abstract class DocumentMapper {

    public abstract DocumentAdapterDTO requestToDTO(DocumentRequest request);

}