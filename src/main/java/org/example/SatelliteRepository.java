package org.example;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SatelliteRepository extends JpaRepository<Satellite, Long> {

    List<Satellite> findByConstellationConstellationName(String constellationName);

    Optional<Satellite> findByName(String name);
}
