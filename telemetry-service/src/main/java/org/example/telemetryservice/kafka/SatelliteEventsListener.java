package org.example.telemetryservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class SatelliteEventsListener {

    private static final Logger log = LoggerFactory.getLogger(SatelliteEventsListener.class);

    private final ObjectMapper objectMapper;
    private final SatelliteRegistry satelliteRegistry;

    public SatelliteEventsListener(ObjectMapper objectMapper, SatelliteRegistry satelliteRegistry) {
        this.objectMapper = objectMapper;
        this.satelliteRegistry = satelliteRegistry;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.satellite-events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handle(String message) {
        try {
            SatelliteEvent event = objectMapper.readValue(message, SatelliteEvent.class);
            validate(event);
            switch (event.eventType()) {
                case SATELLITE_CREATED -> satelliteRegistry.add(event.satelliteId(), event.satelliteName());
                case SATELLITE_DELETED -> satelliteRegistry.remove(event.satelliteId());
            }
        } catch (JsonProcessingException | RuntimeException exception) {
            log.warn("Skipping broken satellite event message: {}", message, exception);
        }
    }

    private void validate(SatelliteEvent event) {
        if (event.satelliteId() == null || event.satelliteName() == null || event.eventType() == null) {
            throw new IllegalArgumentException("Satellite event must contain satelliteId, satelliteName and eventType");
        }
    }
}
