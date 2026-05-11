package org.example.telemetryservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class TelemetryExecutorConfiguration {

    @Bean(destroyMethod = "shutdown")
    public ScheduledExecutorService telemetryExecutorService() {
        return Executors.newSingleThreadScheduledExecutor();
    }
}
