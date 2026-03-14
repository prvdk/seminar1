package org.example;

import lombok.Getter;

import java.util.Objects;

@Getter
public class MissionRequest {

    private final String missionName;
    private final String constellationName;
    private final SatelliteType satelliteType;

    public MissionRequest(String missionName, String constellationName, SatelliteType satelliteType) {
        this.missionName = Objects.requireNonNull(missionName, "Mission name must not be null");
        this.constellationName = Objects.requireNonNull(constellationName, "Constellation name must not be null");
        this.satelliteType = satelliteType;
    }
}
