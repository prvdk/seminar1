# Семинар 2

Что было сделано:

- Созданы отдельные классы `EnergySystem` и `SatelliteState` для управления зарядом и состоянием спутника.
- Абстрактный класс `Satellite` переведен на композицию: `state` и `energy` вместо прямых полей `isActive` и `batteryLevel`.
- Логика работы с энергией и состоянием делегирована новым классам.
- Обновлены `CommunicationSatellite` и `ImagingSatellite`: обращения идут через `state.isActive()` и `energy.getBatteryLevel()`, расход заряда через `energy.consume()`.
- Обновлены `Main` и `SatelliteConstellation` под новую структуру.
- Полиморфное выполнение миссий сохранено, проект успешно собирается.
