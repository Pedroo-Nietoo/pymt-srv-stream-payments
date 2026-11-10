package com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.out.persistence;

import com.pedroonietoo.pymt_srv_stream_payments.domain.models.PaymentMethod;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class PaymentJpaEntity {

    @Id
    private UUID id;

    @Column(name = "customer_id", nullable = false, length = 100)
    private String customerId;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 150)
    private String idempotencyKey;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false, length = 30)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private PaymentStatus status;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected PaymentJpaEntity() {
    }

    public PaymentJpaEntity(UUID id,
                            String customerId,
                            String idempotencyKey,
                            BigDecimal amount,
                            String currency,
                            PaymentMethod method,
                            PaymentStatus status,
                            String failureReason,
                            Instant createdAt,
                            Instant updatedAt) {
        this.id = id;
        this.customerId = customerId;
        this.idempotencyKey = idempotencyKey;
        this.amount = amount;
        this.currency = currency;
        this.method = method;
        this.status = status;
        this.failureReason = failureReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public PaymentMethod getMethod() { return method; }
    public PaymentStatus getStatus() { return status; }
    public String getFailureReason() { return failureReason; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}