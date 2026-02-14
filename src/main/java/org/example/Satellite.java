package org.example;

public abstract class Satellite {

    private static final double MIN_ACTIVATION_BATTERY = 0.2;

    protected String name;
    protected SatelliteState state;
    protected EnergySystem energy;

    public Satellite(String name, double batteryLevel) {
        this.name = name;
        this.state = new SatelliteState();
        this.energy = new EnergySystem(batteryLevel);
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

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return state.isActive();
    }

    public double getBatteryLevel() {
        return energy.getBatteryLevel();
    }

    public SatelliteState getState() {
        return state;
    }
}
