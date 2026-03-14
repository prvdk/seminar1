package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SatelliteServiceImpl implements SatelliteService {

    private final List<SatelliteFactory> factories;

    @Override
    public Satellite createSatellite(SatelliteParam param) {
        Objects.requireNonNull(param, "Satellite param must not be null");
        SatelliteFactory suitableFactory = factories.stream()
                .filter(factory -> factory.isSatelliteTypeSupported(param.getType()))
                .findFirst()
                .orElseThrow(() -> new SpaceOperationException("No factory found for satellite type: " + param.getType()));

        return suitableFactory.createSatelliteWithParameter(param);
    }
}
