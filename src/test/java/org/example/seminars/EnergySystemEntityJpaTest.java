package org.example.seminars;

import org.example.EnergySystem;
import org.example.EnergySystemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DisplayName("JPA test for EnergySystem")
class EnergySystemEntityJpaTest {

    @Autowired
    private EnergySystemRepository repository;

    @Test
    @DisplayName("should persist and load energy system")
    void shouldPersistAndLoadEnergySystem() {
        EnergySystem energy = EnergySystem.builder()
                .batteryLevel(0.75)
                .lowBatteryThreshold(0.2)
                .maxBattery(1.0)
                .minBattery(0.0)
                .build();

        EnergySystem saved = repository.save(energy);
        EnergySystem loaded = repository.findById(saved.getId()).orElseThrow();

        assertEquals(0.75, loaded.getBatteryLevel(), 1e-9);
        assertEquals(0.2, loaded.getLowBatteryThreshold(), 1e-9);
    }
}
