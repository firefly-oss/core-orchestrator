package com.core.orchestrator.config;

import io.camunda.zeebe.client.ZeebeClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Component responsible for deploying BPMN process definitions to the Camunda Zeebe engine
 * during application startup.
 */
@Component
@Slf4j
public class ProcessDeployer implements ApplicationRunner {

    public static final String BPMN_CREATE_LEGAL_PERSON_PROCESS_BPMN = "bpmn/create-legal-person-process.bpmn";
    public static final String BPMN_CREATE_NATURAL_PERSON_PROCESS_BPMN = "bpmn/create-natural-person-process.bpmn";
    public static final String CREATE_LEGAL_PERSON_PROCESS_BPMN = "create-legal-person-process.bpmn";
    public static final String CREATE_NATURAL_PERSON_PROCESS_BPMN = "create-natural-person-process.bpmn";
    public static final String BPMN_CREATE_DOCUMENT_PROCESS_BPMN = "bpmn/create-document-process.bpmn";
    public static final String CREATE_DOCUMENT_PROCESS_BPMN = "create-document-process.bpmn";
    public static final String BPMN_USER_KYC_REVIEW_PROCESS_BPMN = "bpmn/user-kyc-review-process.bpmn";
    public static final String USER_KYC_REVIEW_PROCESS_BPMN = "user-kyc-review-process.bpmn";
    public static final String BPMN_USER_KYB_REVIEW_PROCESS_BPMN = "bpmn/user-kyb-review-process.bpmn";
    public static final String USER_KYB_REVIEW_PROCESS_BPMN = "user-kyb-review-process.bpmn";
    public static final String BPMN_CREATE_ACCOUNT_PROCESS_BPMN = "bpmn/create-account-process.bpmn";
    public static final String CREATE_ACCOUNT_PROCESS_BPMN = "create-account-process.bpmn";

    private final ZeebeClient zeebeClient;

    /**
     * Constructs a new ProcessDeployer with the specified Zeebe client.
     *
     * @param zeebeClient The client used to deploy processes to the Camunda Zeebe engine
     */
    @Autowired
    public ProcessDeployer(ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    /**
     * Executes on application startup to deploy all BPMN process definitions.
     * This method is called automatically by Spring Boot.
     *
     * @param args Application arguments
     */
    @Override
    public void run(ApplicationArguments args) {
        try {
            // Deploy legal person process
            deployProcess(BPMN_CREATE_LEGAL_PERSON_PROCESS_BPMN, CREATE_LEGAL_PERSON_PROCESS_BPMN);

            // Deploy natural person process
            deployProcess(BPMN_CREATE_NATURAL_PERSON_PROCESS_BPMN, CREATE_NATURAL_PERSON_PROCESS_BPMN);

            // Deploy create document process
            deployProcess(BPMN_CREATE_DOCUMENT_PROCESS_BPMN, CREATE_DOCUMENT_PROCESS_BPMN);

            // Deploy user KYC review process
            deployProcess(BPMN_USER_KYC_REVIEW_PROCESS_BPMN, USER_KYC_REVIEW_PROCESS_BPMN);

            // Deploy user KYB review process
            deployProcess(BPMN_USER_KYB_REVIEW_PROCESS_BPMN, USER_KYB_REVIEW_PROCESS_BPMN);

            // Deploy create account process
            deployProcess(BPMN_CREATE_ACCOUNT_PROCESS_BPMN, CREATE_ACCOUNT_PROCESS_BPMN);

        } catch (IOException e) {
            log.error("Error reading BPMN resource: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error deploying BPMN processes: {}", e.getMessage());
        }

    }

    /**
     * Deploys a single BPMN process definition to the Zeebe engine.
     *
     * @param resourcePath The classpath location of the BPMN file
     * @param resourceName The name to use when deploying the process
     * @throws IOException If there's an error reading the BPMN file
     */
    private void deployProcess(String resourcePath, String resourceName) throws IOException {
        ClassPathResource resource = new ClassPathResource(resourcePath);
        var deployment = zeebeClient.newDeployResourceCommand()
                .addResourceBytes(resource.getInputStream().readAllBytes(), resourceName)
                .send()
                .join();
        log.info("{} BPMN process deployed successfully. Key: {}",
                resourceName, deployment.getProcesses().getFirst().getProcessDefinitionKey());
    }
}
