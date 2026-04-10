package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SpaceOperationCenterService {

    private final ConstellationService constellationService;
    private final SatelliteService satelliteService;

    @MeasureExecutionTime
    public SatelliteConstellation addSatellite(AddSatelliteRequest request) {
        Objects.requireNonNull(request, "Add satellite request must not be null");

        SatelliteConstellation constellation = constellationService.createAndSaveConstellation(request.constellationName());
        for (SatelliteParam satelliteParam : request.satelliteParams()) {
            Satellite satellite = satelliteService.createSatellite(satelliteParam);
            constellationService.addSatelliteToConstellation(constellation.getConstellationName(), satellite);
        }
        return constellationService.getConstellation(constellation.getConstellationName());
    }

    @MeasureExecutionTime
    public void executeMission(MissionRequest request) {
        Objects.requireNonNull(request, "Mission request must not be null");
        if (request.targetType() == MissionTargetType.SINGLE_SATELLITE) {
            constellationService.executeSingleSatelliteMission(request.constellationName(), request.satelliteName());
        } else {
            constellationService.executeConstellationMission(request.constellationName());
        }
    }

    public SatelliteConstellation showConstellationStatus(String constellationName) {
        return constellationService.showConstellationStatus(constellationName);
    }

    public void activateConstellation(String constellationName) {
        constellationService.activateAllSatellites(constellationName);
    }

    public String getOverview() {
        StringBuilder builder = new StringBuilder();
        Map<String, SatelliteConstellation> constellations = constellationService.getAllConstellations();
        if (constellations.isEmpty()) {
            return "Сводка: группировки отсутствуют";
        }

        builder.append("Сводка состояния спутниковых группировок:\n");
        constellations.values().forEach(constellation -> builder
                .append(constellation.getConstellationName())
                .append(" - спутников: ")
                .append(constellation.getSatellites().size())
                .append('\n'));

        return builder.toString().trim();
    }

    public void decommissionSatellite(String constellationName, String satelliteName) {
        constellationService.decommissionSatellite(constellationName, satelliteName);
    }
}
