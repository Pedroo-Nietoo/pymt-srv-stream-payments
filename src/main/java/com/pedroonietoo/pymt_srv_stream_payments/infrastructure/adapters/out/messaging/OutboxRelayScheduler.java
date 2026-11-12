package com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.out.messaging;

import com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.out.persistence.outbox.OutboxEventJpaEntity;
import com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.out.persistence.outbox.OutboxStatus;
import com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.out.persistence.outbox.SpringDataOutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class OutboxRelayScheduler {

    private static final Logger log = LoggerFactory.getLogger(OutboxRelayScheduler.class);

    private final SpringDataOutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final String topicPrefix;
    private final int batchSize;
    private final int maxAttempts;
    private final long kafkaTimeoutSeconds;

    public OutboxRelayScheduler(
            SpringDataOutboxRepository outboxRepository,
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${app.kafka.topic-prefix:pymt-srv}") String topicPrefix,
            @Value("${app.outbox.batch-size:50}") int batchSize,
            @Value("${app.outbox.max-attempts:3}") int maxAttempts,
            @Value("${app.outbox.kafka-timeout-seconds:5}") long kafkaTimeoutSeconds
    ) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.topicPrefix = topicPrefix;
        this.batchSize = batchSize;
        this.maxAttempts = maxAttempts;
        this.kafkaTimeoutSeconds = kafkaTimeoutSeconds;
    }

    @Scheduled(fixedDelayString = "${app.outbox.polling-interval-ms:1000}")
    public void processPendingEvents() {
        List<OutboxEventJpaEntity> pendingEvents = outboxRepository
                .findByStatusAndNextAttemptAtLessThanEqualOrderByNextAttemptAtAsc(
                        OutboxStatus.PENDING,
                        Instant.now(),
                        PageRequest.of(0, batchSize)
                );

        if (pendingEvents.isEmpty()) {
            return;
        }

        log.debug("Found {} pending outbox event(s) to publish", pendingEvents.size());

        for (OutboxEventJpaEntity event : pendingEvents) {
            publishSingleEvent(event);
        }
    }

    private void publishSingleEvent(OutboxEventJpaEntity event) {
        String topic = buildTopicName(event.getAggregateType());

        try {
            kafkaTemplate.send(topic, event.getAggregateId(), event.getPayload())
                    .get(kafkaTimeoutSeconds, TimeUnit.SECONDS);

            event.markAsProcessed();
            outboxRepository.save(event);

            log.info("Outbox event published successfully: id={}, topic={}, key={}, type={}",
                    event.getId(), topic, event.getAggregateId(), event.getEventType());

        } catch (Exception ex) {
            event.registerFailedAttempt(maxAttempts);
            outboxRepository.save(event);

            log.error("Failed to publish outbox event: id={}, attempt={}/{}, status={}, error={}",
                    event.getId(), event.getAttempts(), maxAttempts, event.getStatus(), ex.getMessage());
        }
    }

    private String buildTopicName(String aggregateType) {
        return topicPrefix + "." + aggregateType.toLowerCase() + "-events";
    }
}