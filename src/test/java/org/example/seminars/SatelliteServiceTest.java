package org.example.seminars;

import org.example.ImagingSatellite;
import org.example.ImagingSatelliteParam;
import org.example.Satellite;
import org.example.SatelliteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@SpringBootTest
@DisplayName("Integration tests for SatelliteServiceImpl")
class SatelliteServiceTest {

    private static final double DELTA = 1e-9;

    @Autowired
    private SatelliteService satelliteService;

    @Test
    @DisplayName("service should create imaging satellite by parameter type")
    void service_shouldCreateImagingSatelliteByParameterType() {
        ImagingSatelliteParam param = new ImagingSatelliteParam("Image-Service-1", 0.88, 0.9);

        Satellite createdSatellite = satelliteService.createSatellite(param);

        ImagingSatellite imagingSatellite = assertInstanceOf(ImagingSatellite.class, createdSatellite);
        assertEquals("Image-Service-1", imagingSatellite.getName());
        assertEquals(0.88, imagingSatellite.getBatteryLevel(), DELTA);
        assertEquals(0.9, imagingSatellite.getResolution(), DELTA);
    }
}
