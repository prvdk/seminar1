package org.example.seminars;

import org.example.CommunicationSatellite;
import org.example.Satellite;
import org.example.SatelliteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@DataJpaTest
@DisplayName("JPA test for Satellite hierarchy")
class SatelliteEntityJpaTest {

    @Autowired
    private SatelliteRepository repository;

    @Test
    @DisplayName("should load satellites via base repository")
    void shouldLoadSatellitesViaBaseRepository() {
        repository.save(new CommunicationSatellite("Comm-Base-1", 0.8, 500.0));

        List<Satellite> satellites = repository.findAll();

        assertEquals(1, satellites.size());
        assertInstanceOf(CommunicationSatellite.class, satellites.get(0));
    }
}
