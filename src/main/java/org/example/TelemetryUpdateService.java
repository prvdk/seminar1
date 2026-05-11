package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TelemetryUpdateService {

    private final SatelliteRepository satelliteRepository;

    @Transactional
    public void updateTemperatures(String satelliteName, double insideTemperature, double outsideTemperature) {
        satelliteRepository.findByName(satelliteName).ifPresent(satellite -> {
            satellite.setInsideTemperature(insideTemperature);
            satellite.setOutsideTemperature(outsideTemperature);
        });
    }
}
