package org.example.seminars;

import org.example.AddSatelliteRequest;
import org.example.CommunicationSatellite;
import org.example.CommunicationSatelliteParam;
import org.example.ConstellationRepository;
import org.example.ImagingSatellite;
import org.example.ImagingSatelliteParam;
import org.example.MissionRequest;
import org.example.MissionTargetType;
import org.example.SatelliteConstellation;
import org.example.SpaceOperationCenterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Integration tests for SpaceOperationCenterService facade")
@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
class SpaceOperationCenterServiceIntegrationTest {

    private static final String PRIMARY_CONSTELLATION_NAME = "Service-Integration-Orbit";
    private static final double HIGH_BATTERY_LEVEL = 0.90;
    private static final double LOW_BATTERY_LEVEL = 0.10;
    private static final double COMMUNICATION_BANDWIDTH = 550.0;
    private static final double IMAGING_RESOLUTION = 1.2;

    @Autowired
    private SpaceOperationCenterService operationCenterService;

    @Autowired
    private ConstellationRepository constellationRepository;

    @BeforeEach
    void cleanRepository() {
        constellationRepository.getAllConstellations().clear();
    }

    @Test
    @DisplayName("addSatellite should create constellation and add all requested satellites")
    void addSatellite_shouldCreateConstellationAndAddAllRequestedSatellites(CapturedOutput output) {
        AddSatelliteRequest request = new AddSatelliteRequest(
                PRIMARY_CONSTELLATION_NAME,
                List.of(
                        new CommunicationSatelliteParam("Service-Comm-High", HIGH_BATTERY_LEVEL, COMMUNICATION_BANDWIDTH),
                        new ImagingSatelliteParam("Service-Image-High", HIGH_BATTERY_LEVEL, IMAGING_RESOLUTION)
                )
        );

        SatelliteConstellation createdConstellation = operationCenterService.addSatellite(request);

        assertEquals(PRIMARY_CONSTELLATION_NAME, createdConstellation.getConstellationName());
        assertEquals(2, createdConstellation.getSatellites().size());
        assertInstanceOf(CommunicationSatellite.class, createdConstellation.getSatellites().get(0));
        assertInstanceOf(ImagingSatellite.class, createdConstellation.getSatellites().get(1));
        assertTrue(output.getOut().contains("[ExecutionTime]"));
        assertTrue(output.getOut().contains("SpaceOperationCenterService.addSatellite(..)"));
    }

    @Test
    @DisplayName("executeMission should run mission for single selected satellite")
    void executeMission_shouldRunMissionForSingleSelectedSatellite() {
        operationCenterService.addSatellite(new AddSatelliteRequest(
                PRIMARY_CONSTELLATION_NAME,
                List.of(
                        new CommunicationSatelliteParam("Service-Comm-High", HIGH_BATTERY_LEVEL, COMMUNICATION_BANDWIDTH),
                        new ImagingSatelliteParam("Service-Image-High", HIGH_BATTERY_LEVEL, IMAGING_RESOLUTION)
                )
        ));
        operationCenterService.activateConstellation(PRIMARY_CONSTELLATION_NAME);

        SatelliteConstellation constellation = constellationRepository.findByName(PRIMARY_CONSTELLATION_NAME).orElseThrow();
        CommunicationSatellite communicationSatellite = assertInstanceOf(
                CommunicationSatellite.class,
                constellation.getSatellites().get(0)
        );
        ImagingSatellite imagingSatellite = assertInstanceOf(
                ImagingSatellite.class,
                constellation.getSatellites().get(1)
        );

        double communicationBatteryBeforeMission = communicationSatellite.getBatteryLevel();
        double imagingBatteryBeforeMission = imagingSatellite.getBatteryLevel();

        operationCenterService.executeMission(
                new MissionRequest(
                        MissionTargetType.SINGLE_SATELLITE,
                        PRIMARY_CONSTELLATION_NAME,
                        imagingSatellite.getName()
                )
        );

        assertEquals(communicationBatteryBeforeMission, communicationSatellite.getBatteryLevel());
        assertTrue(imagingSatellite.getBatteryLevel() < imagingBatteryBeforeMission);
        assertEquals(1, imagingSatellite.getPhotosTaken());
        assertTrue(communicationSatellite.isActive());
        assertTrue(imagingSatellite.isActive());
    }

    @Test
    @DisplayName("activateConstellation should activate supported satellites and keep low-battery ones inactive")
    void activateConstellation_shouldActivateSupportedSatellitesAndKeepLowBatteryOnesInactive() {
        operationCenterService.addSatellite(new AddSatelliteRequest(
                PRIMARY_CONSTELLATION_NAME,
                List.of(
                        new CommunicationSatelliteParam("Service-Comm-High", HIGH_BATTERY_LEVEL, COMMUNICATION_BANDWIDTH),
                        new CommunicationSatelliteParam("Service-Comm-Low", LOW_BATTERY_LEVEL, COMMUNICATION_BANDWIDTH)
                )
        ));
        operationCenterService.activateConstellation(PRIMARY_CONSTELLATION_NAME);

        SatelliteConstellation constellation = constellationRepository.findByName(PRIMARY_CONSTELLATION_NAME).orElseThrow();

        CommunicationSatellite highBatterySatellite = assertInstanceOf(
                CommunicationSatellite.class,
                constellation.getSatellites().get(0)
        );
        CommunicationSatellite lowBatterySatellite = assertInstanceOf(
                CommunicationSatellite.class,
                constellation.getSatellites().get(1)
        );

        assertTrue(highBatterySatellite.isActive());
        assertFalse(lowBatterySatellite.isActive());
    }

    @Test
    @DisplayName("addSatellite should return existing constellation for duplicate name")
    void addSatellite_shouldReturnExistingConstellationForDuplicateName() {
        SatelliteConstellation firstCreation = operationCenterService.addSatellite(new AddSatelliteRequest(
                PRIMARY_CONSTELLATION_NAME,
                List.of(new CommunicationSatelliteParam("Service-Comm-High", HIGH_BATTERY_LEVEL, COMMUNICATION_BANDWIDTH))
        ));
        SatelliteConstellation secondCreation = operationCenterService.addSatellite(new AddSatelliteRequest(
                PRIMARY_CONSTELLATION_NAME,
                List.of()
        ));

        assertEquals(firstCreation, secondCreation);
        assertEquals(1, constellationRepository.getAllConstellations().size());
    }
}
