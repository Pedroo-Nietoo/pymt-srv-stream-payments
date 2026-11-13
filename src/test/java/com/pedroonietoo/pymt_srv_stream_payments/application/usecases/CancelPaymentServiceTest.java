package com.pedroonietoo.pymt_srv_stream_payments.application.usecases;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedroonietoo.pymt_srv_stream_payments.application.ports.out.PaymentRepositoryPort;
import com.pedroonietoo.pymt_srv_stream_payments.domain.exceptions.PaymentNotFoundException;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.Payment;
import com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.out.persistence.outbox.OutboxEventJpaEntity;
import com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.out.persistence.outbox.SpringDataOutboxRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CancelPaymentServiceTest {

    @Mock
    private PaymentRepositoryPort paymentRepositoryPort;

    @Mock
    private SpringDataOutboxRepository outboxRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CancelPaymentService cancelPaymentService;

    @Test
    void shouldCancelPaymentAndSaveOutboxEvent() throws Exception {
        UUID paymentId = UUID.randomUUID();
        Payment payment = org.mockito.Mockito.mock(Payment.class);
        when(payment.getId()).thenReturn(paymentId);
        when(paymentRepositoryPort.findById(paymentId)).thenReturn(Optional.of(payment));
        when(objectMapper.writeValueAsString(payment)).thenReturn("{\"id\":\"" + paymentId + "\"}");
        cancelPaymentService.execute(paymentId);

        verify(payment).cancel();
        verify(paymentRepositoryPort).save(payment);
        verify(outboxRepository).save(any(OutboxEventJpaEntity.class));
    }

    @Test
    void shouldThrowExceptionIfPaymentNotFound() {
        UUID paymentId = UUID.randomUUID();
        when(paymentRepositoryPort.findById(paymentId)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> cancelPaymentService.execute(paymentId));
    }

    @Test
    void shouldThrowRuntimeExceptionWhenSerializationFails() throws Exception {
        UUID paymentId = UUID.randomUUID();
        Payment payment = org.mockito.Mockito.mock(Payment.class);
        when(paymentRepositoryPort.findById(paymentId)).thenReturn(Optional.of(payment));
        when(objectMapper.writeValueAsString(payment))
                .thenThrow(org.mockito.Mockito.mock(com.fasterxml.jackson.core.JsonProcessingException.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> cancelPaymentService.execute(paymentId));
        assertEquals("Error serializing cancelled payment event", exception.getMessage());
    }
}