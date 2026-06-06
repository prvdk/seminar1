package org.example.telemetryservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SatelliteEventProcessor {

    private static final Logger log = LoggerFactory.getLogger(SatelliteEventProcessor.class);

    private final SatelliteRegistry satelliteRegistry;
    private final InboxMessageRepository inboxMessageRepository;

    public SatelliteEventProcessor(
            SatelliteRegistry satelliteRegistry,
            InboxMessageRepository inboxMessageRepository
    ) {
        this.satelliteRegistry = satelliteRegistry;
        this.inboxMessageRepository = inboxMessageRepository;
    }

    @Transactional
    public void process(SatelliteEvent event) {
        validate(event);
        if (inboxMessageRepository.existsById(event.eventId())) {
            log.info("Ignoring duplicate satellite event {}", event.eventId());
            return;
        }

        try {
            inboxMessageRepository.saveAndFlush(new InboxMessage(
                    event.eventId(),
                    event.satelliteId(),
                    event.eventType()
            ));
        } catch (DataIntegrityViolationException exception) {
            log.info("Ignoring concurrently processed satellite event {}", event.eventId());
            return;
        }

        switch (event.eventType()) {
            case SATELLITE_CREATED -> satelliteRegistry.add(event.satelliteId(), event.satelliteName());
            case SATELLITE_DELETED -> satelliteRegistry.remove(event.satelliteId());
        }
    }

    private void validate(SatelliteEvent event) {
        if (event.eventId() == null || event.satelliteId() == null || event.eventType() == null) {
            throw new IllegalArgumentException("Satellite event must contain eventId, satelliteId and eventType");
        }
        if (event.eventType() == SatelliteEventType.SATELLITE_CREATED && event.satelliteName() == null) {
            throw new IllegalArgumentException("Satellite created event must contain satelliteName");
        }
    }
}
