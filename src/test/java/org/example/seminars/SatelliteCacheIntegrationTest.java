package org.example.seminars;

import org.example.CommunicationSatellite;
import org.example.Satellite;
import org.example.SatelliteCommandService;
import org.example.SatelliteCrudService;
import org.example.SatelliteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
@DisplayName("Integration tests for satellite cache")
class SatelliteCacheIntegrationTest {

    @Autowired
    private SatelliteCrudService satelliteCrudService;

    @Autowired
    private SatelliteCommandService satelliteCommandService;

    @SpyBean
    private SatelliteRepository satelliteRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void cleanRepositoryAndCache() {
        satelliteRepository.deleteAll();
        cacheManager.getCache("satellites").clear();
    }

    @Test
    @DisplayName("getAllSatellites should use cache and create should evict it")
    void getAllSatellitesShouldUseCacheAndCreateShouldEvictIt() {
        clearInvocations(satelliteRepository);

        List<Satellite> firstRead = satelliteCrudService.getAllSatellites();
        List<Satellite> secondRead = satelliteCrudService.getAllSatellites();

        assertEquals(firstRead, secondRead);
        verify(satelliteRepository, times(1)).findAll();

        satelliteCommandService.create(new CommunicationSatellite("Cache-Comm-1", 0.9, 512.0));

        List<Satellite> afterCreate = satelliteCrudService.getAllSatellites();

        assertEquals(1, afterCreate.size());
        verify(satelliteRepository, times(2)).findAll();
    }
}
