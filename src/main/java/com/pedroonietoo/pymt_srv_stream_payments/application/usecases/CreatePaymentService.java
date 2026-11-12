package com.pedroonietoo.pymt_srv_stream_payments.application.usecases;

import com.pedroonietoo.pymt_srv_stream_payments.application.ports.in.CreatePaymentInput;
import com.pedroonietoo.pymt_srv_stream_payments.application.ports.in.CreatePaymentUseCase;
import com.pedroonietoo.pymt_srv_stream_payments.application.ports.out.OutboxEventPort;
import com.pedroonietoo.pymt_srv_stream_payments.application.ports.out.PaymentRepositoryPort;
import com.pedroonietoo.pymt_srv_stream_payments.domain.events.PaymentCreatedEvent;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.Money;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.Payment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreatePaymentService implements CreatePaymentUseCase {

    private final PaymentRepositoryPort paymentRepositoryPort;
    private final OutboxEventPort outboxEventPort;

    public CreatePaymentService(
            PaymentRepositoryPort paymentRepositoryPort,
            OutboxEventPort outboxEventPort
    ) {
        this.paymentRepositoryPort = paymentRepositoryPort;
        this.outboxEventPort = outboxEventPort;
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

        Payment savedPayment = paymentRepositoryPort.save(payment);

        PaymentCreatedEvent event = PaymentCreatedEvent.from(savedPayment);
        outboxEventPort.saveEvent(
                "PAYMENT",
                savedPayment.getId().toString(),
                PaymentCreatedEvent.EVENT_TYPE,
                event
        );

        return savedPayment;
    }
}