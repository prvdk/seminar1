package org.example;

import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
public class AddSatelliteRequest {

    private final String constellationName;
    private final List<SatelliteParam> satelliteParams;

    public AddSatelliteRequest(String constellationName, List<SatelliteParam> satelliteParams) {
        this.constellationName = Objects.requireNonNull(constellationName, "Constellation name must not be null");
        this.satelliteParams = List.copyOf(Objects.requireNonNull(satelliteParams, "Satellite params must not be null"));
    }
}
