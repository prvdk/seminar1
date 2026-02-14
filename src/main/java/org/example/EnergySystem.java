package org.example;

public class EnergySystem {

    private double batteryLevel;

    public EnergySystem(double batteryLevel) {
        this.batteryLevel = clamp01(batteryLevel);
    }

    public void consume(double amount) {
        if (amount <= 0.0) {
            return;
        }
        batteryLevel = clamp01(batteryLevel - amount);
    }

    public double getBatteryLevel() {
        return batteryLevel;
    }

    private double clamp01(double value) {
        if (value < 0.0) {
            return 0.0;
        }
        if (value > 1.0) {
            return 1.0;
        }
        return value;
    }
}
