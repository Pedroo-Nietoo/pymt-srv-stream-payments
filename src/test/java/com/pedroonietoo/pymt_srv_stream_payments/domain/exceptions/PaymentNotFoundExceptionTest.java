package com.pedroonietoo.pymt_srv_stream_payments.domain.exceptions;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentNotFoundExceptionTest {

    @Test
    void shouldCreateExceptionWithCorrectMessage() {
        UUID paymentId = UUID.randomUUID();
        PaymentNotFoundException exception = new PaymentNotFoundException(paymentId);

        assertEquals("Payment not found with id: " + paymentId, exception.getMessage());
    }
}