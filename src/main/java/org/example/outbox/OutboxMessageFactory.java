package org.example.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.Satellite;
import org.example.kafka.SatelliteEvent;
import org.example.kafka.SatelliteEventType;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OutboxMessageFactory {

    private final ObjectMapper objectMapper;

    public OutboxMessage created(Satellite satellite) {
        return create(satellite, OutboxEventType.CREATED, SatelliteEventType.SATELLITE_CREATED);
    }

    public OutboxMessage deleted(Satellite satellite) {
        return create(satellite, OutboxEventType.DELETED, SatelliteEventType.SATELLITE_DELETED);
    }

    private OutboxMessage create(Satellite satellite, OutboxEventType outboxType, SatelliteEventType kafkaType) {
        UUID eventId = UUID.randomUUID();
        SatelliteEvent event = new SatelliteEvent(
                eventId,
                satellite.getId(),
                satellite.getName(),
                kafkaType,
                Instant.now()
        );
        try {
            return new OutboxMessage(eventId, satellite.getId(), outboxType, objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize satellite event", exception);
        }
    }
}
