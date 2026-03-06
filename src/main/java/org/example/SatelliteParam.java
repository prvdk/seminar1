package org.example;

import lombok.Getter;

import java.util.Objects;

@Getter
public abstract class SatelliteParam {

    private final SatelliteType type;
    private final String name;
    private final double batteryLevel;

    protected SatelliteParam(SatelliteType type, String name, double batteryLevel) {
        this.type = Objects.requireNonNull(type, "Satellite type must not be null");
        this.name = Objects.requireNonNull(name, "Satellite name must not be null");
        this.batteryLevel = batteryLevel;
    }
}
