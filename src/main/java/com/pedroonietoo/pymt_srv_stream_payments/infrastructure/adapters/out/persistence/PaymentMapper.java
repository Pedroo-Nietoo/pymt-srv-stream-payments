package com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.out.persistence;

import com.pedroonietoo.pymt_srv_stream_payments.domain.models.Money;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.Payment;

public final class PaymentMapper {

    private PaymentMapper() { }

    public static PaymentJpaEntity toJpaEntity(Payment domain) {
        return domain == null ? null : new PaymentJpaEntity(
                domain.getId(),
                domain.getCustomerId(),
                domain.getIdempotencyKey(),
                domain.getMoney().amount(),
                domain.getMoney().currency(),
                domain.getMethod(),
                domain.getStatus(),
                domain.getFailureReason(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }

    public static Payment toDomain(PaymentJpaEntity entity) {
        return entity == null ? null : new Payment(
                entity.getId(),
                entity.getCustomerId(),
                entity.getIdempotencyKey(),
                Money.of(entity.getAmount(), entity.getCurrency()),
                entity.getMethod(),
                entity.getStatus(),
                entity.getFailureReason(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
