package org.example.seminars;

import org.example.CommunicationSatellite;
import org.example.CommunicationSatelliteFactory;
import org.example.ImagingSatellite;
import org.example.ImagingSatelliteFactory;
import org.example.Satellite;
import org.example.SatelliteFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@DisplayName("Unit tests for SatelliteFactory implementations")
class SatelliteFactoryUnitTest {

    private static final double DELTA = 1e-9;

    @Test
    @DisplayName("communication factory should create satellite with default parameter")
    void communicationFactory_shouldCreateSatelliteWithDefaultParameter() {
        SatelliteFactory communicationFactory = new CommunicationSatelliteFactory();

        Satellite createdSatellite = communicationFactory.createSatellite("Comm-Default", 0.7);

        CommunicationSatellite communicationSatellite = assertInstanceOf(CommunicationSatellite.class, createdSatellite);
        assertEquals("Comm-Default", communicationSatellite.getName());
        assertEquals(0.7, communicationSatellite.getBatteryLevel(), DELTA);
        assertEquals(100.0, communicationSatellite.getBandwidth(), DELTA);
    }

    @Test
    @DisplayName("communication factory should create satellite with explicit bandwidth")
    void communicationFactory_shouldCreateSatelliteWithExplicitBandwidth() {
        SatelliteFactory communicationFactory = new CommunicationSatelliteFactory();

        Satellite createdSatellite = communicationFactory.createSatelliteWithParameter("Comm-Custom", 0.8, 750.0);

        CommunicationSatellite communicationSatellite = assertInstanceOf(CommunicationSatellite.class, createdSatellite);
        assertEquals(750.0, communicationSatellite.getBandwidth(), DELTA);
    }

    @Test
    @DisplayName("imaging factory should create satellite with default parameter")
    void imagingFactory_shouldCreateSatelliteWithDefaultParameter() {
        SatelliteFactory imagingFactory = new ImagingSatelliteFactory();

        Satellite createdSatellite = imagingFactory.createSatellite("Img-Default", 0.6);

        ImagingSatellite imagingSatellite = assertInstanceOf(ImagingSatellite.class, createdSatellite);
        assertEquals("Img-Default", imagingSatellite.getName());
        assertEquals(0.6, imagingSatellite.getBatteryLevel(), DELTA);
        assertEquals(5.0, imagingSatellite.getResolution(), DELTA);
    }

    @Test
    @DisplayName("imaging factory should create satellite with explicit resolution")
    void imagingFactory_shouldCreateSatelliteWithExplicitResolution() {
        SatelliteFactory imagingFactory = new ImagingSatelliteFactory();

        Satellite createdSatellite = imagingFactory.createSatelliteWithParameter("Img-Custom", 0.9, 1.1);

        ImagingSatellite imagingSatellite = assertInstanceOf(ImagingSatellite.class, createdSatellite);
        assertEquals(1.1, imagingSatellite.getResolution(), DELTA);
    }
}
