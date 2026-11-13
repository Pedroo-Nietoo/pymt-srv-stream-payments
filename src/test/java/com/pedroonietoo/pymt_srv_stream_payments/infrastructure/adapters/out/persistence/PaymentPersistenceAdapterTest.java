package com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.out.persistence;

import com.pedroonietoo.pymt_srv_stream_payments.domain.models.Money;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.Payment;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.PaymentMethod;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentPersistenceAdapterTest {

    @Mock
    private SpringDataPaymentRepository repository;

    @InjectMocks
    private PaymentPersistenceAdapter adapter;

    @Test
    void shouldSavePayment() {
        Payment payment = Payment.create("cust", "idemp", Money.of(BigDecimal.TEN, "BRL"), PaymentMethod.PIX);
        when(repository.save(any(PaymentJpaEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        Payment saved = adapter.save(payment);

        assertEquals(payment.getId(), saved.getId());
    }

    @Test
    void shouldFindById() {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        PaymentJpaEntity realEntity = new PaymentJpaEntity(
                id, "cust-1", "idemp-key", new BigDecimal("10.00"), "BRL",
                PaymentMethod.PIX, PaymentStatus.PENDING, null, now, now
        );

        when(repository.findById(id)).thenReturn(Optional.of(realEntity));

        Optional<Payment> result = adapter.findById(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
    }

    @Test
    void shouldFindByIdempotencyKey() {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        PaymentJpaEntity realEntity = new PaymentJpaEntity(
                id, "cust-1", "idemp-key", new BigDecimal("10.00"), "BRL",
                PaymentMethod.PIX, PaymentStatus.PENDING, null, now, now
        );

        when(repository.findByIdempotencyKey("idemp-key")).thenReturn(Optional.of(realEntity));

        Optional<Payment> result = adapter.findByIdempotencyKey("idemp-key");

        assertTrue(result.isPresent());
        assertEquals("idemp-key", result.get().getIdempotencyKey());
    }
}