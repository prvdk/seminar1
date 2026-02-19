package org.example.seminars;

import org.example.CommunicationSatellite;
import org.example.ConstellationRepository;
import org.example.ImagingSatellite;
import org.example.Satellite;
import org.example.SatelliteConstellation;
import org.example.SpaceOperationCenterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Integration tests for ConstellationRepository with Spring context")
@SpringBootTest
class ConstellationRepositoryIntegrationTest {

    private static final String INTEGRATION_CONSTELLATION_NAME = "Integration-Orbit";
    private static final String UNKNOWN_CONSTELLATION_NAME = "Unknown-Integration-Orbit";
    private static final String COMMUNICATION_SATELLITE_NAME = "Comm-Integration-1";
    private static final String IMAGING_SATELLITE_NAME = "Image-Integration-1";
    private static final double HIGH_BATTERY_LEVEL = 0.95;
    private static final double LOW_BATTERY_LEVEL = 0.10;
    private static final double COMMUNICATION_BANDWIDTH = 600.0;
    private static final double IMAGING_RESOLUTION = 1.5;

    @Autowired
    private ConstellationRepository constellationRepository;

    @Autowired
    private SpaceOperationCenterService operationCenterService;

    @BeforeEach
    void cleanRepository() {
        constellationRepository.getAllConstellations().clear();
    }

    @Test
    @DisplayName("full lifecycle should create add activate and execute missions for constellation")
    void fullLifecycle_shouldCreateAddActivateAndExecuteMissionsForConstellation() {
        operationCenterService.createAndSaveConstellation(INTEGRATION_CONSTELLATION_NAME);
        CommunicationSatellite communicationSatellite = new CommunicationSatellite(
                COMMUNICATION_SATELLITE_NAME,
                HIGH_BATTERY_LEVEL,
                COMMUNICATION_BANDWIDTH
        );
        ImagingSatellite imagingSatellite = new ImagingSatellite(
                IMAGING_SATELLITE_NAME,
                HIGH_BATTERY_LEVEL,
                IMAGING_RESOLUTION
        );

        SatelliteConstellation createdConstellation = constellationRepository
                .findByName(INTEGRATION_CONSTELLATION_NAME)
                .orElseThrow();
        assertNotNull(createdConstellation);
        assertEquals(0, createdConstellation.getSatellites().size());

        operationCenterService.addSatelliteToConstellation(INTEGRATION_CONSTELLATION_NAME, communicationSatellite);
        operationCenterService.addSatelliteToConstellation(INTEGRATION_CONSTELLATION_NAME, imagingSatellite);

        assertEquals(2, createdConstellation.getSatellites().size());
        assertTrue(createdConstellation.getSatellites().stream().noneMatch(Satellite::isActive));

        operationCenterService.activateAllSatellites(INTEGRATION_CONSTELLATION_NAME);
        assertTrue(createdConstellation.getSatellites().stream().allMatch(Satellite::isActive));

        double communicationBatteryBeforeMission = communicationSatellite.getBatteryLevel();
        double imagingBatteryBeforeMission = imagingSatellite.getBatteryLevel();
        int photosBeforeMission = imagingSatellite.getPhotosTaken();

        operationCenterService.executeConstellationMission(INTEGRATION_CONSTELLATION_NAME);

        assertTrue(communicationSatellite.getBatteryLevel() < communicationBatteryBeforeMission);
        assertTrue(imagingSatellite.getBatteryLevel() < imagingBatteryBeforeMission);
        assertEquals(photosBeforeMission + 1, imagingSatellite.getPhotosTaken());
    }

    @Test
    @DisplayName("add satellite should throw exception for unknown constellation")
    void addSatellite_shouldThrowExceptionForUnknownConstellation() {
        CommunicationSatellite communicationSatellite = new CommunicationSatellite(
                COMMUNICATION_SATELLITE_NAME,
                HIGH_BATTERY_LEVEL,
                COMMUNICATION_BANDWIDTH
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> operationCenterService.addSatelliteToConstellation(UNKNOWN_CONSTELLATION_NAME, communicationSatellite)
        );

        assertTrue(exception.getMessage().contains("Группировка не найдена"));
    }

    @Test
    @DisplayName("createAndSaveConstellation should return existing constellation for duplicate name")
    void createAndSaveConstellation_shouldReturnExistingConstellationForDuplicateName() {
        SatelliteConstellation firstCreation = operationCenterService.createAndSaveConstellation(INTEGRATION_CONSTELLATION_NAME);
        SatelliteConstellation secondCreation = operationCenterService.createAndSaveConstellation(INTEGRATION_CONSTELLATION_NAME);
        Map<String, SatelliteConstellation> allConstellations = operationCenterService.getAllConstellations();

        assertEquals(firstCreation, secondCreation);
        assertEquals(1, allConstellations.size());
        assertTrue(allConstellations.containsKey(INTEGRATION_CONSTELLATION_NAME));
    }

    @Test
    @DisplayName("activateAllSatellites should keep low-battery satellite inactive")
    void activateAllSatellites_shouldKeepLowBatterySatelliteInactive() {
        operationCenterService.createAndSaveConstellation(INTEGRATION_CONSTELLATION_NAME);
        CommunicationSatellite lowBatterySatellite = new CommunicationSatellite(
                "Low-Battery-Comm",
                LOW_BATTERY_LEVEL,
                COMMUNICATION_BANDWIDTH
        );
        operationCenterService.addSatelliteToConstellation(INTEGRATION_CONSTELLATION_NAME, lowBatterySatellite);

        operationCenterService.activateAllSatellites(INTEGRATION_CONSTELLATION_NAME);

        assertFalse(lowBatterySatellite.isActive());
        assertDoesNotThrow(() -> operationCenterService.showConstellationStatus(INTEGRATION_CONSTELLATION_NAME));
    }
}
