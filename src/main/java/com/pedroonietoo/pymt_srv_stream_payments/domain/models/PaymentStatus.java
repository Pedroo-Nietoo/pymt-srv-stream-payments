package com.pedroonietoo.pymt_srv_stream_payments.domain.models;

public enum PaymentStatus {
    PENDING,
    PROCESSING,
    APPROVED,
    CANCELLED,
    FAILED;

    public boolean canTransitionTo(PaymentStatus nextStatus) {
        return switch (this) {
            case PENDING -> nextStatus == PROCESSING || nextStatus == FAILED || nextStatus == CANCELLED;
            case PROCESSING -> nextStatus == APPROVED || nextStatus == FAILED;
            case APPROVED, FAILED, CANCELLED -> false;
        };
    }
}