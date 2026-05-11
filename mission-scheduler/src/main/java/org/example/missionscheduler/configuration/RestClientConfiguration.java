package org.example.missionscheduler.configuration;

import org.example.missionscheduler.properties.SpaceCenterServiceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfiguration {

    @Bean
    public RestClient spaceOperationRestClient(SpaceCenterServiceProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.url())
                .build();
    }
}
