package com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.out.persistence;

import com.pedroonietoo.pymt_srv_stream_payments.domain.models.Money;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.Payment;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.PaymentMethod;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PaymentMapperTest {

    @Test
    void shouldMapDomainToEntity() {
        Payment payment = Payment.create("cust", "idemp", Money.of(new BigDecimal("100"), "BRL"), PaymentMethod.PIX);

        PaymentJpaEntity entity = PaymentMapper.toJpaEntity(payment);

        assertEquals(payment.getId(), entity.getId());
        assertEquals(payment.getCustomerId(), entity.getCustomerId());
        assertEquals(payment.getMoney().amount(), entity.getAmount());
    }

    @Test
    void shouldMapEntityToDomain() {
        PaymentJpaEntity entity = new PaymentJpaEntity(
                UUID.randomUUID(), "cust", "idemp", new BigDecimal("100.00"), "BRL",
                PaymentMethod.PIX, PaymentStatus.PENDING, null, Instant.now(), Instant.now()
        );

        Payment payment = PaymentMapper.toDomain(entity);

        assertEquals(entity.getId(), payment.getId());
        assertEquals(entity.getAmount(), payment.getMoney().amount());
    }

    @Test
    void shouldHandleNulls() {
        assertNull(PaymentMapper.toJpaEntity(null));
        assertNull(PaymentMapper.toDomain(null));
    }
}