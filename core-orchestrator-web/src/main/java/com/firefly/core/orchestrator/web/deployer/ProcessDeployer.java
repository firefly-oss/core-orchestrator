package com.firefly.core.orchestrator.web.deployer;

import com.firefly.common.config.sdk.model.ProviderProcessDTO;
import com.firefly.common.config.sdk.model.ProviderProcessVersionDTO;
import com.firefly.core.orchestrator.interfaces.services.ConfigMgmtService;
import io.camunda.zeebe.client.ZeebeClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Component responsible for deploying BPMN process definitions to the Camunda Zeebe engine
 * during application startup.
 */
@Component
@Slf4j
public class ProcessDeployer implements ApplicationRunner {

    private final ZeebeClient zeebeClient;
    private final ConfigMgmtService configMgmtService;

    @Value("${config.api.base-url:http://localhost:8087}")
    private String configApiBaseUrl;

    @Value("${api-configuration.providers.treezorId:1}")
    private Long treezorProviderId;

    @Value("${api-configuration.providers.commonId:3}")
    private Long commonProviderId;

    /**
     * Constructs a new ProcessDeployer with the specified Zeebe client and configuration management service.
     *
     * @param zeebeClient The client used to deploy processes to the Camunda Zeebe engine
     * @param configMgmtService The service used to retrieve process configurations
     */
    @Autowired
    public ProcessDeployer(ZeebeClient zeebeClient, ConfigMgmtService configMgmtService) {
        this.zeebeClient = zeebeClient;
        this.configMgmtService = configMgmtService;
    }

    /**
     * Executes on application startup to deploy all BPMN process definitions.
     * This method is called automatically by Spring Boot.
     *
     * @param args Application arguments
     */
    @Override
    public void run(ApplicationArguments args) {
        log.info("Deploying BPMN processes...");

        Mono.zip(
            getProcessesForProvider(treezorProviderId),
            getProcessesForProvider(commonProviderId)
        )
        .flatMap(tuple -> {
            List<ProviderProcessDTO> allProcesses = new ArrayList<>();
            allProcesses.addAll(tuple.getT1());
            allProcesses.addAll(tuple.getT2());

            log.info("Found {} processes from API", allProcesses.size());

            if (allProcesses.isEmpty()) {
                log.warn("No processes found from API");
                return Mono.empty();
            }

            // Process all processes reactively
            return Flux.fromIterable(allProcesses)
                .flatMap(this::deployProcessWithVersion)
                .then();
        })
        .doOnError(e -> log.error("Error deploying BPMN processes: {}", e.getMessage(), e))
        .subscribe();
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
     * Retrieves processes for a specific provider.
     *
     * @param providerId The ID of the provider to retrieve processes for
     * @return A Mono containing a list of provider processes
     */
    private Mono<List<ProviderProcessDTO>> getProcessesForProvider(Long providerId) {
        return configMgmtService.getProviderProcesses(providerId)
            .flatMap(response -> {
                if (response == null || response.getBody() == null) {
                    log.error("Failed to get response from processes API for provider: {}", providerId);
                    return Mono.just(new ArrayList<ProviderProcessDTO>());
                }
                return Mono.just(Objects.requireNonNull(response.getBody().getContent()));
            })
            .onErrorResume(e -> {
                log.error("Error fetching processes for provider {}: {}", providerId, e.getMessage());
                return Mono.just(new ArrayList<>());
            });
    }

    /**
     * Deploys a process with its version.
     *
     * @param process The provider process to deploy
     * @return A Mono that completes when the process is deployed
     */
    private Mono<Void> deployProcessWithVersion(ProviderProcessDTO process) {
        log.info("Fetching process version for process ID: {}", process.getId());

        return configMgmtService.getProviderProcessVersion(process.getId())
            .flatMap(response -> {
                if (response == null || response.getBody() == null) {
                    log.error("Failed to get response from process-versions API for process ID: {}", process.getId());
                    return Mono.empty();
                }

                ProviderProcessVersionDTO processVersion = response.getBody();
                return deployProcessFromXml(processVersion.getBpmnXml(), process.getCode() + ".bpmn");
            })
            .onErrorResume(e -> {
                log.error("Error deploying process {}: {}", process.getId(), e.getMessage());
                return Mono.empty();
            });
    }

    /**
     * Reactive version of the deployProcessFromXml method.
     *
     * @param bpmnXml The BPMN XML content to deploy
     * @param resourceName The name to use when deploying the process
     * @return A Mono that completes when the process is deployed
     */
    private Mono<Void> deployProcessFromXml(String bpmnXml, String resourceName) {
        return Mono.fromCallable(() -> {
            var deployment = zeebeClient.newDeployResourceCommand()
                    .addResourceStringUtf8(bpmnXml, resourceName)
                    .send()
                    .join();

            log.info("{} BPMN process deployed successfully from XML. Key: {}", 
                resourceName, deployment.getProcesses().getFirst().getProcessDefinitionKey());

            return deployment;
        })
        .subscribeOn(Schedulers.boundedElastic())
        .then();
    }
}
