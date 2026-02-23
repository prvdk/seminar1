package org.example;

public class ImagingSatelliteFactory extends SatelliteFactory {

    private static final double DEFAULT_RESOLUTION = 5.0;

    @Override
    public Satellite createSatellite(String name, double batteryLevel) {
        return new ImagingSatellite(name, batteryLevel, DEFAULT_RESOLUTION);
    }

    @Override
    public Satellite createSatelliteWithParameter(String name, double batteryLevel, double parameter) {
        return new ImagingSatellite(name, batteryLevel, parameter);
    }
}
