package org.example;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        System.out.println("ЗАПУСК СИСТЕМЫ УПРАВЛЕНИЯ СПУТНИКОВОЙ ГРУППИРОВКОЙ");
        System.out.println("============================================================");
        System.out.println("СОЗДАНИЕ СПЕЦИАЛИЗИРОВАННЫХ СПУТНИКОВ:");
        System.out.println("---------------------------------------------");

        CommunicationSatellite c1 = new CommunicationSatellite("Связь-1", 0.85, 500.0);
        CommunicationSatellite c2 = new CommunicationSatellite("Связь-2", 0.75, 1000.0);
        ImagingSatellite i1 = new ImagingSatellite("ДЗЗ-1", 0.92, 2.5);
        ImagingSatellite i2 = new ImagingSatellite("ДЗЗ-2", 0.45, 1.0);
        ImagingSatellite i3 = new ImagingSatellite("ДЗЗ-3", 0.15, 0.5);

        System.out.println("Создан спутник: " + c1.getName() + " (заряд: 85%)");
        System.out.println("Создан спутник: " + c2.getName() + " (заряд: 75%)");
        System.out.println("Создан спутник: " + i1.getName() + " (заряд: 92%)");
        System.out.println("Создан спутник: " + i2.getName() + " (заряд: 45%)");
        System.out.println("Создан спутник: " + i3.getName() + " (заряд: 15%)");

        System.out.println("---------------------------------------------");

        SatelliteConstellation constellation = new SatelliteConstellation("RU Basic");
        System.out.println("Создана спутниковая группировка: RU Basic");
        System.out.println("---------------------------------------------");

        System.out.println("ФОРМИРОВАНИЕ ГРУППИРОВКИ:");
        System.out.println("-----------------------------------");

        constellation.addSatellite(c1);
        constellation.addSatellite(c2);
        constellation.addSatellite(i1);
        constellation.addSatellite(i2);
        constellation.addSatellite(i3);

        System.out.println("-----------------------------------");
        System.out.println(constellation.getSatellites());
        System.out.println("-----------------------------------");

        System.out.println("АКТИВАЦИЯ СПУТНИКОВ:");
        System.out.println("-------------------------");

        activateAndPrint(c1);
        activateAndPrint(c2);
        activateAndPrint(i1);
        activateAndPrint(i2);
        activateAndPrint(i3);

        System.out.println("ВЫПОЛНЕНИЕ МИССИЙ ГРУППИРОВКИ RU BASIC");
        System.out.println("==================================================");

        constellation.executeAllMissions();

        List<Satellite> updated = constellation.getSatellites();
        System.out.println(updated);
    }

    private static void activateAndPrint(Satellite s) {
        boolean ok = s.activate();
        if (ok) {
            System.out.println("✅ " + s.getName() + ": Активация успешна");
        } else {
            int percent = (int) Math.round(s.getBatteryLevel() * 100);
            System.out.println("🛑 " + s.getName() + ": Ошибка активации (заряд: " + percent + "%)");
        }
    }
}
