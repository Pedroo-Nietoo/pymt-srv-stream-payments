package com.pedroonietoo.pymt_srv_stream_payments.application.ports.out;

import com.pedroonietoo.pymt_srv_stream_payments.domain.models.Payment;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepositoryPort {

    Payment save(Payment payment);
    Optional<Payment> findById(UUID id);
    Optional<Payment> findByIdempotencyKey(String idempotencyKey);
}
