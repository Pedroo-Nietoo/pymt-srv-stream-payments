package com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.out.messaging;

import com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.out.persistence.outbox.OutboxEventJpaEntity;
import com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.out.persistence.outbox.OutboxStatus;
import com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.out.persistence.outbox.SpringDataOutboxRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxRelaySchedulerTest {

    @Mock
    private SpringDataOutboxRepository outboxRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    private OutboxRelayScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new OutboxRelayScheduler(
                outboxRepository,
                kafkaTemplate,
                "pymt-srv",
                50,
                3,
                5
        );
    }

    @Test
    void shouldDoNothingWhenNoPendingEvents() {
        when(outboxRepository.findByStatusAndNextAttemptAtLessThanEqualOrderByNextAttemptAtAsc(
                eq(OutboxStatus.PENDING), any(Instant.class), any(PageRequest.class)
        )).thenReturn(Collections.emptyList());

        scheduler.processPendingEvents();

        verify(kafkaTemplate, never()).send(any(), any(), any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldPublishAndMarkAsProcessedWhenKafkaSucceeds() throws Exception {
        OutboxEventJpaEntity event = new OutboxEventJpaEntity(
                UUID.randomUUID(), "Payment", "id-123", "CREATED", "{}"
        );

        when(outboxRepository.findByStatusAndNextAttemptAtLessThanEqualOrderByNextAttemptAtAsc(
                eq(OutboxStatus.PENDING), any(Instant.class), any(PageRequest.class)
        )).thenReturn(List.of(event));

        CompletableFuture future = mock(CompletableFuture.class);
        when(future.get(5, java.util.concurrent.TimeUnit.SECONDS)).thenReturn(null);
        when(kafkaTemplate.send("pymt-srv.payment-events", "id-123", "{}")).thenReturn(future);

        scheduler.processPendingEvents();

        verify(kafkaTemplate).send("pymt-srv.payment-events", "id-123", "{}");
        verify(outboxRepository).save(event);
        assert event.getStatus() == OutboxStatus.PROCESSED;
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldRegisterFailedAttemptWhenKafkaThrowsException() throws Exception {
        OutboxEventJpaEntity event = new OutboxEventJpaEntity(
                UUID.randomUUID(), "Payment", "id-123", "CREATED", "{}"
        );

        when(outboxRepository.findByStatusAndNextAttemptAtLessThanEqualOrderByNextAttemptAtAsc(
                eq(OutboxStatus.PENDING), any(Instant.class), any(PageRequest.class)
        )).thenReturn(List.of(event));

        when(kafkaTemplate.send(any(), any(), any())).thenThrow(new RuntimeException("Kafka down"));

        scheduler.processPendingEvents();

        verify(outboxRepository).save(event);
        assert event.getAttempts() == 1;
        assert event.getStatus() == OutboxStatus.PENDING;
    }
}