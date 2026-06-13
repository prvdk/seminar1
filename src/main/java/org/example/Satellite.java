package org.example;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "satellites")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "satellite_type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "satelliteType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CommunicationSatellite.class, name = "COMMUNICATION"),
        @JsonSubTypes.Type(value = ImagingSatellite.class, name = "IMAGE")
})
@Getter
@Setter
@NoArgsConstructor
@ToString
public abstract class Satellite implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final double MIN_ACTIVATION_BATTERY = 0.2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "name", nullable = false)
    protected String name;

    @Column(name = "inside_temperature")
    protected Double insideTemperature;

    @Column(name = "outside_temperature")
    protected Double outsideTemperature;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "state_id", nullable = false)
    protected SatelliteState state;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "energy_system_id", nullable = false)
    protected EnergySystem energy;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "constellation_id")
    protected SatelliteConstellation constellation;

    public Satellite(String name, double batteryLevel) {
        this(name, EnergySystem.builder().batteryLevel(batteryLevel).build());
    }

    public Satellite(String name, EnergySystem energySystem) {
        this.name = Objects.requireNonNull(name, "Satellite name must not be null");
        this.state = new SatelliteState();
        this.energy = Objects.requireNonNull(energySystem, "Energy system must not be null");
    }

    public boolean activate() {
        if (energy.getBatteryLevel() > MIN_ACTIVATION_BATTERY) {
            state.activate();
            return true;
        }
        return false;
    }

    public void deactivate() {
        state.deactivate();
    }

    protected abstract void performMission();

    protected void updateStateAfterEnergyConsumption() {
        if (energy.getBatteryLevel() <= MIN_ACTIVATION_BATTERY) {
            state.deactivate();
        }
    }

    public boolean isActive() {
        return state.isActive();
    }

    public double getBatteryLevel() {
        return energy.getBatteryLevel();
    }
}
