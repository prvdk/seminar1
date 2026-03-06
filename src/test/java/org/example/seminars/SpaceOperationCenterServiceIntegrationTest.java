package org.example.seminars;

import org.example.CommunicationSatellite;
import org.example.CommunicationSatelliteFactory;
import org.example.CommunicationSatelliteParam;
import org.example.ConstellationRepository;
import org.example.ImagingSatellite;
import org.example.ImagingSatelliteFactory;
import org.example.ImagingSatelliteParam;
import org.example.Satellite;
import org.example.SatelliteConstellation;
import org.example.SatelliteFactory;
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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Integration tests for SpaceOperationCenterService")
@SpringBootTest
class SpaceOperationCenterServiceIntegrationTest {

    private static final String PRIMARY_CONSTELLATION_NAME = "Service-Integration-Orbit";
    private static final String UNKNOWN_CONSTELLATION_NAME = "Service-Integration-Orbit-Unknown";
    private static final String HIGH_BATTERY_COMMUNICATION_NAME = "Service-Comm-High";
    private static final String HIGH_BATTERY_IMAGING_NAME = "Service-Image-High";
    private static final String LOW_BATTERY_COMMUNICATION_NAME = "Service-Comm-Low";
    private static final double HIGH_BATTERY_LEVEL = 0.90;
    private static final double LOW_BATTERY_LEVEL = 0.10;
    private static final double COMMUNICATION_BANDWIDTH = 550.0;
    private static final double IMAGING_RESOLUTION = 1.2;
    private static final double DELTA = 1e-9;

    private final SatelliteFactory communicationFactory = new CommunicationSatelliteFactory();
    private final SatelliteFactory imagingFactory = new ImagingSatelliteFactory();

    @Autowired
    private SpaceOperationCenterService operationCenterService;

    @Autowired
    private ConstellationRepository constellationRepository;

    @BeforeEach
    void cleanRepository() {
        constellationRepository.getAllConstellations().clear();
    }

    @Test
    @DisplayName("full lifecycle should create add activate and execute missions through service")
    void fullLifecycle_shouldCreateAddActivateAndExecuteMissionsThroughService() {
        operationCenterService.createAndSaveConstellation(PRIMARY_CONSTELLATION_NAME);
        CommunicationSatellite communicationSatellite = createCommunicationSatellite(
                HIGH_BATTERY_COMMUNICATION_NAME,
                HIGH_BATTERY_LEVEL,
                COMMUNICATION_BANDWIDTH);
        ImagingSatellite imagingSatellite = createImagingSatellite(
                HIGH_BATTERY_IMAGING_NAME,
                HIGH_BATTERY_LEVEL,
                IMAGING_RESOLUTION);

        operationCenterService.addSatelliteToConstellation(PRIMARY_CONSTELLATION_NAME, communicationSatellite);
        operationCenterService.addSatelliteToConstellation(PRIMARY_CONSTELLATION_NAME, imagingSatellite);

        SatelliteConstellation constellation = constellationRepository.findByName(PRIMARY_CONSTELLATION_NAME).orElseThrow();
        assertEquals(2, constellation.getSatellites().size());
        assertTrue(constellation.getSatellites().stream().noneMatch(Satellite::isActive));

        operationCenterService.activateAllSatellites(PRIMARY_CONSTELLATION_NAME);
        assertTrue(constellation.getSatellites().stream().allMatch(Satellite::isActive));

        double communicationBatteryBeforeMission = communicationSatellite.getBatteryLevel();
        double imagingBatteryBeforeMission = imagingSatellite.getBatteryLevel();
        int photosBeforeMission = imagingSatellite.getPhotosTaken();

        operationCenterService.executeConstellationMission(PRIMARY_CONSTELLATION_NAME);

        assertTrue(communicationSatellite.getBatteryLevel() < communicationBatteryBeforeMission);
        assertTrue(imagingSatellite.getBatteryLevel() < imagingBatteryBeforeMission);
        assertEquals(photosBeforeMission + 1, imagingSatellite.getPhotosTaken());
    }

