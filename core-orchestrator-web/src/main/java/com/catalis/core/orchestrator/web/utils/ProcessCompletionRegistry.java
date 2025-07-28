package com.catalis.core.orchestrator.web.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for tracking process completion futures.
 * This allows direct communication between workers and controllers,
 * eliminating the need for polling to detect process completion.
 */
@Component
@Slf4j
public class ProcessCompletionRegistry {

    private final Map<Long, CompletableFuture<?>> processCompletionFutures = new ConcurrentHashMap<>();

    /**
     * Registers a CompletableFuture for a process instance.
     *
     * @param processInstanceKey The key of the process instance
     * @param <T> The type of the result that will be returned when the process completes
     * @return A CompletableFuture that will be completed when the process completes
     */
    public <T> CompletableFuture<T> registerProcess(long processInstanceKey) {
        log.info("Registering process instance: {}", processInstanceKey);
        CompletableFuture<T> future = new CompletableFuture<>();
        processCompletionFutures.put(processInstanceKey, future);
        return future;
    }

    /**
     * Completes the future for a process instance.
     *
     * @param processInstanceKey The key of the process instance
     * @param result The result of the process execution
     * @param <T> The type of the result
     */
    public <T> void completeProcess(long processInstanceKey, T result) {
        log.info("Completing process instance: {} with result: {}", processInstanceKey, result);
        CompletableFuture<T> future = (CompletableFuture<T>) processCompletionFutures.remove(processInstanceKey);
        if (future != null) {
            future.complete(result);
        } else {
            log.warn("No future found for process instance: {}", processInstanceKey);
        }
    }

    /**
     * Removes a process instance from the registry without completing its future.
     * This is useful for cleanup in case of errors or timeouts.
     *
     * @param processInstanceKey The key of the process instance
     */
    public void removeProcess(long processInstanceKey) {
        log.info("Removing process instance: {}", processInstanceKey);
        processCompletionFutures.remove(processInstanceKey);
    }
}
