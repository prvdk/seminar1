package org.example;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "imaging_satellites")
@PrimaryKeyJoinColumn(name = "satellite_id")
@DiscriminatorValue("IMAGE")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class ImagingSatellite extends Satellite {

    @Column(name = "resolution", nullable = false)
    private double resolution;

    @Column(name = "photos_taken", nullable = false)
    private int photosTaken;

    public ImagingSatellite(String name, double batteryLevel, double resolution) {
        this(name, EnergySystem.builder().batteryLevel(batteryLevel).build(), resolution);
    }

    public ImagingSatellite(String name, EnergySystem energySystem, double resolution) {
        super(name, energySystem);
        this.resolution = resolution;
        this.photosTaken = 0;
    }

    private void takePhoto() {
        if (!state.isActive()) {
            return;
        }
        photosTaken++;
        System.out.println(name + ": Снимок #" + photosTaken + " сделан!");
    }

    @Override
    public void performMission() {
        if (!state.isActive()) {
            System.out.println("🛑 " + name + ": Не может выполнить съемку - не активен");
            return;
        }

        System.out.println(name + ": Съемка территории с разрешением " + resolution + " м/пиксель");
        takePhoto();
        energy.consume(0.08);
        updateStateAfterEnergyConsumption();
    }

}
