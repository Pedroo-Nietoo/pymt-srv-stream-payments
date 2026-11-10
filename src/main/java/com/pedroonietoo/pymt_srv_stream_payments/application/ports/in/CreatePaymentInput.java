package com.pedroonietoo.pymt_srv_stream_payments.application.ports.in;

import com.pedroonietoo.pymt_srv_stream_payments.domain.models.PaymentMethod;

import java.math.BigDecimal;
import java.util.Objects;

public record CreatePaymentInput(
        String customerId,
        String idempotencyKey,
        BigDecimal amount,
        String currency,
        PaymentMethod method
) {
    public CreatePaymentInput {
        Objects.requireNonNull(customerId, "CustomerId must not be null");
        Objects.requireNonNull(idempotencyKey, "IdempotencyKey must not be null");
        Objects.requireNonNull(amount, "Amount must not be null");
        Objects.requireNonNull(currency, "Currency must not be null");
        Objects.requireNonNull(method, "PaymentMethod must not be null");
    }
}