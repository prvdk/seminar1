package org.example.missionscheduler.domains;

public record MissionRequest(
        MissionTargetType targetType,
        String constellationName,
        String satelliteName
) {
}
