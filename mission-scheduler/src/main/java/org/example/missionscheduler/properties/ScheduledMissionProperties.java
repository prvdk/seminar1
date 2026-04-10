package org.example.missionscheduler.properties;

import org.example.missionscheduler.domains.MissionTargetType;

public record ScheduledMissionProperties(
        MissionTargetType targetType,
        String constellationName,
        String satelliteName,
        String cron
) {
}
