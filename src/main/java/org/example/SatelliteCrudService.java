package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SatelliteCrudService {

    private final SatelliteRepository satelliteRepository;

    @Cacheable(value = "satellite", key = "#id")
    public Optional<Satellite> getSatelliteById(Long id) {
        return satelliteRepository.findById(id);
    }

    @Cacheable(value = "satellites", key = "'all'")
    public List<Satellite> getAllSatellites() {
        return satelliteRepository.findAll();
    }

    @Cacheable(value = "satellite", key = "#constellationName + '::' + #satelliteName")
    public Optional<Satellite> findByName(String constellationName, String satelliteName) {
        return satelliteRepository.findByConstellationConstellationName(constellationName).stream()
                .filter(satellite -> satellite.getName().equals(satelliteName))
                .findFirst();
    }

    @Transactional
    @CacheEvict(value = "satellite", key = "#id")
    public Satellite updateSatellite(Long id, Satellite satellite) {
        satellite.setId(id);
        return satelliteRepository.save(satellite);
    }
}
