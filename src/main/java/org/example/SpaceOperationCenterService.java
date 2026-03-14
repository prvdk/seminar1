package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SpaceOperationCenterService {

    private final ConstellationService constellationService;
    private final SatelliteService satelliteService;

    @MeasureExecutionTime
    public SatelliteConstellation addSatellite(AddSatelliteRequest request) {
        Objects.requireNonNull(request, "Add satellite request must not be null");

        SatelliteConstellation constellation = constellationService.createAndSaveConstellation(request.getConstellationName());
        for (SatelliteParam satelliteParam : request.getSatelliteParams()) {
            Satellite satellite = satelliteService.createSatellite(satelliteParam);
            constellationService.addSatelliteToConstellation(constellation.getConstellationName(), satellite);
        }
        return constellationService.getConstellation(constellation.getConstellationName());
    }

    @MeasureExecutionTime
    public void executeMission(MissionRequest request) {
        Objects.requireNonNull(request, "Mission request must not be null");
        System.out.println("Запуск миссии: " + request.getMissionName());

        if (request.getSatelliteType() == null) {
            constellationService.executeConstellationMission(request.getConstellationName());
            return;
        }

        constellationService.executeConstellationMission(request.getConstellationName(), request.getSatelliteType());
    }

    public SatelliteConstellation showConstellationStatus(String constellationName) {
        return constellationService.showConstellationStatus(constellationName);
    }

    public void activateConstellation(String constellationName) {
        constellationService.activateAllSatellites(constellationName);
    }
}
