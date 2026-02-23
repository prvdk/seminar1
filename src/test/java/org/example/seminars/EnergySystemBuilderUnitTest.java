package org.example.seminars;

import org.example.EnergySystem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Unit tests for EnergySystem builder")
class EnergySystemBuilderUnitTest {

    private static final double DELTA = 1e-9;

    @Test
    @DisplayName("builder should apply default values when configuration is omitted")
    void builder_shouldApplyDefaultValuesWhenConfigurationIsOmitted() {
        EnergySystem energySystem = EnergySystem.builder().build();

        assertEquals(1.0, energySystem.getBatteryLevel(), DELTA);
        assertEquals(0.2, energySystem.getLowBatteryThreshold(), DELTA);
        assertEquals(0.0, energySystem.getMinBattery(), DELTA);
        assertEquals(1.0, energySystem.getMaxBattery(), DELTA);
    }

    @Test
    @DisplayName("builder should clamp battery level into configured range")
    void builder_shouldClampBatteryLevelIntoConfiguredRange() {
        EnergySystem energySystem = EnergySystem.builder()
                .minBattery(0.1)
                .maxBattery(0.9)
                .batteryLevel(1.5)
                .build();

        assertEquals(0.9, energySystem.getBatteryLevel(), DELTA);
    }

    @Test
    @DisplayName("builder should throw exception when min battery exceeds max battery")
    void builder_shouldThrowExceptionWhenMinBatteryExceedsMaxBattery() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> EnergySystem.builder()
                        .minBattery(0.8)
                        .maxBattery(0.6)
                        .build()
        );

        assertTrue(exception.getMessage().contains("minBattery"));
    }

    @Test
    @DisplayName("builder should throw exception when threshold is out of configured range")
    void builder_shouldThrowExceptionWhenThresholdIsOutOfConfiguredRange() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> EnergySystem.builder()
                        .minBattery(0.1)
                        .maxBattery(0.9)
                        .lowBatteryThreshold(1.1)
                        .build()
        );

        assertTrue(exception.getMessage().contains("lowBatteryThreshold"));
    }

    @Test
    @DisplayName("consume and recharge should update battery and criticality flags")
    void consumeAndRecharge_shouldUpdateBatteryAndCriticalityFlags() {
        EnergySystem energySystem = EnergySystem.builder()
                .batteryLevel(0.5)
                .lowBatteryThreshold(0.2)
                .build();

        assertTrue(energySystem.consume(0.35));
        assertTrue(energySystem.isCritical());
        assertFalse(energySystem.hasSufficientPower());
        assertTrue(energySystem.recharge(0.2));
        assertTrue(energySystem.hasSufficientPower());
        assertFalse(energySystem.isCritical());
    }
}
