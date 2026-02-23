package org.example;

import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
public abstract class Satellite {

    private static final double MIN_ACTIVATION_BATTERY = 0.2;

    protected final String name;
    protected final SatelliteState state;
    protected final EnergySystem energy;

    public Satellite(String name, double batteryLevel) {
        this(name, EnergySystem.builder().batteryLevel(batteryLevel).build());
    }

    public Satellite(String name, EnergySystem energySystem) {
        this.name = Objects.requireNonNull(name, "Satellite name must not be null");
        this.state = new SatelliteState();
        this.energy = Objects.requireNonNull(energySystem, "Energy system must not be null");
    }

    public boolean activate() {
        if (energy.getBatteryLevel() > MIN_ACTIVATION_BATTERY) {
            state.activate();
            return true;
        }
        return false;
    }

    public void deactivate() {
        state.deactivate();
    }

    protected abstract void performMission();

    protected void updateStateAfterEnergyConsumption() {
        if (energy.getBatteryLevel() <= MIN_ACTIVATION_BATTERY) {
            state.deactivate();
        }
    }

    public boolean isActive() {
        return state.isActive();
    }

    public double getBatteryLevel() {
        return energy.getBatteryLevel();
    }
}
