package com.pedroonietoo.pymt_srv_stream_payments.domain.exceptions;

import java.util.UUID;

public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException(UUID paymentId) {
        super("Payment not found with id: " + paymentId);
    }
}