package org.example;

import java.util.ArrayList;
import java.util.List;

public class SatelliteConstellation {

    private String constellationName;
    private List<Satellite> satellites;

    public SatelliteConstellation(String constellationName) {
        this.constellationName = constellationName;
        this.satellites = new ArrayList<>();
    }

    public void addSatellite(Satellite satellite) {
        satellites.add(satellite);
        System.out.println(satellite.getName() + " добавлен в группировку '" + constellationName + "'");
    }

    public void executeAllMissions() {
        for (Satellite s : satellites) {
            s.performMission();
        }
    }

    public List<Satellite> getSatellites() {
        return satellites;
    }
}
