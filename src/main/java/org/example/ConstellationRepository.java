package org.example;

import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class ConstellationRepository {

    private final Map<String, SatelliteConstellation> constellations;

    public ConstellationRepository() {
        this.constellations = new LinkedHashMap<>();
    }

    public SatelliteConstellation save(SatelliteConstellation constellation) {
        constellations.put(constellation.getConstellationName(), constellation);
        System.out.println("Сохранена группировка: " + constellation.getConstellationName());
        return constellation;
    }

    public Optional<SatelliteConstellation> findByName(String name) {
        return Optional.ofNullable(constellations.get(name));
    }

    public Map<String, SatelliteConstellation> findAll() {
        return constellations;
    }

    public boolean existsByName(String name) {
        return constellations.containsKey(name);
    }

    public void deleteByName(String name) {
        constellations.remove(name);
    }
}
