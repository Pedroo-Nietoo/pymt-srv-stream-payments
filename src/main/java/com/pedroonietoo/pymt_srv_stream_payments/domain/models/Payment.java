package com.pedroonietoo.pymt_srv_stream_payments.domain.models;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Payment {

    private final UUID id;
    private final String customerId;
    private final String idempotencyKey;
    private final Money money;
    private final PaymentMethod method;
    private PaymentStatus status;
    private String failureReason;
    private final Instant createdAt;
    private Instant updatedAt;

    public Payment(UUID id,
                   String customerId,
                   String idempotencyKey,
                   Money money,
                   PaymentMethod method,
                   PaymentStatus status,
                   String failureReason,
                   Instant createdAt,
                   Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.customerId = Objects.requireNonNull(customerId, "CustomerId cannot be null");
        this.idempotencyKey = Objects.requireNonNull(idempotencyKey, "IdempotencyKey cannot be null");
        this.money = Objects.requireNonNull(money, "Money cannot be null");
        this.method = Objects.requireNonNull(method, "PaymentMethod cannot be null");
        this.status = Objects.requireNonNull(status, "PaymentStatus cannot be null");
        this.failureReason = failureReason;
        this.createdAt = Objects.requireNonNull(createdAt, "CreatedAt cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "UpdatedAt cannot be null");
    }

    public static Payment create(String customerId, String idempotencyKey, Money money, PaymentMethod method) {
        Instant now = Instant.now();
        return new Payment(
                UUID.randomUUID(),
                customerId,
                idempotencyKey,
                money,
                method,
                PaymentStatus.PENDING,
                null,
                now,
                now
        );
    }

    public void markAsProcessing() {
        validateTransition(PaymentStatus.PROCESSING);
        this.status = PaymentStatus.PROCESSING;
        this.updatedAt = Instant.now();
    }

    public void approve() {
        validateTransition(PaymentStatus.APPROVED);
        this.status = PaymentStatus.APPROVED;
        this.updatedAt = Instant.now();
    }

    public void cancel() {
        validateTransition(PaymentStatus.CANCELLED);
        this.status = PaymentStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

    public void fail(String reason) {
        validateTransition(PaymentStatus.FAILED);
        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
        this.updatedAt = Instant.now();
    }

    private void validateTransition(PaymentStatus targetStatus) {
        if (!this.status.canTransitionTo(targetStatus)) {
            throw new IllegalStateException(
                    String.format("Cannot transition payment from %s to %s", this.status, targetStatus)
            );
        }
    }

    public UUID getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public Money getMoney() { return money; }
    public PaymentMethod getMethod() { return method; }
    public PaymentStatus getStatus() { return status; }
    public String getFailureReason() { return failureReason; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
