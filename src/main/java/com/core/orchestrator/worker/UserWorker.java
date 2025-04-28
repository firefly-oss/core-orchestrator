package com.core.orchestrator.worker;

import com.catalis.baas.adapter.impl.CustomerAdapterImpl;
import com.catalis.core.customers.interfaces.dtos.FrontLegalPersonDTO;
import com.catalis.core.customers.interfaces.dtos.FrontNaturalPersonDTO;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class UserWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserWorker.class);


    private final CustomerAdapterImpl customerAdapterImpl;

    @Autowired
    public UserWorker(CustomerAdapterImpl customerAdapterImpl) {
        this.customerAdapterImpl = customerAdapterImpl;
    }

    @JobWorker(type = "baas-create-legal-person")
    public Map<String, Object> bassCreateLegalPerson(final ActivatedJob job) {
        LOGGER.info("Executing baas-create-legal-person task for job: {}", job.getKey());

        // Obtener variables del proceso
        FrontLegalPersonDTO userData = job.getVariablesAsType(FrontLegalPersonDTO.class);

        LOGGER.info("Creating legal person: {}", userData.getLegalName());

        // Llamar al microservicio externo
        String externalId = Objects.requireNonNull(customerAdapterImpl.createLegalPerson(userData).block()).getBody();

        LOGGER.info("External ID retrieved successfully: {}", externalId);

        // Preparar resultado para el proceso
        Map<String, Object> result = new HashMap<>();
        result.put("externalReferenceId", externalId);

        return result;
    }

    @JobWorker(type = "baas-create-natural-person")
    public Map<String, Object> bassCreateNaturalPerson(final ActivatedJob job) {
        LOGGER.info("Executing baas-create-natural-person task for job: {}", job.getKey());

        // Obtener variables del proceso
        FrontNaturalPersonDTO userData = job.getVariablesAsType(FrontNaturalPersonDTO.class);

        LOGGER.info("Creating natural person: {}", userData.getFirstname());

        // Llamar al microservicio externo
        // Note: Using createLegalPerson for now, in a real implementation this would call a method specific to natural persons
        String externalId = Objects.requireNonNull(customerAdapterImpl.createNaturalPerson(userData).block()).getBody();

        LOGGER.info("External ID retrieved successfully: {}", externalId);

        // Preparar resultado para el proceso
        Map<String, Object> result = new HashMap<>();
        result.put("externalReferenceId", externalId);

        return result;
    }
}
