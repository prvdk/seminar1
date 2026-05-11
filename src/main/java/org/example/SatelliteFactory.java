package org.example;

public interface SatelliteFactory {

    Satellite createSatelliteWithParameter(SatelliteParam param);

    boolean isSatelliteTypeSupported(SatelliteType type);
}
