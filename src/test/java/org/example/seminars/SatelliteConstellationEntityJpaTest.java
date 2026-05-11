package org.example.seminars;

import org.example.CommunicationSatellite;
import org.example.ConstellationRepository;
import org.example.SatelliteConstellation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DisplayName("JPA test for SatelliteConstellation")
class SatelliteConstellationEntityJpaTest {

    @Autowired
    private ConstellationRepository repository;

    @Test
    @DisplayName("should persist constellation with satellites")
    void shouldPersistConstellationWithSatellites() {
        SatelliteConstellation constellation = new SatelliteConstellation("Geo-1");
        constellation.addSatellite(new CommunicationSatellite("Comm-1", 0.9, 600.0));

        SatelliteConstellation saved = repository.save(constellation);
        SatelliteConstellation loaded = repository.findById(saved.getId()).orElseThrow();

        assertEquals("Geo-1", loaded.getConstellationName());
        assertEquals(1, loaded.getSatellites().size());
        assertTrue(repository.existsByConstellationName("Geo-1"));
    }
}
