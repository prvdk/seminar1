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
    private final SatelliteEventProcessor satelliteEventProcessor;

    public SatelliteEventsListener(
            ObjectMapper objectMapper,
            SatelliteEventProcessor satelliteEventProcessor
    ) {
        this.objectMapper = objectMapper;
        this.satelliteEventProcessor = satelliteEventProcessor;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.satellite-events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handle(String message) {
        try {
            SatelliteEvent event = objectMapper.readValue(message, SatelliteEvent.class);
            satelliteEventProcessor.process(event);
        } catch (JsonProcessingException | RuntimeException exception) {
            log.warn("Skipping broken satellite event message: {}", message, exception);
        }
    }
}
