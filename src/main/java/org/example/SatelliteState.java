package org.example;

public class SatelliteState {

    private boolean active;
    private String statusMessage;

    public SatelliteState() {
        this.active = false;
        this.statusMessage = "Не активирован";
    }

    public void activate() {
        active = true;
        statusMessage = "Активен";
    }

    public void deactivate() {
        active = false;
        statusMessage = "Не активирован";
    }

    public boolean isActive() {
        return active;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    @Override
    public String toString() {
        return "SatelliteState{" +
                "isActive=" + active +
                ", statusMessage='" + statusMessage + '\'' +
                '}';
    }
}
