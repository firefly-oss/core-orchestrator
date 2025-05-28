package com.catalis.core.orchestrator.web.deployer;

import io.camunda.zeebe.client.ZeebeClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

/**
 * Component responsible for deploying BPMN process definitions to the Camunda Zeebe engine
 * during application startup.
 */
@Component
@Slf4j
public class ProcessDeployer implements ApplicationRunner {

    private final ZeebeClient zeebeClient;
    private final WebClient.Builder webClientBuilder;

    @Value("${config.api.base-url:http://localhost:8087}")
    private String configApiBaseUrl;

    @Value("${config.api.provider-id:1}")
    private Long providerId;

    /**
     * Constructs a new ProcessDeployer with the specified Zeebe client.
     *
     * @param zeebeClient The client used to deploy processes to the Camunda Zeebe engine
     */
    @Autowired
    public ProcessDeployer(ZeebeClient zeebeClient, WebClient.Builder webClientBuilder) {
        this.zeebeClient = zeebeClient;
        this.webClientBuilder = webClientBuilder;
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
            // Deploy local BPMN processes
            log.info("Deploying local BPMN processes...");

            // Use ResourcePatternResolver to find all BPMN files in the bpmn directory
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            try {
                Resource[] resources = resolver.getResources("classpath:bpmn/*.bpmn");
                log.info("Found {} BPMN processes to deploy", resources.length);

                for (Resource resource : resources) {
                    String filename = resource.getFilename();
                    try {
                        deployProcess("bpmn/" + filename, filename);
                        log.info("{} deployed successfully", filename);
                    } catch (Exception e) {
                        log.error("Error deploying {}: {}", filename, e.getMessage(), e);
                    }
                }
            } catch (Exception e) {
                log.error("Error scanning for BPMN processes: {}", e.getMessage(), e);
            }
//
//            log.info("Fetching processes from API...");
//
//            // Create request body for the filter endpoint
//            String requestBody = String.format(
//                    "{\n" +
//                    "  \"filters\": {\n" +
//                    "    \"providerId\": %d\n" +
//                    "  },\n" +
//                    "  \"active\": true,\n" +
//                    "  \"pagination\": {\n" +
//                    "    \"pageNumber\": 0,\n" +
//                    "    \"pageSize\": 10,\n" +
//                    "    \"sortBy\": \"name\",\n" +
//                    "    \"sortDirection\": \"DESC\"\n" +
//                    "  }\n" +
//                    "}", providerId);
//
//            // Call the filter endpoint to get the list of processes
//            Mono<ResponseEntity<PaginationResponse<ProviderProcessDTO>>> responseEntityMono = webClientBuilder
//                    .baseUrl(configApiBaseUrl)
//                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                    .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
//                    .defaultHeader("X-Idempotency-Key", "12345")
//                    .build()
//                    .post()
//                    .uri("/api/v1/providers/" + providerId + "/processes/filter")
//                    .bodyValue(requestBody)
//                    .retrieve()
//                    .toEntity(new ParameterizedTypeReference<PaginationResponse<ProviderProcessDTO>>() {});
//
//            ResponseEntity<PaginationResponse<ProviderProcessDTO>> responseEntity = responseEntityMono.block();
//            if (responseEntity == null) {
//                log.error("Failed to get response from processes API");
//                return;
//            }
//
//            PaginationResponse<ProviderProcessDTO> paginationResponse = responseEntity.getBody();
//            if (paginationResponse == null) {
//                log.error("Response body from processes API is null");
//                return;
//            }
//
//            List<ProviderProcessDTO> processes = paginationResponse.getContent();
//
//            if (processes == null || processes.isEmpty()) {
//                log.warn("No processes found from API");
//                return;
//            }
//
//            log.info("Found {} processes from API", processes.size());
//
//            // For each process, get the process version and deploy it
//            for (ProviderProcessDTO process : processes) {
//                try {
//                    log.info("Fetching process version for process ID: {}", process.getId());
//
//                    // Call the process-versions endpoint to get the process version
//                    Mono<ResponseEntity<ProviderProcessVersionDTO>> processVersionResponseEntityMono = webClientBuilder
//                            .baseUrl(configApiBaseUrl)
//                            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
//                            .defaultHeader("X-Idempotency-Key", "1234")
//                            .build()
//                            .get()
//                            .uri("/api/v1/process-versions/" + process.getId())
//                            .retrieve()
//                            .toEntity(ProviderProcessVersionDTO.class);
//
//                    ResponseEntity<ProviderProcessVersionDTO> processVersionResponseEntity = processVersionResponseEntityMono.block();
//                    if (processVersionResponseEntity == null) {
//                        log.error("Failed to get response from process-versions API for process ID: {}", process.getId());
//                        continue;
//                    }
//
//                    ProviderProcessVersionDTO processVersion = processVersionResponseEntity.getBody();
//                    if (processVersion == null) {
//                        log.error("Response body from process-versions API is null for process ID: {}", process.getId());
//                        continue;
//                    }
//
//                    if (processVersion.getBpmnXml() == null || processVersion.getBpmnXml().isEmpty()) {
//                        log.warn("No BPMN XML found for process ID: {}", process.getId());
//                        continue;
//                    }
//
//                    // Deploy the process using the BPMN XML from the API response
//                    deployProcessFromXml(processVersion.getBpmnXml(), process.getCode() + ".bpmn");
//
//                } catch (Exception e) {
//                    log.error("Error deploying process ID {}: {}", process.getId(), e.getMessage());
//                }
//            }

        } catch (Exception e) {
            log.error("Error deploying BPMN processes: {}", e.getMessage(), e);
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

    /**
     * Deploys a single BPMN process definition to the Zeebe engine using XML content.
     *
     * @param bpmnXml The XML content of the BPMN process
     * @param resourceName The name to use when deploying the process
     */
    private void deployProcessFromXml(String bpmnXml, String resourceName) {
        var deployment = zeebeClient.newDeployResourceCommand()
                .addResourceStringUtf8(bpmnXml, resourceName)
                .send()
                .join();
        log.info("{} BPMN process deployed successfully from XML. Key: {}",
                resourceName, deployment.getProcesses().getFirst().getProcessDefinitionKey());
    }
}
