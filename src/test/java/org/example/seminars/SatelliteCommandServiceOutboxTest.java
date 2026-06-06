package org.example.seminars;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.CommunicationSatellite;
import org.example.SatelliteCommandService;
import org.example.kafka.SatelliteEvent;
import org.example.kafka.SatelliteEventType;
import org.example.outbox.OutboxEventType;
import org.example.outbox.OutboxMessage;
import org.example.outbox.OutboxMessageRepository;
import org.example.outbox.OutboxStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
@DisplayName("Transactional outbox tests for SatelliteCommandService")
class SatelliteCommandServiceOutboxTest {

    @Autowired
    private SatelliteCommandService satelliteCommandService;

    @Autowired
    private OutboxMessageRepository outboxMessageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("create should persist satellite and outbox message in same transaction")
    void createShouldPersistOutboxMessage() throws Exception {
        CommunicationSatellite satellite = new CommunicationSatellite("Outbox-Comm-1", 0.9, 512.0);

        satelliteCommandService.create(satellite);

        List<OutboxMessage> messages = outboxMessageRepository.findAll();
        assertEquals(1, messages.size());

        OutboxMessage outboxMessage = messages.getFirst();
        SatelliteEvent event = objectMapper.readValue(outboxMessage.getPayload(), SatelliteEvent.class);

        assertEquals(satellite.getId(), outboxMessage.getAggregateId());
        assertEquals(OutboxEventType.CREATED, outboxMessage.getEventType());
        assertEquals(OutboxStatus.PENDING, outboxMessage.getStatus());
        assertEquals(satellite.getId(), event.satelliteId());
        assertEquals("Outbox-Comm-1", event.satelliteName());
        assertEquals(SatelliteEventType.SATELLITE_CREATED, event.eventType());
        assertNotNull(event.eventId());
    }
}
