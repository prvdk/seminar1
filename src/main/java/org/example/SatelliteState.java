package org.example;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "satellite_states")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class SatelliteState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean active = false;

    @Column(name = "status_message", nullable = false)
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
