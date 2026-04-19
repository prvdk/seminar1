package org.example.missionscheduler.services;

import jakarta.annotation.PostConstruct;
import org.example.missionscheduler.clients.SpaceOperationClient;
import org.example.missionscheduler.domains.MissionRequest;
import org.example.missionscheduler.properties.ScheduledMissionProperties;
import org.example.missionscheduler.properties.SpaceCenterServiceProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConfiguredMissionScheduler {

    private static final Logger log = LoggerFactory.getLogger(ConfiguredMissionScheduler.class);

    private final TaskScheduler taskScheduler;
    private final SpaceOperationClient spaceOperationClient;
    private final SpaceCenterServiceProperties properties;

    public ConfiguredMissionScheduler(
            TaskScheduler taskScheduler,
            SpaceOperationClient spaceOperationClient,
            SpaceCenterServiceProperties properties
    ) {
        this.taskScheduler = taskScheduler;
        this.spaceOperationClient = spaceOperationClient;
        this.properties = properties;
    }

    @PostConstruct
    public void scheduleConfiguredMissions() {
        List<ScheduledMissionProperties> missions = properties.missions() == null ? List.of() : properties.missions();
        for (ScheduledMissionProperties mission : missions) {
            taskScheduler.schedule(
                    () -> executeMission(mission),
                    new CronTrigger(mission.cron())
            );
            log.info(
                    "Mission scheduled: targetType={}, constellationName={}, satelliteName={}, cron={}",
                    mission.targetType(),
                    mission.constellationName(),
                    mission.satelliteName(),
                    mission.cron()
            );
        }
    }

    private void executeMission(ScheduledMissionProperties mission) {
        MissionRequest request = new MissionRequest(
                mission.targetType(),
                mission.constellationName(),
                mission.satelliteName()
        );

        log.info(
                "Mission execution started: targetType={}, constellationName={}, satelliteName={}",
                mission.targetType(),
                mission.constellationName(),
                mission.satelliteName()
        );

        try {
            spaceOperationClient.executeMission(request);
            log.info(
                    "Mission executed successfully: targetType={}, constellationName={}, satelliteName={}",
                    mission.targetType(),
                    mission.constellationName(),
                    mission.satelliteName()
            );
        } catch (Exception exception) {
            log.error(
                    "Mission execution failed: targetType={}, constellationName={}, satelliteName={}",
                    mission.targetType(),
                    mission.constellationName(),
                    mission.satelliteName(),
                    exception
            );
        }
    }
}
