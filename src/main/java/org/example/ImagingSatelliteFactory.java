package org.example;

import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ImagingSatelliteFactory implements SatelliteFactory {

    @Override
    public Satellite createSatelliteWithParameter(SatelliteParam param) {
        Objects.requireNonNull(param, "Satellite param must not be null");
        if (!(param instanceof ImagingSatelliteParam imagingParam)) {
            throw new SpaceOperationException("Imaging factory does not support parameter type: " + param.getClass().getSimpleName());
        }
        return new ImagingSatellite(
                imagingParam.getName(),
                imagingParam.getBatteryLevel(),
                imagingParam.getResolution()
        );
    }

    @Override
    public boolean isSatelliteTypeSupported(SatelliteType type) {
        return SatelliteType.IMAGE == type;
    }
}
