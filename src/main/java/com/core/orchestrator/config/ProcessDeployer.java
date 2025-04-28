package com.core.orchestrator.config;

import io.camunda.zeebe.client.ZeebeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ProcessDeployer implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessDeployer.class);

    private final ZeebeClient zeebeClient;

    @Autowired
    public ProcessDeployer(ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            // Deploy legal person process
            ClassPathResource legalPersonResource = new ClassPathResource("bpmn/create-legal-person-process.bpmn");
            var legalPersonDeployment = zeebeClient.newDeployResourceCommand()
                    .addResourceBytes(legalPersonResource.getInputStream().readAllBytes(), "create-legal-person-process.bpmn")
                    .send()
                    .join();
            LOGGER.info("Legal person BPMN process deployed successfully. Key: {}",
                    legalPersonDeployment.getProcesses().getFirst().getProcessDefinitionKey());

            // Deploy natural person process
            ClassPathResource naturalPersonResource = new ClassPathResource("bpmn/create-natural-person-process.bpmn");
            var naturalPersonDeployment = zeebeClient.newDeployResourceCommand()
                    .addResourceBytes(naturalPersonResource.getInputStream().readAllBytes(), "create-natural-person-process.bpmn")
                    .send()
                    .join();
            LOGGER.info("Natural person BPMN process deployed successfully. Key: {}",
                    naturalPersonDeployment.getProcesses().getFirst().getProcessDefinitionKey());

        } catch (IOException e) {
            LOGGER.error("Error deploying BPMN processes", e);
        }
    }
}
