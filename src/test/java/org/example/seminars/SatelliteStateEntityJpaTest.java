package org.example.seminars;

import org.example.SatelliteState;
import org.example.SatelliteStateRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DisplayName("JPA test for SatelliteState")
class SatelliteStateEntityJpaTest {

    @Autowired
    private SatelliteStateRepository repository;

    @Test
    @DisplayName("should persist and load satellite state")
    void shouldPersistAndLoadSatelliteState() {
        SatelliteState state = new SatelliteState();
        state.activate();

        SatelliteState saved = repository.save(state);
        SatelliteState loaded = repository.findById(saved.getId()).orElseThrow();

        assertTrue(loaded.isActive());
        assertEquals("Активен", loaded.getStatusMessage());
    }
}
