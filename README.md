# Space Operation Center

Backend-сервис для управления спутниковыми группировками, спутниками, энергосистемами, состояниями спутников и запуском миссий.

## Стек

- Java 21
- Spring Boot
- Gradle
- PostgreSQL
- Kafka
- gRPC
- Docker Compose

## Локальный запуск

```bash
git clone https://github.com/prvdk/seminar1.git
cd seminar1
git checkout seminar-11
docker compose up --build
```

После запуска основной API доступен по адресу:

```text
http://localhost:8080
```

Быстрая проверка:

```bash
curl http://localhost:8080/api/overview
```

## Основные API

- `POST /api/add-satellites`
- `POST /api/missions`
- `GET /api/overview`
- `DELETE /api/constellations/{constellationName}/satellites/{satelliteName}`
- `GET /api/constellations/{constellationName}/status`
- `POST /api/constellations/{constellationName}/activate`
- CRUD API:
  - `/api/db/satellites`
  - `/api/db/constellations`
  - `/api/db/energy-systems`
  - `/api/db/satellite-states`

## Остановка

```bash
docker compose down
```

Для удаления данных PostgreSQL:

```bash
docker compose down -v
```
