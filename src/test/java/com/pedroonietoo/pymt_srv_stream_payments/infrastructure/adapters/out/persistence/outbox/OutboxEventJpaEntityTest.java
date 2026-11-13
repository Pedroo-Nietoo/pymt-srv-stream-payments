package com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.out.persistence.outbox;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class OutboxEventJpaEntityTest {

    @Test
    void shouldCreateEventAndAccessProperties() {
        UUID id = UUID.randomUUID();
        OutboxEventJpaEntity event = new OutboxEventJpaEntity(id, "Payment", "id-123", "TYPE", "{}");

        assertEquals(id, event.getId());
        assertEquals("Payment", event.getAggregateType());
        assertEquals("id-123", event.getAggregateId());
        assertEquals("TYPE", event.getEventType());
        assertEquals("{}", event.getPayload());
        assertEquals(OutboxStatus.PENDING, event.getStatus());
        assertEquals(0, event.getAttempts());
        assertNotNull(event.getCreatedAt());
        assertNotNull(event.getNextAttemptAt());
        assertNull(event.getProcessedAt());
    }

    @Test
    void shouldMarkAsProcessed() {
        OutboxEventJpaEntity event = new OutboxEventJpaEntity(UUID.randomUUID(), "T", "1", "E", "{}");
        event.markAsProcessed();

        assertEquals(OutboxStatus.PROCESSED, event.getStatus());
        assertNotNull(event.getProcessedAt());
    }

    @Test
    void shouldRegisterFailedAttemptsAndTransitionToFailed() {
        OutboxEventJpaEntity event = new OutboxEventJpaEntity(UUID.randomUUID(), "T", "1", "E", "{}");

        event.registerFailedAttempt(3);
        assertEquals(1, event.getAttempts());
        assertEquals(OutboxStatus.PENDING, event.getStatus());

        event.registerFailedAttempt(3);
        assertEquals(2, event.getAttempts());
        assertEquals(OutboxStatus.PENDING, event.getStatus());

        event.registerFailedAttempt(3);
        assertEquals(3, event.getAttempts());
        assertEquals(OutboxStatus.FAILED, event.getStatus());
    }

    @Test
    void shouldCallProtectedConstructorForCoverage() throws Exception {
        java.lang.reflect.Constructor<OutboxEventJpaEntity> constructor = OutboxEventJpaEntity.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        OutboxEventJpaEntity instance = constructor.newInstance();
        assertNotNull(instance);
    }
}