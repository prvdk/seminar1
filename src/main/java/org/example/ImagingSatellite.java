package org.example;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class ImagingSatellite extends Satellite {

    private final double resolution;
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
