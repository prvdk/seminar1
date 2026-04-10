package org.example;

import java.util.List;

public record AddSatelliteRequest(
        String constellationName,
        List<SatelliteParam> satelliteParams
) {
}
