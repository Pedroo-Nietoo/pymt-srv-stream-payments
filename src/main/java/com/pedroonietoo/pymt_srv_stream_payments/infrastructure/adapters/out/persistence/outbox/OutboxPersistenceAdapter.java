package com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.out.persistence.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedroonietoo.pymt_srv_stream_payments.application.ports.out.OutboxEventPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OutboxPersistenceAdapter implements OutboxEventPort {

    private final SpringDataOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public OutboxPersistenceAdapter(
            SpringDataOutboxRepository outboxRepository,
            ObjectMapper objectMapper
    ) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void saveEvent(String aggregateType, String aggregateId, String eventType, Object eventPayload) {
        String jsonPayload = serializePayload(eventPayload);

        var entity = new OutboxEventJpaEntity(
                UUID.randomUUID(),
                aggregateType,
                aggregateId,
                eventType,
                jsonPayload
        );

        outboxRepository.save(entity);
    }

    private String serializePayload(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize outbox event payload", e);
        }
    }
}