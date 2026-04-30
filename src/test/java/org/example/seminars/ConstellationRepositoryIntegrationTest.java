package org.example.seminars;

import org.example.CommunicationSatellite;
import org.example.CommunicationSatelliteFactory;
import org.example.CommunicationSatelliteParam;
import org.example.ConstellationService;
import org.example.ConstellationRepository;
import org.example.ImagingSatellite;
import org.example.ImagingSatelliteFactory;
import org.example.ImagingSatelliteParam;
import org.example.Satellite;
import org.example.SatelliteConstellation;
import org.example.SatelliteFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Integration tests for ConstellationRepository with Spring context")
@SpringBootTest
@Transactional
class ConstellationRepositoryIntegrationTest {

    private static final String INTEGRATION_CONSTELLATION_NAME = "Integration-Orbit";
    private static final String UNKNOWN_CONSTELLATION_NAME = "Unknown-Integration-Orbit";
    private static final String COMMUNICATION_SATELLITE_NAME = "Comm-Integration-1";
    private static final String IMAGING_SATELLITE_NAME = "Image-Integration-1";
    private static final double HIGH_BATTERY_LEVEL = 0.95;
    private static final double LOW_BATTERY_LEVEL = 0.10;
    private static final double COMMUNICATION_BANDWIDTH = 600.0;
    private static final double IMAGING_RESOLUTION = 1.5;

    private final SatelliteFactory communicationFactory = new CommunicationSatelliteFactory();
    private final SatelliteFactory imagingFactory = new ImagingSatelliteFactory();

    @Autowired
    private ConstellationRepository constellationRepository;

    @Autowired
    private ConstellationService constellationService;

    @BeforeEach
    void cleanRepository() {
        constellationRepository.deleteAll();
    }

    @Test
    @DisplayName("full lifecycle should create add activate and execute missions for constellation")
    void fullLifecycle_shouldCreateAddActivateAndExecuteMissionsForConstellation() {
        constellationService.createAndSaveConstellation(INTEGRATION_CONSTELLATION_NAME);
        CommunicationSatellite communicationSatellite = createCommunicationSatellite(
                COMMUNICATION_SATELLITE_NAME,
                HIGH_BATTERY_LEVEL,
                COMMUNICATION_BANDWIDTH);
        ImagingSatellite imagingSatellite = createImagingSatellite(
                IMAGING_SATELLITE_NAME,
                HIGH_BATTERY_LEVEL,
                IMAGING_RESOLUTION);

        SatelliteConstellation createdConstellation = constellationRepository
                .findByConstellationName(INTEGRATION_CONSTELLATION_NAME)
                .orElseThrow();
        assertNotNull(createdConstellation);
        assertEquals(0, createdConstellation.getSatellites().size());

        constellationService.addSatelliteToConstellation(INTEGRATION_CONSTELLATION_NAME, communicationSatellite);
        constellationService.addSatelliteToConstellation(INTEGRATION_CONSTELLATION_NAME, imagingSatellite);

        assertEquals(2, createdConstellation.getSatellites().size());
        assertTrue(createdConstellation.getSatellites().stream().noneMatch(Satellite::isActive));

        constellationService.activateAllSatellites(INTEGRATION_CONSTELLATION_NAME);
        assertTrue(createdConstellation.getSatellites().stream().allMatch(Satellite::isActive));

        double communicationBatteryBeforeMission = communicationSatellite.getBatteryLevel();
        double imagingBatteryBeforeMission = imagingSatellite.getBatteryLevel();
        int photosBeforeMission = imagingSatellite.getPhotosTaken();

        constellationService.executeConstellationMission(INTEGRATION_CONSTELLATION_NAME);

        SatelliteConstellation afterMission = constellationRepository
                .findByConstellationName(INTEGRATION_CONSTELLATION_NAME)
                .orElseThrow();
        CommunicationSatellite updatedCommunication = (CommunicationSatellite) afterMission.getSatellites().stream()
                .filter(satellite -> satellite.getName().equals(COMMUNICATION_SATELLITE_NAME))
                .findFirst()
                .orElseThrow();
        ImagingSatellite updatedImaging = (ImagingSatellite) afterMission.getSatellites().stream()
                .filter(satellite -> satellite.getName().equals(IMAGING_SATELLITE_NAME))
                .findFirst()
                .orElseThrow();

        assertTrue(updatedCommunication.getBatteryLevel() < communicationBatteryBeforeMission);
        assertTrue(updatedImaging.getBatteryLevel() < imagingBatteryBeforeMission);
        assertEquals(photosBeforeMission + 1, updatedImaging.getPhotosTaken());
    }

    @Test
    @DisplayName("add satellite should throw exception for unknown constellation")
    void addSatellite_shouldThrowExceptionForUnknownConstellation() {
        Satellite communicationSatellite = communicationFactory.createSatelliteWithParameter(
                new CommunicationSatelliteParam(
                        COMMUNICATION_SATELLITE_NAME,
                        HIGH_BATTERY_LEVEL,
                        COMMUNICATION_BANDWIDTH
                ));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> constellationService.addSatelliteToConstellation(UNKNOWN_CONSTELLATION_NAME, communicationSatellite)
        );

        assertTrue(exception.getMessage().contains("Группировка не найдена"));
    }

    @Test
    @DisplayName("createAndSaveConstellation should return existing constellation for duplicate name")
    void createAndSaveConstellation_shouldReturnExistingConstellationForDuplicateName() {
        SatelliteConstellation firstCreation = constellationService.createAndSaveConstellation(INTEGRATION_CONSTELLATION_NAME);
        SatelliteConstellation secondCreation = constellationService.createAndSaveConstellation(INTEGRATION_CONSTELLATION_NAME);
        Map<String, SatelliteConstellation> allConstellations = constellationService.getAllConstellations();

        assertEquals(firstCreation.getConstellationName(), secondCreation.getConstellationName());
        assertEquals(1, allConstellations.size());
        assertTrue(allConstellations.containsKey(INTEGRATION_CONSTELLATION_NAME));
    }

    @Test
    @DisplayName("activateAllSatellites should keep low-battery satellite inactive")
    void activateAllSatellites_shouldKeepLowBatterySatelliteInactive() {
        constellationService.createAndSaveConstellation(INTEGRATION_CONSTELLATION_NAME);
        CommunicationSatellite lowBatterySatellite = createCommunicationSatellite(
                "Low-Battery-Comm",
                LOW_BATTERY_LEVEL,
                COMMUNICATION_BANDWIDTH);
        constellationService.addSatelliteToConstellation(INTEGRATION_CONSTELLATION_NAME, lowBatterySatellite);

        constellationService.activateAllSatellites(INTEGRATION_CONSTELLATION_NAME);

        assertFalse(lowBatterySatellite.isActive());
        assertDoesNotThrow(() -> constellationService.showConstellationStatus(INTEGRATION_CONSTELLATION_NAME));
    }

    private CommunicationSatellite createCommunicationSatellite(String name, double batteryLevel, double bandwidth) {
        return (CommunicationSatellite) communicationFactory.createSatelliteWithParameter(
                new CommunicationSatelliteParam(name, batteryLevel, bandwidth)
        );
    }

    private ImagingSatellite createImagingSatellite(String name, double batteryLevel, double resolution) {
        return (ImagingSatellite) imagingFactory.createSatelliteWithParameter(
                new ImagingSatelliteParam(name, batteryLevel, resolution)
        );
    }
}
