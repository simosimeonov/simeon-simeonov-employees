package com.sirma.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class ExecutorConfig {

    @Bean(name = "projectExecutor", destroyMethod = "shutdown")
    public ExecutorService projectExecutor(AppProperties props) {
        int configured = props.calculation().parallelism();
        int cores = Runtime.getRuntime().availableProcessors();
        int size = (configured > 0) ? Math.min(configured, cores) : cores;
        AtomicInteger counter = new AtomicInteger();
        return Executors.newFixedThreadPool(size, r -> {
            Thread t = new Thread(r, "project-worker-" + counter.incrementAndGet());
            t.setDaemon(true);
            return t;
        });
    }
}