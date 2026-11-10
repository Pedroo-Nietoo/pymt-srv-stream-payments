package com.pedroonietoo.pymt_srv_stream_payments.application.ports.in;

import com.pedroonietoo.pymt_srv_stream_payments.domain.models.Payment;

public interface CreatePaymentUseCase {

    Payment execute(CreatePaymentInput command);
}
