package org.example;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "satellite_constellations")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class SatelliteConstellation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "constellation_name", nullable = false, unique = true)
    private String constellationName;

    @OneToMany(mappedBy = "constellation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Satellite> satellites = new ArrayList<>();

    public SatelliteConstellation(String constellationName) {
        this.constellationName = constellationName;
    }

    public void addSatellite(Satellite satellite) {
        satellite.setConstellation(this);
        satellites.add(satellite);
        System.out.println(satellite.getName() + " добавлен в группировку '" + constellationName + "'");
    }

    public void executeAllMissions() {
        for (Satellite s : satellites) {
            s.performMission();
        }
    }

    public boolean removeSatellite(String satelliteName) {
        return satellites.removeIf(satellite -> {
            if (satellite.getName().equals(satelliteName)) {
                satellite.setConstellation(null);
                return true;
            }
            return false;
        });
    }
}
