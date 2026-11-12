package com.pedroonietoo.pymt_srv_stream_payments.application.usecases;

import com.pedroonietoo.pymt_srv_stream_payments.application.ports.in.GetPaymentUseCase;
import com.pedroonietoo.pymt_srv_stream_payments.application.ports.out.PaymentRepositoryPort;
import com.pedroonietoo.pymt_srv_stream_payments.domain.exceptions.PaymentNotFoundException;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.Payment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GetPaymentService implements GetPaymentUseCase {

    private final PaymentRepositoryPort paymentRepositoryPort;

    public GetPaymentService(PaymentRepositoryPort paymentRepositoryPort) {
        this.paymentRepositoryPort = paymentRepositoryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public Payment execute(UUID paymentId) {
        return paymentRepositoryPort.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));
    }
}