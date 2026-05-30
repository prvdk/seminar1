package org.example.telemetryservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SatelliteRegistry {

    private static final Logger log = LoggerFactory.getLogger(SatelliteRegistry.class);

    private final Map<Long, String> satellites = new ConcurrentHashMap<>();

    public void add(Long id, String name) {
        satellites.put(id, name);
        log.info("Registered satellite {} with id {}", name, id);
    }

    public void remove(Long id) {
        String removedName = satellites.remove(id);
        if (removedName != null) {
            log.info("Removed satellite {} with id {}", removedName, id);
        }
    }

    public List<String> names() {
        return satellites.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(Map.Entry::getValue)
                .toList();
    }
}
