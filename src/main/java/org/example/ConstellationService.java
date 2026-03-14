package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ConstellationService {

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
        constellation.addSatellite(Objects.requireNonNull(satellite, "Satellite must not be null"));
        System.out.println("Добавлен спутник " + satellite.getName() + " в группировку " + constellationName);
    }

    public void executeConstellationMission(String constellationName) {
        SatelliteConstellation constellation = getConstellationOrThrow(constellationName);
        printMissionHeader(constellationName);
        constellation.executeAllMissions();
    }

    public void executeConstellationMission(String constellationName, SatelliteType satelliteType) {
        SatelliteConstellation constellation = getConstellationOrThrow(constellationName);
        printMissionHeader(constellationName);

        List<Satellite> selectedSatellites = constellation.getSatellites().stream()
                .filter(satellite -> isSatelliteTypeMatches(satellite, satelliteType))
                .toList();

        for (Satellite satellite : selectedSatellites) {
            satellite.performMission();
        }
    }

    public void activateAllSatellites(String constellationName) {
        SatelliteConstellation constellation = getConstellationOrThrow(constellationName);
        printActivationHeader(constellationName);

        for (Satellite satellite : constellation.getSatellites()) {
            printActivationResult(satellite);
        }
    }

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

    public Map<String, SatelliteConstellation> getAllConstellations() {
        return constellationRepository.getAllConstellations();
    }

    private SatelliteConstellation getConstellationOrThrow(String name) {
        return constellationRepository.findByName(name)
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

    private boolean isSatelliteTypeMatches(Satellite satellite, SatelliteType satelliteType) {
        if (satelliteType == null) {
            return true;
        }
        return switch (satelliteType) {
            case IMAGE -> satellite instanceof ImagingSatellite;
            case COMMUNICATION -> satellite instanceof CommunicationSatellite;
        };
    }
}
