package com.pedroonietoo.pymt_srv_stream_payments.application.usecases;


import com.pedroonietoo.pymt_srv_stream_payments.application.ports.in.CreatePaymentInput;
import com.pedroonietoo.pymt_srv_stream_payments.application.ports.in.CreatePaymentUseCase;
import com.pedroonietoo.pymt_srv_stream_payments.application.ports.out.PaymentRepositoryPort;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.Money;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.Payment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreatePaymentService implements CreatePaymentUseCase {

    private final PaymentRepositoryPort paymentRepositoryPort;

    public CreatePaymentService(PaymentRepositoryPort paymentRepositoryPort) {
        this.paymentRepositoryPort = paymentRepositoryPort;
    }

    @Override
    @Transactional
    public Payment execute(CreatePaymentInput input) {
        return paymentRepositoryPort.findByIdempotencyKey(input.idempotencyKey())
                .orElseGet(() -> createAndSavePayment(input));
    }

    private Payment createAndSavePayment(CreatePaymentInput input) {
        Money money = Money.of(input.amount(), input.currency());
        Payment payment = Payment.create(
                input.customerId(),
                input.idempotencyKey(),
                money,
                input.method()
        );

        return paymentRepositoryPort.save(payment);
    }
}