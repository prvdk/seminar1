package org.example;

public abstract class Satellite {

    protected String name;
    protected boolean isActive;
    protected double batteryLevel; // 0.0..1.0

    public Satellite(String name, double batteryLevel) {
        this.name = name;
        this.batteryLevel = clamp01(batteryLevel);
        this.isActive = false;
    }

    public boolean activate() {
        // включается, если заряд > 0.2
        if (batteryLevel > 0.2) {
            isActive = true;
            return true;
        }
        return false;
    }

    public void deactivate() {
        // выключаем только если был включен
        if (isActive) {
            isActive = false;
        }
    }

    public void consumeBattery(double amount) {
        if (amount <= 0) {
            return;
        }

        batteryLevel = clamp01(batteryLevel - amount);

        if (batteryLevel <= 0.2) {
            deactivate();
        }
    }

    protected abstract void performMission();

    private double clamp01(double v) {
        if (v < 0.0) return 0.0;
        if (v > 1.0) return 1.0;
        return v;
    }
}
