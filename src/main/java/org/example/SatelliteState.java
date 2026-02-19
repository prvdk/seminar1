package org.example;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SatelliteState {

    private boolean active = false;
    private String statusMessage = "Не активирован";

    public void activate() {
        active = true;
        statusMessage = "Активен";
    }

    public void deactivate() {
        active = false;
        statusMessage = "Не активирован";
    }
}
