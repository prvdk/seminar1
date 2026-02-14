package org.example;

public class CommunicationSatellite extends Satellite {

    private double bandwidth;

    public CommunicationSatellite(String name, double batteryLevel, double bandwidth) {
        super(name, batteryLevel);
        this.bandwidth = bandwidth;
    }

    public double getBandwidth() {
        return bandwidth;
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

    @Override
    public String toString() {
        return "CommunicationSatellite{" +
                "bandwidth=" + bandwidth +
                ", name='" + name + '\'' +
                ", isActive=" + state.isActive() +
                ", batteryLevel=" + energy.getBatteryLevel() +
                '}';
    }
}
