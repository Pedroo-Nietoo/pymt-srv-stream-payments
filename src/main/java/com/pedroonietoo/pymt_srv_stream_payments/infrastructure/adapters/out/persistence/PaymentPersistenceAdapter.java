package com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.out.persistence;

import com.pedroonietoo.pymt_srv_stream_payments.application.ports.out.PaymentRepositoryPort;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.Payment;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PaymentPersistenceAdapter implements PaymentRepositoryPort {

    private final SpringDataPaymentRepository springDataPaymentRepository;

    public PaymentPersistenceAdapter(SpringDataPaymentRepository springDataPaymentRepository) {
        this.springDataPaymentRepository = springDataPaymentRepository;
    }

    @Override
    public Payment save(Payment payment) {
        PaymentJpaEntity entity = PaymentMapper.toJpaEntity(payment);
        PaymentJpaEntity savedEntity = springDataPaymentRepository.save(entity);
        return PaymentMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Payment> findById(UUID id) {
        return springDataPaymentRepository.findById(id)
                .map(PaymentMapper::toDomain);
    }

    @Override
    public Optional<Payment> findByIdempotencyKey(String idempotencyKey) {
        return springDataPaymentRepository.findByIdempotencyKey(idempotencyKey)
                .map(PaymentMapper::toDomain);
    }
}
