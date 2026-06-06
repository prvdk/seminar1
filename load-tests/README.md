# Нагрузочное тестирование

Публичный репозиторий: https://github.com/prvdk/seminar1/tree/load-testing

## Ключевой пользовательский сценарий

Оператор центра управления выполняет полный цикл работы со спутниковой группировкой:

1. Получает сводку по системе через `GET /api/overview`.
2. Создает группировку и добавляет в нее спутники связи и съемки через `POST /api/add-satellites`.
3. Проверяет состояние группировки через `GET /api/constellations/{constellationName}/status`.
4. Активирует спутники группировки через `POST /api/constellations/{constellationName}/activate`.
5. Запускает миссию для группировки через `POST /api/missions`.
6. Повторно получает сводку через `GET /api/overview`.
7. Выводит один спутник из эксплуатации через `DELETE /api/constellations/{constellationName}/satellites/{satelliteName}`.
8. Удаляет тестовую группировку через `DELETE /api/db/constellations/{id}`.

## Состав решения

- `space-operation-scenario.js` - сценарий нагрузочного тестирования k6.
- `reports/k6-report.html` - HTML-отчет последнего прогона.
- `../docker-compose.load-tests.yml` - отдельный Docker Compose-файл для запуска k6 без локальной установки.

## Профиль нагрузки

Сценарий имитирует одновременную работу пользователей, которые выполняют законченный бизнес-сценарий с чтением, созданием, изменением и удалением данных.

- Разгон до 20 виртуальных пользователей за 15 секунд.
- Удержание 20 виртуальных пользователей 60 секунд.
- Спад до 0 пользователей за 15 секунд.

Количество виртуальных пользователей можно изменить через переменную `VUS`.

## Запуск проекта

Из корня репозитория:

```bash
docker compose up --build -d
```

Проверка доступности API:

```bash
curl http://localhost:8080/api/overview
```

## Запуск нагрузочного теста

Из корня репозитория:

```bash
docker compose -f docker-compose.load-tests.yml run --rm k6
```

Запуск с другим количеством виртуальных пользователей:

```bash
VUS=50 docker compose -f docker-compose.load-tests.yml run --rm k6
```

После завершения теста HTML-отчет будет сохранен в `load-tests/reports/k6-report.html`.

## Остановка проекта

```bash
docker compose down
```

Для удаления данных PostgreSQL:

```bash
docker compose down -v
```
