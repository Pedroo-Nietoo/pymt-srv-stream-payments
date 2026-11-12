package com.pedroonietoo.pymt_srv_stream_payments.application.ports.in;

import java.util.UUID;

public interface CancelPaymentUseCase {

    void execute(UUID paymentId);
}