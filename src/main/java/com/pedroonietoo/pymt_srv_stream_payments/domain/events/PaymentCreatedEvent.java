package com.pedroonietoo.pymt_srv_stream_payments.domain.events;

import com.pedroonietoo.pymt_srv_stream_payments.domain.models.Payment;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.PaymentMethod;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentCreatedEvent(
        UUID eventId,
        UUID paymentId,
        String customerId,
        String idempotencyKey,
        BigDecimal amount,
        String currency,
        PaymentMethod method,
        PaymentStatus status,
        Instant occurredAt
) {
    public static final String EVENT_TYPE = "PAYMENT_CREATED";

    public static PaymentCreatedEvent from(Payment payment) {
        return new PaymentCreatedEvent(
                UUID.randomUUID(),
                payment.getId(),
                payment.getCustomerId(),
                payment.getIdempotencyKey(),
                payment.getMoney().amount(),
                payment.getMoney().currency(),
                payment.getMethod(),
                payment.getStatus(),
                Instant.now()
        );
    }
}