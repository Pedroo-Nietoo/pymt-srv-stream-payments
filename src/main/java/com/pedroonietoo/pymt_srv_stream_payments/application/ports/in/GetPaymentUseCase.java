package com.pedroonietoo.pymt_srv_stream_payments.application.ports.in;

import com.pedroonietoo.pymt_srv_stream_payments.domain.models.Payment;

import java.util.UUID;

public interface GetPaymentUseCase {

    Payment execute(UUID paymentId);
}