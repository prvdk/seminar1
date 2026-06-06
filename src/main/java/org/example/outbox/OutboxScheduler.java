package org.example.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.kafka.SatelliteEvent;
import org.example.kafka.SatelliteEventPublisher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(value = "app.outbox.enabled", havingValue = "true", matchIfMissing = true)
public class OutboxScheduler {

    private final OutboxMessageRepository outboxMessageRepository;
    private final SatelliteEventPublisher satelliteEventPublisher;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelayString = "${app.outbox.polling-delay-ms:5000}")
    @Transactional
    public void publishPendingMessages() {
        outboxMessageRepository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING)
                .forEach(this::publish);
    }

    private void publish(OutboxMessage message) {
        try {
            SatelliteEvent event = objectMapper.readValue(message.getPayload(), SatelliteEvent.class);
            satelliteEventPublisher.publish(event);
            message.markSent();
        } catch (JsonProcessingException exception) {
            log.error("Failed to deserialize outbox message {}", message.getId(), exception);
        } catch (RuntimeException exception) {
            log.warn("Failed to publish outbox message {}, it will be retried", message.getId(), exception);
        }
    }
}
