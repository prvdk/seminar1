package org.example.seminars;

import org.example.ConstellationRepository;
import org.example.ConstellationService;
import org.example.Satellite;
import org.example.SatelliteConstellation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Mock tests for ConstellationService")
@ExtendWith(MockitoExtension.class)
class ConstellationServiceMockTest {

    private static final String CONSTELLATION_NAME = "Service-Mock-Orbit-Alpha";
    private static final String UNKNOWN_CONSTELLATION_NAME = "Service-Mock-Orbit-Unknown";

    @Mock
    private ConstellationRepository constellationRepository;

    @InjectMocks
    private ConstellationService constellationService;

    @Test
    @DisplayName("createAndSaveConstellation should save new constellation")
    void createAndSaveConstellation_shouldSaveNewConstellation() {
        when(constellationRepository.existsByConstellationName(CONSTELLATION_NAME)).thenReturn(false);
        when(constellationRepository.save(any(SatelliteConstellation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SatelliteConstellation result = constellationService.createAndSaveConstellation(CONSTELLATION_NAME);

        assertEquals(CONSTELLATION_NAME, result.getConstellationName());
        verify(constellationRepository).save(any(SatelliteConstellation.class));
    }

    @Test
    @DisplayName("createAndSaveConstellation should return existing constellation")
    void createAndSaveConstellation_shouldReturnExistingConstellation() {
        SatelliteConstellation existing = new SatelliteConstellation(CONSTELLATION_NAME);
        when(constellationRepository.existsByConstellationName(CONSTELLATION_NAME)).thenReturn(true);
        when(constellationRepository.findByConstellationName(CONSTELLATION_NAME)).thenReturn(Optional.of(existing));

        SatelliteConstellation result = constellationService.createAndSaveConstellation(CONSTELLATION_NAME);

        assertSame(existing, result);
        verify(constellationRepository, never()).save(any(SatelliteConstellation.class));
    }

    @Test
    @DisplayName("addSatelliteToConstellation should throw for unknown constellation")
    void addSatelliteToConstellation_shouldThrowForUnknownConstellation() {
        Satellite satellite = org.mockito.Mockito.mock(Satellite.class);
        when(constellationRepository.findByConstellationName(UNKNOWN_CONSTELLATION_NAME)).thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> constellationService.addSatelliteToConstellation(UNKNOWN_CONSTELLATION_NAME, satellite)
        );
    }

    @Test
    @DisplayName("getAllConstellations should return map with all names")
    void getAllConstellations_shouldReturnMapWithAllNames() {
        SatelliteConstellation first = new SatelliteConstellation("A");
        SatelliteConstellation second = new SatelliteConstellation("B");
        when(constellationRepository.findAll()).thenReturn(List.of(first, second));

        assertEquals(2, constellationService.getAllConstellations().size());
    }
}
