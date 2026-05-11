package org.example;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConstellationRepository extends JpaRepository<SatelliteConstellation, Long> {

    Optional<SatelliteConstellation> findByConstellationName(String constellationName);

    boolean existsByConstellationName(String constellationName);

    void deleteByConstellationName(String constellationName);
}
