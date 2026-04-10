package org.example;

public record MissionRequest(
        MissionTargetType targetType,
        String constellationName,
        String satelliteName
) {
}
