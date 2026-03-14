package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

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

        operationCenterService.addSatellite(new AddSatelliteRequest(
                "Орбита-1",
                List.of(
                        new CommunicationSatelliteParam("Связь-1", 0.85, 500.0),
                        new ImagingSatelliteParam("ДЗЗ-1", 0.92, 2.5),
                        new ImagingSatelliteParam("ДЗЗ-2", 0.45, 1.0)
                )
        ));
        operationCenterService.addSatellite(new AddSatelliteRequest(
                "Орбита-2",
                List.of(
                        new CommunicationSatelliteParam("Связь-2", 0.75, 1000.0),
                        new ImagingSatelliteParam("ДЗЗ-3", 0.15, 0.5)
                )
        ));
        System.out.println("---------------------------------------------");

        System.out.println("\n📡 УПРАВЛЕНИЕ СПУТНИКОВЫМИ ГРУППИРОВКАМИ:");
        System.out.println("-----------------------------------");

        operationCenterService.showConstellationStatus("Орбита-1");
        operationCenterService.showConstellationStatus("Орбита-2");

        operationCenterService.activateConstellation("Орбита-1");
        operationCenterService.activateConstellation("Орбита-2");
        operationCenterService.executeMission(
                new MissionRequest("Съемка Земли", "Орбита-1", SatelliteType.IMAGE)
        );
        operationCenterService.showConstellationStatus("Орбита-1");

        System.out.println("\n=== ДАННЫЕ РЕПОЗИТОРИЯ (через repository bean) ===");
        System.out.println(constellationRepository.getAllConstellations());

        context.close();
    }
}
