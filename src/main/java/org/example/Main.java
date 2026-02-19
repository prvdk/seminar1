package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        System.out.println("ЗАПУСК СИСТЕМЫ УПРАВЛЕНИЯ СПУТНИКОВОЙ ГРУППИРОВКОЙ");
        System.out.println("============================================================");

        ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);

        ConstellationRepository constellationRepository = context.getBean(ConstellationRepository.class);
        SpaceOperationCenterService operationCenterService = context.getBean(SpaceOperationCenterService.class);

        System.out.println();
        System.out.println("СОЗДАНИЕ СПЕЦИАЛИЗИРОВАННЫХ СПУТНИКОВ:");
        System.out.println("---------------------------------------------");

        CommunicationSatellite c1 = new CommunicationSatellite("Связь-1", 0.85, 500.0);
        CommunicationSatellite c2 = new CommunicationSatellite("Связь-2", 0.75, 1000.0);
        ImagingSatellite i1 = new ImagingSatellite("ДЗЗ-1", 0.92, 2.5);
        ImagingSatellite i2 = new ImagingSatellite("ДЗЗ-2", 0.45, 1.0);
        ImagingSatellite i3 = new ImagingSatellite("ДЗЗ-3", 0.15, 0.5);

        System.out.println("Создан спутник: " + c1.getName() + " (" + c1.getBatteryLevel() + ")");
        System.out.println("Создан спутник: " + c2.getName() + " (" + c2.getBatteryLevel() + ")");
        System.out.println("Создан спутник: " + i1.getName() + " (" + i1.getBatteryLevel() + ")");
        System.out.println("Создан спутник: " + i2.getName() + " (" + i2.getBatteryLevel() + ")");
        System.out.println("Создан спутник: " + i3.getName() + " (" + i3.getBatteryLevel() + ")");

        System.out.println("---------------------------------------------");
        operationCenterService.createAndSaveConstellation("Орбита-1");
        operationCenterService.createAndSaveConstellation("Орбита-2");
        System.out.println("---------------------------------------------");

        System.out.println("\n📡 ДОБАВЛЕНИЕ СПУТНИКОВ:");
        System.out.println("-----------------------------------");

        operationCenterService.addSatelliteToConstellation("Орбита-1", c1);
        operationCenterService.addSatelliteToConstellation("Орбита-1", i1);
        operationCenterService.addSatelliteToConstellation("Орбита-1", i2);
        operationCenterService.addSatelliteToConstellation("Орбита-2", c2);
        operationCenterService.addSatelliteToConstellation("Орбита-2", i3);

        System.out.println("-----------------------------------");

        operationCenterService.showConstellationStatus("Орбита-1");
        operationCenterService.showConstellationStatus("Орбита-2");

        operationCenterService.activateAllSatellites("Орбита-1");
        operationCenterService.executeConstellationMission("Орбита-1");
        operationCenterService.showConstellationStatus("Орбита-1");

        System.out.println("\n=== ДАННЫЕ РЕПОЗИТОРИЯ (через repository bean) ===");
        System.out.println(constellationRepository.getAllConstellations());
        System.out.println("\n=== ДАННЫЕ ЧЕРЕЗ СЕРВИС (тот же repository bean) ===");
        System.out.println(operationCenterService.getAllConstellations());

        context.close();
    }
}
