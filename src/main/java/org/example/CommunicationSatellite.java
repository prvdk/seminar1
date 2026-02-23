package org.example;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class CommunicationSatellite extends Satellite {

    private final double bandwidth;

    public CommunicationSatellite(String name, double batteryLevel, double bandwidth) {
        this(name, EnergySystem.builder().batteryLevel(batteryLevel).build(), bandwidth);
    }

    public CommunicationSatellite(String name, EnergySystem energySystem, double bandwidth) {
        super(name, energySystem);
        this.bandwidth = bandwidth;
    }

    private void sendData(double amount) {
        if (!state.isActive()) {
            return;
        }
        System.out.println(name + ": Отправил " + amount + " Мбит данных!");
    }

    @Override
    public void performMission() {
        if (!state.isActive()) {
            System.out.println("🛑 " + name + ": Не может выполнить передачу - не активен");
            return;
        }

        System.out.println(name + ": Передача данных со скоростью " + bandwidth + " Мбит/с");
        sendData(bandwidth);
        energy.consume(0.05);
        updateStateAfterEnergyConsumption();
    }

}
