package org.example.missionscheduler.clients;

import org.example.missionscheduler.domains.MissionRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class SpaceOperationClient {

    private final RestClient spaceOperationRestClient;

    public SpaceOperationClient(RestClient spaceOperationRestClient) {
        this.spaceOperationRestClient = spaceOperationRestClient;
    }

    public void executeMission(MissionRequest request) {
        spaceOperationRestClient.post()
                .uri("/missions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
