package org.example.seminars;

import org.example.ConstellationRepository;
import org.example.Satellite;
import org.example.SatelliteConstellation;
import org.example.SatelliteState;
import org.example.SpaceOperationCenterService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Mock tests for SpaceOperationCenterService")
@ExtendWith(MockitoExtension.class)
class SpaceOperationCenterServiceMockTest {

    private static final String PRIMARY_CONSTELLATION_NAME = "Service-Mock-Orbit-Alpha";
    private static final String UNKNOWN_CONSTELLATION_NAME = "Service-Mock-Orbit-Unknown";
    private static final String FIRST_SATELLITE_NAME = "Service-Mock-Satellite-1";
    private static final String SECOND_SATELLITE_NAME = "Service-Mock-Satellite-2";

    @Mock
    private ConstellationRepository constellationRepository;

    @InjectMocks
    private SpaceOperationCenterService operationCenterService;

    @Test
    @DisplayName("createAndSaveConstellation should save new constellation when name is unique")
    void createAndSaveConstellation_shouldSaveNewConstellationWhenNameIsUnique() {
        when(constellationRepository.existsByName(PRIMARY_CONSTELLATION_NAME)).thenReturn(false);
        when(constellationRepository.save(any(SatelliteConstellation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SatelliteConstellation savedConstellation =
                operationCenterService.createAndSaveConstellation(PRIMARY_CONSTELLATION_NAME);

        ArgumentCaptor<SatelliteConstellation> constellationCaptor =
                ArgumentCaptor.forClass(SatelliteConstellation.class);
        verify(constellationRepository).save(constellationCaptor.capture());

        assertEquals(PRIMARY_CONSTELLATION_NAME, constellationCaptor.getValue().getConstellationName());
        assertEquals(PRIMARY_CONSTELLATION_NAME, savedConstellation.getConstellationName());
        verify(constellationRepository).existsByName(PRIMARY_CONSTELLATION_NAME);
        verify(constellationRepository, never()).findByName(PRIMARY_CONSTELLATION_NAME);
    }

    @Test
    @DisplayName("createAndSaveConstellation should return existing constellation for duplicate name")
    void createAndSaveConstellation_shouldReturnExistingConstellationForDuplicateName() {
        SatelliteConstellation existingConstellation = new SatelliteConstellation(PRIMARY_CONSTELLATION_NAME);
        when(constellationRepository.existsByName(PRIMARY_CONSTELLATION_NAME)).thenReturn(true);
        when(constellationRepository.findByName(PRIMARY_CONSTELLATION_NAME))
                .thenReturn(Optional.of(existingConstellation));

        SatelliteConstellation result = operationCenterService.createAndSaveConstellation(PRIMARY_CONSTELLATION_NAME);

        assertSame(existingConstellation, result);
        verify(constellationRepository).findByName(PRIMARY_CONSTELLATION_NAME);
        verify(constellationRepository, never()).save(any(SatelliteConstellation.class));
    }

    @Test
    @DisplayName("addSatelliteToConstellation should pass satellite to located constellation")
    void addSatelliteToConstellation_shouldPassSatelliteToLocatedConstellation() {
        SatelliteConstellation locatedConstellation = org.mockito.Mockito.mock(SatelliteConstellation.class);
        Satellite satellite = org.mockito.Mockito.mock(Satellite.class);
        when(constellationRepository.findByName(PRIMARY_CONSTELLATION_NAME))
                .thenReturn(Optional.of(locatedConstellation));
        when(satellite.getName()).thenReturn(FIRST_SATELLITE_NAME);

        operationCenterService.addSatelliteToConstellation(PRIMARY_CONSTELLATION_NAME, satellite);

        verify(locatedConstellation).addSatellite(satellite);
        verify(constellationRepository).findByName(PRIMARY_CONSTELLATION_NAME);
    }

    @Test
    @DisplayName("addSatelliteToConstellation should throw exception for unknown constellation")
    void addSatelliteToConstellation_shouldThrowExceptionForUnknownConstellation() {
        Satellite satellite = org.mockito.Mockito.mock(Satellite.class);
        when(constellationRepository.findByName(UNKNOWN_CONSTELLATION_NAME)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> operationCenterService.addSatelliteToConstellation(UNKNOWN_CONSTELLATION_NAME, satellite)
        );

        assertTrue(exception.getMessage().contains("Группировка не найдена"));
        verify(constellationRepository).findByName(UNKNOWN_CONSTELLATION_NAME);
    }

    @Test
    @DisplayName("executeConstellationMission should trigger execution for located constellation")
    void executeConstellationMission_shouldTriggerExecutionForLocatedConstellation() {
        SatelliteConstellation locatedConstellation = org.mockito.Mockito.mock(SatelliteConstellation.class);
        when(constellationRepository.findByName(PRIMARY_CONSTELLATION_NAME))
                .thenReturn(Optional.of(locatedConstellation));

        operationCenterService.executeConstellationMission(PRIMARY_CONSTELLATION_NAME);

        verify(locatedConstellation).executeAllMissions();
    }

    @Test
    @DisplayName("activateAllSatellites should activate each satellite in constellation")
    void activateAllSatellites_shouldActivateEachSatelliteInConstellation() {
        SatelliteConstellation constellation = org.mockito.Mockito.mock(SatelliteConstellation.class);
        Satellite firstSatellite = org.mockito.Mockito.mock(Satellite.class);
        Satellite secondSatellite = org.mockito.Mockito.mock(Satellite.class);
        when(constellationRepository.findByName(PRIMARY_CONSTELLATION_NAME)).thenReturn(Optional.of(constellation));
        when(constellation.getSatellites()).thenReturn(List.of(firstSatellite, secondSatellite));
        when(firstSatellite.getName()).thenReturn(FIRST_SATELLITE_NAME);
        when(secondSatellite.getName()).thenReturn(SECOND_SATELLITE_NAME);
        when(firstSatellite.activate()).thenReturn(true);
        when(secondSatellite.activate()).thenReturn(false);
        when(secondSatellite.getBatteryLevel()).thenReturn(0.10);

        operationCenterService.activateAllSatellites(PRIMARY_CONSTELLATION_NAME);

        verify(firstSatellite).activate();
        verify(secondSatellite).activate();
        verify(secondSatellite).getBatteryLevel();
        verify(firstSatellite, never()).getBatteryLevel();
    }

    @Test
    @DisplayName("showConstellationStatus should request state for each satellite")
    void showConstellationStatus_shouldRequestStateForEachSatellite() {
        SatelliteConstellation constellation = org.mockito.Mockito.mock(SatelliteConstellation.class);
        Satellite satellite = org.mockito.Mockito.mock(Satellite.class);
        when(constellationRepository.findByName(PRIMARY_CONSTELLATION_NAME)).thenReturn(Optional.of(constellation));
        when(constellation.getSatellites()).thenReturn(List.of(satellite));
        when(satellite.getState()).thenReturn(new SatelliteState());

        operationCenterService.showConstellationStatus(PRIMARY_CONSTELLATION_NAME);

        verify(satellite).getState();
    }

    @Test
    @DisplayName("getAllConstellations should return repository snapshot")
    void getAllConstellations_shouldReturnRepositorySnapshot() {
        Map<String, SatelliteConstellation> repositorySnapshot = new LinkedHashMap<>();
        repositorySnapshot.put(PRIMARY_CONSTELLATION_NAME, new SatelliteConstellation(PRIMARY_CONSTELLATION_NAME));
        when(constellationRepository.getAllConstellations()).thenReturn(repositorySnapshot);

        Map<String, SatelliteConstellation> actualSnapshot = operationCenterService.getAllConstellations();

        assertSame(repositorySnapshot, actualSnapshot);
        verify(constellationRepository).getAllConstellations();
    }
}
