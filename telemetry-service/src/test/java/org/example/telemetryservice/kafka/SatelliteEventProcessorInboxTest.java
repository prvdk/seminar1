package org.example.telemetryservice.kafka;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DisplayName("Inbox tests for SatelliteEventProcessor")
class SatelliteEventProcessorInboxTest {

    @Autowired
    private SatelliteEventProcessor satelliteEventProcessor;

    @Autowired
    private InboxMessageRepository inboxMessageRepository;

    @Autowired
    private SatelliteRegistry satelliteRegistry;

    @Test
    @DisplayName("process should ignore duplicate event ids")
    void processShouldIgnoreDuplicateEventIds() {
        UUID eventId = UUID.randomUUID();

        satelliteEventProcessor.process(new SatelliteEvent(
                eventId,
                42L,
                "Telemetry-1",
                SatelliteEventType.SATELLITE_CREATED,
                Instant.now()
        ));
        satelliteEventProcessor.process(new SatelliteEvent(
                eventId,
                42L,
                "Telemetry-Duplicate",
                SatelliteEventType.SATELLITE_CREATED,
                Instant.now()
        ));

        assertEquals(1, inboxMessageRepository.count());
        assertEquals(1, satelliteRegistry.names().size());
        assertEquals("Telemetry-1", satelliteRegistry.names().getFirst());
    }
}
