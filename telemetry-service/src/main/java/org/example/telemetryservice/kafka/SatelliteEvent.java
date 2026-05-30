package org.example.telemetryservice.kafka;

import java.time.Instant;

public record SatelliteEvent(
        Long satelliteId,
        String satelliteName,
        SatelliteEventType eventType,
        Instant occurredAt
) {
}
