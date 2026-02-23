package org.example.seminars;

import org.example.ConstellationRepository;
import org.example.SatelliteConstellation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Mock tests for ConstellationRepository")
@ExtendWith(MockitoExtension.class)
class ConstellationRepositoryMockTest {

    private static final String CONSTELLATION_ALPHA_NAME = "Mock-Orbit-Alpha";
    private static final String CONSTELLATION_BETA_NAME = "Mock-Orbit-Beta";
    private static final String MISSING_CONSTELLATION_NAME = "Missing-Mock-Orbit";

    @Mock
    private ConstellationRepository repository;

    @Test
    @DisplayName("getAllConstellations should return prepared map from mock repository")
    void getAllConstellations_shouldReturnPreparedMapFromMockRepository() {
        SatelliteConstellation alphaConstellation = new SatelliteConstellation(CONSTELLATION_ALPHA_NAME);
        SatelliteConstellation betaConstellation = new SatelliteConstellation(CONSTELLATION_BETA_NAME);
        Map<String, SatelliteConstellation> preparedConstellations = new LinkedHashMap<>();
        preparedConstellations.put(CONSTELLATION_ALPHA_NAME, alphaConstellation);
        preparedConstellations.put(CONSTELLATION_BETA_NAME, betaConstellation);

        when(repository.getAllConstellations()).thenReturn(preparedConstellations);
        Map<String, SatelliteConstellation> actualConstellations = repository.getAllConstellations();

        assertEquals(2, actualConstellations.size());
        assertTrue(actualConstellations.containsKey(CONSTELLATION_ALPHA_NAME));
        verify(repository, times(1)).getAllConstellations();
    }

    @Test
    @DisplayName("findByName should return prepared optional when constellation exists")
    void findByName_shouldReturnPreparedOptionalWhenConstellationExists() {
        SatelliteConstellation expectedConstellation = new SatelliteConstellation(CONSTELLATION_ALPHA_NAME);

        when(repository.findByName(CONSTELLATION_ALPHA_NAME)).thenReturn(Optional.of(expectedConstellation));
        Optional<SatelliteConstellation> foundConstellation = repository.findByName(CONSTELLATION_ALPHA_NAME);

        assertTrue(foundConstellation.isPresent());
        assertEquals(CONSTELLATION_ALPHA_NAME, foundConstellation.orElseThrow().getConstellationName());
        verify(repository, times(1)).findByName(CONSTELLATION_ALPHA_NAME);
    }

    @Test
    @DisplayName("save should return prepared constellation from mock repository")
    void save_shouldReturnPreparedConstellationFromMockRepository() {
        SatelliteConstellation expectedConstellation = new SatelliteConstellation(CONSTELLATION_ALPHA_NAME);

        when(repository.save(expectedConstellation)).thenReturn(expectedConstellation);
        SatelliteConstellation savedConstellation = repository.save(expectedConstellation);

        assertEquals(CONSTELLATION_ALPHA_NAME, savedConstellation.getConstellationName());
        verify(repository, times(1)).save(expectedConstellation);
    }

    @Test
    @DisplayName("existsByName should return false for missing constellation in mock")
    void existsByName_shouldReturnFalseForMissingConstellationInMock() {
        when(repository.existsByName(MISSING_CONSTELLATION_NAME)).thenReturn(false);
        boolean exists = repository.existsByName(MISSING_CONSTELLATION_NAME);

        assertFalse(exists);
        verify(repository, times(1)).existsByName(MISSING_CONSTELLATION_NAME);
    }

    @Test
    @DisplayName("deleteByName should verify invocation with target name")
    void deleteByName_shouldVerifyInvocationWithTargetName() {
        repository.deleteByName(CONSTELLATION_BETA_NAME);

        verify(repository, times(1)).deleteByName(CONSTELLATION_BETA_NAME);
        verify(repository, never()).deleteByName(MISSING_CONSTELLATION_NAME);
    }
}
