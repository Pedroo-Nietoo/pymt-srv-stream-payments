package com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.out.persistence;

import com.pedroonietoo.pymt_srv_stream_payments.domain.models.PaymentMethod;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PaymentJpaEntityTest {

    @Test
    void shouldCreateAndAccessProperties() {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        PaymentJpaEntity entity = new PaymentJpaEntity(
                id, "cust-1", "idemp-1", new BigDecimal("100"), "BRL",
                PaymentMethod.PIX, PaymentStatus.PENDING, "reason", now, now
        );

        assertEquals(id, entity.getId());
        assertEquals("cust-1", entity.getCustomerId());
        assertEquals("idemp-1", entity.getIdempotencyKey());
        assertEquals(new BigDecimal("100"), entity.getAmount());
        assertEquals("BRL", entity.getCurrency());
        assertEquals(PaymentMethod.PIX, entity.getMethod());
        assertEquals(PaymentStatus.PENDING, entity.getStatus());
        assertEquals("reason", entity.getFailureReason());
        assertEquals(now, entity.getCreatedAt());
        assertEquals(now, entity.getUpdatedAt());
    }

    @Test
    void shouldCallProtectedConstructorForCoverage() throws Exception {
        java.lang.reflect.Constructor<PaymentJpaEntity> constructor = PaymentJpaEntity.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        PaymentJpaEntity instance = constructor.newInstance();
        assertNotNull(instance);
    }
}