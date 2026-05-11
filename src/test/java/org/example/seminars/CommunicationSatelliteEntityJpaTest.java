package org.example.seminars;

import org.example.CommunicationSatellite;
import org.example.CommunicationSatelliteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DisplayName("JPA test for CommunicationSatellite")
class CommunicationSatelliteEntityJpaTest {

    @Autowired
    private CommunicationSatelliteRepository repository;

    @Test
    @DisplayName("should persist communication satellite")
    void shouldPersistCommunicationSatellite() {
        CommunicationSatellite satellite = new CommunicationSatellite("Comm-Db-1", 0.85, 750.0);

        CommunicationSatellite saved = repository.save(satellite);
        CommunicationSatellite loaded = repository.findById(saved.getId()).orElseThrow();

        assertEquals("Comm-Db-1", loaded.getName());
        assertEquals(750.0, loaded.getBandwidth(), 1e-9);
    }
}
