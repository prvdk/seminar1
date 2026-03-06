package org.example.seminars;

import org.example.CommunicationSatellite;
import org.example.CommunicationSatelliteFactory;
import org.example.CommunicationSatelliteParam;
import org.example.ImagingSatellite;
import org.example.ImagingSatelliteFactory;
import org.example.ImagingSatelliteParam;
import org.example.Satellite;
import org.example.SatelliteFactory;
import org.example.SatelliteType;
import org.example.SpaceOperationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Unit tests for SatelliteFactory implementations")
class SatelliteFactoryUnitTest {

    private static final double DELTA = 1e-9;

    @Test
    @DisplayName("communication factory should create satellite with explicit bandwidth")
    void communicationFactory_shouldCreateSatelliteWithExplicitBandwidth() {
        SatelliteFactory communicationFactory = new CommunicationSatelliteFactory();

        Satellite createdSatellite = communicationFactory.createSatelliteWithParameter(
                new CommunicationSatelliteParam("Comm-Custom", 0.8, 750.0)
        );

        CommunicationSatellite communicationSatellite = assertInstanceOf(CommunicationSatellite.class, createdSatellite);
        assertEquals("Comm-Custom", communicationSatellite.getName());
        assertEquals(0.8, communicationSatellite.getBatteryLevel(), DELTA);
        assertEquals(750.0, communicationSatellite.getBandwidth(), DELTA);
        assertTrue(communicationFactory.isSatelliteTypeSupported(SatelliteType.COMMUNICATION));
    }

    @Test
    @DisplayName("imaging factory should create satellite with explicit resolution")
    void imagingFactory_shouldCreateSatelliteWithExplicitResolution() {
        SatelliteFactory imagingFactory = new ImagingSatelliteFactory();

        Satellite createdSatellite = imagingFactory.createSatelliteWithParameter(
                new ImagingSatelliteParam("Img-Custom", 0.9, 1.1)
        );

        ImagingSatellite imagingSatellite = assertInstanceOf(ImagingSatellite.class, createdSatellite);
        assertEquals("Img-Custom", imagingSatellite.getName());
        assertEquals(0.9, imagingSatellite.getBatteryLevel(), DELTA);
        assertEquals(1.1, imagingSatellite.getResolution(), DELTA);
        assertTrue(imagingFactory.isSatelliteTypeSupported(SatelliteType.IMAGE));
    }

    @Test
    @DisplayName("imaging factory should throw exception for unsupported parameter type")
    void imagingFactory_shouldThrowExceptionForUnsupportedParameterType() {
        SatelliteFactory imagingFactory = new ImagingSatelliteFactory();

        assertThrows(
                SpaceOperationException.class,
                () -> imagingFactory.createSatelliteWithParameter(
                        new CommunicationSatelliteParam("Wrong-Param", 0.8, 500.0)
                )
        );
    }
}
