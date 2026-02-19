package org.example;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
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
