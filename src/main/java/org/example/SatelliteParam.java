package org.example;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;

import java.util.Objects;

@Getter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CommunicationSatelliteParam.class, name = "COMMUNICATION"),
        @JsonSubTypes.Type(value = ImagingSatelliteParam.class, name = "IMAGE")
})
public abstract class SatelliteParam {

    private final SatelliteType type;
    private final String name;
    private final double batteryLevel;

    protected SatelliteParam(SatelliteType type, String name, double batteryLevel) {
        this.type = Objects.requireNonNull(type, "Satellite type must not be null");
        this.name = Objects.requireNonNull(name, "Satellite name must not be null");
        this.batteryLevel = batteryLevel;
    }
}