    @Test
    @DisplayName("executeConstellationMission should not change batteries while satellites are inactive")
    void executeConstellationMission_shouldNotChangeBatteriesWhileSatellitesAreInactive() {
        operationCenterService.createAndSaveConstellation(PRIMARY_CONSTELLATION_NAME);
        CommunicationSatellite communicationSatellite = createCommunicationSatellite(
                HIGH_BATTERY_COMMUNICATION_NAME,
                HIGH_BATTERY_LEVEL,
                COMMUNICATION_BANDWIDTH);
        ImagingSatellite imagingSatellite = createImagingSatellite(
                HIGH_BATTERY_IMAGING_NAME,
                HIGH_BATTERY_LEVEL,
                IMAGING_RESOLUTION);
        operationCenterService.addSatelliteToConstellation(PRIMARY_CONSTELLATION_NAME, communicationSatellite);
        operationCenterService.addSatelliteToConstellation(PRIMARY_CONSTELLATION_NAME, imagingSatellite);
        double communicationBatteryBeforeMission = communicationSatellite.getBatteryLevel();
        double imagingBatteryBeforeMission = imagingSatellite.getBatteryLevel();
        int photosBeforeMission = imagingSatellite.getPhotosTaken();

        operationCenterService.executeConstellationMission(PRIMARY_CONSTELLATION_NAME);

        assertFalse(communicationSatellite.isActive());
        assertFalse(imagingSatellite.isActive());
        assertEquals(communicationBatteryBeforeMission, communicationSatellite.getBatteryLevel(), DELTA);
        assertEquals(imagingBatteryBeforeMission, imagingSatellite.getBatteryLevel(), DELTA);
        assertEquals(photosBeforeMission, imagingSatellite.getPhotosTaken());
    }

    @Test
    @DisplayName("createAndSaveConstellation should return same object for duplicate name")
    void createAndSaveConstellation_shouldReturnSameObjectForDuplicateName() {
        SatelliteConstellation firstCreation = operationCenterService.createAndSaveConstellation(PRIMARY_CONSTELLATION_NAME);
        SatelliteConstellation secondCreation = operationCenterService.createAndSaveConstellation(PRIMARY_CONSTELLATION_NAME);
        Map<String, SatelliteConstellation> allConstellations = operationCenterService.getAllConstellations();

        assertSame(firstCreation, secondCreation);
        assertEquals(1, allConstellations.size());
        assertTrue(allConstellations.containsKey(PRIMARY_CONSTELLATION_NAME));
    }

    @Test
    @DisplayName("activateAllSatellites should keep low battery satellite inactive")
    void activateAllSatellites_shouldKeepLowBatterySatelliteInactive() {
        operationCenterService.createAndSaveConstellation(PRIMARY_CONSTELLATION_NAME);
        CommunicationSatellite lowBatterySatellite = createCommunicationSatellite(
                LOW_BATTERY_COMMUNICATION_NAME,
                LOW_BATTERY_LEVEL,
                COMMUNICATION_BANDWIDTH);
        operationCenterService.addSatelliteToConstellation(PRIMARY_CONSTELLATION_NAME, lowBatterySatellite);

        operationCenterService.activateAllSatellites(PRIMARY_CONSTELLATION_NAME);

        assertFalse(lowBatterySatellite.isActive());
        assertDoesNotThrow(() -> operationCenterService.showConstellationStatus(PRIMARY_CONSTELLATION_NAME));
    }

    @Test
    @DisplayName("addSatelliteToConstellation should throw exception for unknown constellation")
    void addSatelliteToConstellation_shouldThrowExceptionForUnknownConstellation() {
        Satellite communicationSatellite = communicationFactory.createSatelliteWithParameter(
                new CommunicationSatelliteParam(
                        HIGH_BATTERY_COMMUNICATION_NAME,
                        HIGH_BATTERY_LEVEL,
                        COMMUNICATION_BANDWIDTH
                ));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> operationCenterService.addSatelliteToConstellation(UNKNOWN_CONSTELLATION_NAME, communicationSatellite)
        );

        assertTrue(exception.getMessage().contains("Группировка не найдена"));
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
