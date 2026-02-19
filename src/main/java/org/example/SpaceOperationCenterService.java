package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SpaceOperationCenterService {

    private final ConstellationRepository constellationRepository;

    public SatelliteConstellation createAndSaveConstellation(String name) {
        if (constellationRepository.existsByName(name)) {
            System.out.println("Группировка уже существует: " + name);
            return getConstellationOrThrow(name);
        }

        SatelliteConstellation constellation = new SatelliteConstellation(name);
        System.out.println("Создана спутниковая группировка: " + name);
        return constellationRepository.save(constellation);
    }

    public void addSatelliteToConstellation(String constellationName, Satellite satellite) {
        SatelliteConstellation constellation = getConstellationOrThrow(constellationName);
        constellation.addSatellite(satellite);
        System.out.println("Добавлен спутник " + satellite.getName() + " в группировку " + constellationName);
    }

    public void executeConstellationMission(String constellationName) {
        SatelliteConstellation constellation = getConstellationOrThrow(constellationName);
        System.out.println("\n=== ВЫПОЛНЕНИЕ МИССИЙ ДЛЯ ГРУППИРОВКИ: " + constellationName + " ===");
        System.out.println("ВЫПОЛНЕНИЕ МИССИЙ ГРУППИРОВКИ " + constellationName.toUpperCase());
        System.out.println("==================================================");
        constellation.executeAllMissions();
    }

    public void activateAllSatellites(String constellationName) {
        SatelliteConstellation constellation = getConstellationOrThrow(constellationName);
        System.out.println("\n=== АКТИВАЦИЯ СПУТНИКОВ В ГРУППИРОВКЕ: " + constellationName + " ===");

        for (Satellite satellite : constellation.getSatellites()) {
            boolean activated = satellite.activate();
            if (activated) {
                System.out.println("✅ " + satellite.getName() + ": Активация успешна");
            } else {
                int percent = (int) Math.round(satellite.getBatteryLevel() * 100);
                System.out.println("🛑 " + satellite.getName() + ": Ошибка активации (заряд: " + percent + "%)");
            }
        }
    }

    public void showConstellationStatus(String constellationName) {
        SatelliteConstellation constellation = getConstellationOrThrow(constellationName);
        System.out.println("\n=== СТАТУС ГРУППИРОВКИ: " + constellationName + " ===");
        System.out.println("Количество спутников: " + constellation.getSatellites().size());
        for (Satellite satellite : constellation.getSatellites()) {
            System.out.println(satellite.getState());
        }
    }

    public Map<String, SatelliteConstellation> getAllConstellations() {
        return constellationRepository.getAllConstellations();
    }

    private SatelliteConstellation getConstellationOrThrow(String name) {
        return constellationRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Группировка не найдена: " + name));
    }
}
