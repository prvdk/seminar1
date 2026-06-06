package org.example.kafka;

import java.time.Instant;
import java.util.UUID;

public record SatelliteEvent(
        UUID eventId,
        Long satelliteId,
        String satelliteName,
        SatelliteEventType eventType,
        Instant occurredAt
) {
}
