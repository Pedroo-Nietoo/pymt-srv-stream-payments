package com.pedroonietoo.pymt_srv_stream_payments.domain.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentStatusTest {

    @Test
    void pendingShouldTransitionToValidStates() {
        assertTrue(PaymentStatus.PENDING.canTransitionTo(PaymentStatus.PROCESSING));
        assertTrue(PaymentStatus.PENDING.canTransitionTo(PaymentStatus.FAILED));
        assertTrue(PaymentStatus.PENDING.canTransitionTo(PaymentStatus.CANCELLED));
        assertFalse(PaymentStatus.PENDING.canTransitionTo(PaymentStatus.APPROVED));
    }

    @Test
    void processingShouldTransitionToValidStates() {
        assertTrue(PaymentStatus.PROCESSING.canTransitionTo(PaymentStatus.APPROVED));
        assertTrue(PaymentStatus.PROCESSING.canTransitionTo(PaymentStatus.FAILED));
        assertFalse(PaymentStatus.PROCESSING.canTransitionTo(PaymentStatus.CANCELLED));
        assertFalse(PaymentStatus.PROCESSING.canTransitionTo(PaymentStatus.PENDING));
    }

    @Test
    void terminalStatesShouldNotTransitionToAnything() {
        assertFalse(PaymentStatus.APPROVED.canTransitionTo(PaymentStatus.PENDING));
        assertFalse(PaymentStatus.APPROVED.canTransitionTo(PaymentStatus.FAILED));
        assertFalse(PaymentStatus.FAILED.canTransitionTo(PaymentStatus.PROCESSING));
        assertFalse(PaymentStatus.CANCELLED.canTransitionTo(PaymentStatus.PENDING));
    }
}