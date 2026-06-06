package org.example;

import lombok.RequiredArgsConstructor;
import org.example.outbox.OutboxMessageFactory;
import org.example.outbox.OutboxMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SatelliteCommandService {

    private final SatelliteRepository satelliteRepository;
    private final OutboxMessageRepository outboxMessageRepository;
    private final OutboxMessageFactory outboxMessageFactory;

    @Transactional
    public Satellite create(Satellite satellite) {
        Satellite savedSatellite = satelliteRepository.save(satellite);
        outboxMessageRepository.save(outboxMessageFactory.created(savedSatellite));
        return savedSatellite;
    }

    @Transactional
    public boolean delete(Long id) {
        Optional<Satellite> satellite = satelliteRepository.findById(id);
        if (satellite.isEmpty()) {
            return false;
        }

        Satellite existingSatellite = satellite.get();
        satelliteRepository.delete(existingSatellite);
        outboxMessageRepository.save(outboxMessageFactory.deleted(existingSatellite));
        return true;
    }
}
