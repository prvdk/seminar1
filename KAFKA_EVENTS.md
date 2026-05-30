# Kafka events

## Топик

`satellite-events`

## Формат сообщения

Сообщения передаются как JSON:

```json
{
  "satelliteId": 1,
  "satelliteName": "Sat-1",
  "eventType": "SATELLITE_CREATED",
  "occurredAt": "2026-05-30T12:00:00Z"
}
```

Допустимые значения `eventType`:

- `SATELLITE_CREATED`
- `SATELLITE_DELETED`

Ключ сообщения - идентификатор спутника, приведенный к строке.
