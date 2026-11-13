package com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.out.persistence.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxPersistenceAdapterTest {

    @Mock
    private SpringDataOutboxRepository outboxRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OutboxPersistenceAdapter adapter;

    @Test
    void shouldSaveEventSuccessfully() throws Exception {
        Object payload = new Object();
        when(objectMapper.writeValueAsString(payload)).thenReturn("{\"key\":\"value\"}");

        adapter.saveEvent("Payment", "id-123", "CREATED", payload);

        verify(outboxRepository).save(any(OutboxEventJpaEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenSerializationFails() throws Exception {
        Object payload = new Object();
        when(objectMapper.writeValueAsString(payload)).thenThrow(org.mockito.Mockito.mock(JsonProcessingException.class));
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                adapter.saveEvent("Payment", "id-123", "CREATED", payload)
        );

        assertEquals("Failed to serialize outbox event payload", exception.getMessage());
    }
}