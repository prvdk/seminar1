package org.example;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "energy_systems")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class EnergySystem {

    private static final double DEFAULT_BATTERY_LEVEL = 1.0;
    private static final double DEFAULT_MIN_BATTERY = 0.0;
    private static final double DEFAULT_MAX_BATTERY = 1.0;
    private static final double DEFAULT_LOW_BATTERY_THRESHOLD = 0.2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "low_battery_threshold", nullable = false)
    private double lowBatteryThreshold;

    @Column(name = "max_battery", nullable = false)
    private double maxBattery;

    @Column(name = "min_battery", nullable = false)
    private double minBattery;

    @Column(name = "battery_level", nullable = false)
    private double batteryLevel;

    private EnergySystem(Builder builder) {
        this.lowBatteryThreshold = builder.lowBatteryThreshold;
        this.maxBattery = builder.maxBattery;
        this.minBattery = builder.minBattery;
        this.batteryLevel = builder.batteryLevel;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean consume(double amount) {
        if (amount <= 0.0) {
            return false;
        }
        batteryLevel = clampToRange(batteryLevel - amount);
        return true;
    }

    public boolean recharge(double amount) {
        if (amount <= 0.0) {
            return false;
        }
        batteryLevel = clampToRange(batteryLevel + amount);
        return true;
    }

    public boolean hasSufficientPower() {
        return batteryLevel > lowBatteryThreshold;
    }

    public boolean isCritical() {
        return batteryLevel <= lowBatteryThreshold;
    }

    private double clampToRange(double value) {
        if (value < minBattery) {
            return minBattery;
        }
        if (value > maxBattery) {
            return maxBattery;
        }
        return value;
    }

    public static class Builder {

        private double batteryLevel = DEFAULT_BATTERY_LEVEL;
        private double lowBatteryThreshold = DEFAULT_LOW_BATTERY_THRESHOLD;
        private double maxBattery = DEFAULT_MAX_BATTERY;
        private double minBattery = DEFAULT_MIN_BATTERY;

        public Builder batteryLevel(double batteryLevel) {
            this.batteryLevel = batteryLevel;
            return this;
        }

        public Builder lowBatteryThreshold(double lowBatteryThreshold) {
            this.lowBatteryThreshold = lowBatteryThreshold;
            return this;
        }

        public Builder maxBattery(double maxBattery) {
            this.maxBattery = maxBattery;
            return this;
        }

        public Builder minBattery(double minBattery) {
            this.minBattery = minBattery;
            return this;
        }

        public EnergySystem build() {
            validateRange();
            if (lowBatteryThreshold < minBattery || lowBatteryThreshold > maxBattery) {
                throw new IllegalArgumentException("lowBatteryThreshold must be in [minBattery, maxBattery]");
            }
            batteryLevel = clamp(batteryLevel, minBattery, maxBattery);
            return new EnergySystem(this);
        }

        private void validateRange() {
            if (minBattery > maxBattery) {
                throw new IllegalArgumentException("minBattery must be <= maxBattery");
            }
        }

        private double clamp(double value, double min, double max) {
            if (value < min) {
                return min;
            }
            if (value > max) {
                return max;
            }
            return value;
        }
    }

}
