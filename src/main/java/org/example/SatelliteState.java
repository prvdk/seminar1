package org.example;

public class SatelliteState {

    private boolean active;

    public SatelliteState() {
        this.active = false;
    }

    public void activate() {
        active = true;
    }

    public void deactivate() {
        active = false;
    }

    public boolean isActive() {
        return active;
    }
}
