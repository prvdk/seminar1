package org.example.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class SatelliteEventPublisher {

    private final KafkaTemplate<String, SatelliteEvent> kafkaTemplate;

    @Value("${app.kafka.topics.satellite-events}")
    private String satelliteEventsTopic;

    @Value("${app.outbox.send-timeout-ms:3000}")
    private long sendTimeoutMs;

    public void publish(SatelliteEvent event) {
        try {
            kafkaTemplate.send(satelliteEventsTopic, event.satelliteId().toString(), event)
                    .get(sendTimeoutMs, TimeUnit.MILLISECONDS);
            log.info("Published satellite event {}", event);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while publishing satellite event", exception);
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to publish satellite event", exception);
        }
    }
}
