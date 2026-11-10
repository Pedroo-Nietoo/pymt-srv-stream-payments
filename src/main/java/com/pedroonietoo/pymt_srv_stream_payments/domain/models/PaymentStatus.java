package com.pedroonietoo.pymt_srv_stream_payments.domain.models;

public enum PaymentStatus {
    PENDING,
    PROCESSING,
    APPROVED,
    FAILED;

    public boolean canTransitionTo(PaymentStatus nextStatus) {
        return switch (this) {
            case PENDING -> nextStatus == PROCESSING || nextStatus == FAILED;
            case PROCESSING -> nextStatus == APPROVED || nextStatus == FAILED;
            case APPROVED, FAILED -> false;
        };
    }
}
