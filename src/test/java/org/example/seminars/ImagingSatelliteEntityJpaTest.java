package org.example.seminars;

import org.example.ImagingSatellite;
import org.example.ImagingSatelliteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DisplayName("JPA test for ImagingSatellite")
class ImagingSatelliteEntityJpaTest {

    @Autowired
    private ImagingSatelliteRepository repository;

    @Test
    @DisplayName("should persist imaging satellite")
    void shouldPersistImagingSatellite() {
        ImagingSatellite satellite = new ImagingSatellite("Img-Db-1", 0.95, 1.4);

        ImagingSatellite saved = repository.save(satellite);
        ImagingSatellite loaded = repository.findById(saved.getId()).orElseThrow();

        assertEquals("Img-Db-1", loaded.getName());
        assertEquals(1.4, loaded.getResolution(), 1e-9);
        assertEquals(0, loaded.getPhotosTaken());
    }
}
