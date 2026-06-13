package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConstellationService {

    private final ConstellationRepository constellationRepository;

    @Transactional
    public SatelliteConstellation createAndSaveConstellation(String name) {
        if (constellationRepository.existsByConstellationName(name)) {
            System.out.println("Группировка уже существует: " + name);
            return getConstellationOrThrow(name);
        }

        SatelliteConstellation constellation = new SatelliteConstellation(name);
        System.out.println("Создана спутниковая группировка: " + name);
        return constellationRepository.save(constellation);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "constellation", key = "#constellationName"),
            @CacheEvict(value = "satellites", allEntries = true)
    })
    public void addSatelliteToConstellation(String constellationName, Satellite satellite) {
        SatelliteConstellation constellation = getConstellationOrThrow(constellationName);
        constellation.addSatellite(Objects.requireNonNull(satellite, "Satellite must not be null"));
        constellationRepository.save(constellation);
        System.out.println("Добавлен спутник " + satellite.getName() + " в группировку " + constellationName);
    }

    @Transactional
    public void executeConstellationMission(String constellationName) {
        SatelliteConstellation constellation = getConstellationOrThrow(constellationName);
        printMissionHeader(constellationName);
        constellation.executeAllMissions();
        constellationRepository.save(constellation);
    }

    @Transactional
    public void executeSingleSatelliteMission(String constellationName, String satelliteName) {
        SatelliteConstellation constellation = getConstellationOrThrow(constellationName);
        printMissionHeader(constellationName);

        Satellite satellite = constellation.getSatellites().stream()
                .filter(existingSatellite -> existingSatellite.getName().equals(satelliteName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Спутник не найден: " + satelliteName));
        satellite.performMission();
        constellationRepository.save(constellation);
    }

    @Transactional
    public void activateAllSatellites(String constellationName) {
        SatelliteConstellation constellation = getConstellationOrThrow(constellationName);
        printActivationHeader(constellationName);

        for (Satellite satellite : constellation.getSatellites()) {
            printActivationResult(satellite);
        }
        constellationRepository.save(constellation);
    }

    @Cacheable(value = "constellation", key = "#constellationName")
    public SatelliteConstellation showConstellationStatus(String constellationName) {
        SatelliteConstellation constellation = getConstellationOrThrow(constellationName);
        System.out.println("\n=== СТАТУС ГРУППИРОВКИ: " + constellationName + " ===");
        System.out.println("Количество спутников: " + constellation.getSatellites().size());
        for (Satellite satellite : constellation.getSatellites()) {
            System.out.println(satellite.getState());
        }
        return constellation;
    }

    public SatelliteConstellation getConstellation(String name) {
        return getConstellationOrThrow(name);
    }

    @Cacheable(value = "constellation", key = "#name")
    public SatelliteConstellation getConstellationByName(String name) {
        return getConstellationOrThrow(name);
    }

    public Map<String, SatelliteConstellation> getAllConstellations() {
        return constellationRepository.findAll().stream()
                .collect(LinkedHashMap::new,
                        (map, constellation) -> map.put(constellation.getConstellationName(), constellation),
                        LinkedHashMap::putAll);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "constellation", key = "#constellationName"),
            @CacheEvict(value = "satellites", allEntries = true)
    })
    public void decommissionSatellite(String constellationName, String satelliteName) {
        SatelliteConstellation constellation = getConstellationOrThrow(constellationName);
        boolean removed = constellation.removeSatellite(satelliteName);
        if (!removed) {
            throw new IllegalArgumentException("Спутник не найден: " + satelliteName);
        }
        constellationRepository.save(constellation);
        System.out.println("Спутник " + satelliteName + " выведен из эксплуатации");
    }

    private SatelliteConstellation getConstellationOrThrow(String name) {
        return constellationRepository.findByConstellationName(name)
                .orElseThrow(() -> new IllegalArgumentException("Группировка не найдена: " + name));
    }

    private void printMissionHeader(String constellationName) {
        System.out.println("\n=== ВЫПОЛНЕНИЕ МИССИЙ ДЛЯ ГРУППИРОВКИ: " + constellationName + " ===");
        System.out.println("ВЫПОЛНЕНИЕ МИССИЙ ГРУППИРОВКИ " + constellationName.toUpperCase());
        System.out.println("==================================================");
    }

    private void printActivationHeader(String constellationName) {
        System.out.println("\n=== АКТИВАЦИЯ СПУТНИКОВ В ГРУППИРОВКЕ: " + constellationName + " ===");
    }

    private void printActivationResult(Satellite satellite) {
        boolean activated = satellite.activate();
        if (activated) {
            System.out.println("✅ " + satellite.getName() + ": Активация успешна");
            return;
        }

        int percent = (int) Math.round(satellite.getBatteryLevel() * 100);
        System.out.println("🛑 " + satellite.getName() + ": Ошибка активации (заряд: " + percent + "%)");
    }

}
