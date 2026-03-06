package org.example;

import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CommunicationSatelliteFactory implements SatelliteFactory {

    @Override
    public Satellite createSatelliteWithParameter(SatelliteParam param) {
        Objects.requireNonNull(param, "Satellite param must not be null");
        if (!(param instanceof CommunicationSatelliteParam communicationParam)) {
            throw new SpaceOperationException("Communication factory does not support parameter type: " + param.getClass().getSimpleName());
        }
        return new CommunicationSatellite(
                communicationParam.getName(),
                communicationParam.getBatteryLevel(),
                communicationParam.getBandwidth()
        );
    }

    @Override
    public boolean isSatelliteTypeSupported(SatelliteType type) {
        return SatelliteType.COMMUNICATION == type;
    }
}
