package org.example.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.Satellite;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class SatelliteEventPublisher {

    private final KafkaTemplate<String, SatelliteEvent> kafkaTemplate;

    @Value("${app.kafka.topics.satellite-events}")
    private String satelliteEventsTopic;

    public void publishCreated(Satellite satellite) {
        publish(satellite, SatelliteEventType.SATELLITE_CREATED);
    }

    public void publishDeleted(Satellite satellite) {
        publish(satellite, SatelliteEventType.SATELLITE_DELETED);
    }

    private void publish(Satellite satellite, SatelliteEventType eventType) {
        SatelliteEvent event = new SatelliteEvent(
                satellite.getId(),
                satellite.getName(),
                eventType,
                Instant.now()
        );
        kafkaTemplate.send(satelliteEventsTopic, satellite.getId().toString(), event)
                .whenComplete((result, exception) -> {
                    if (exception != null) {
                        log.error("Failed to publish satellite event {}", event, exception);
                    } else {
                        log.info("Published satellite event {}", event);
                    }
                });
    }
}
