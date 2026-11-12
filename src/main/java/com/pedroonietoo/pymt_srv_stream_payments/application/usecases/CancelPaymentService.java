package com.pedroonietoo.pymt_srv_stream_payments.application.usecases;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedroonietoo.pymt_srv_stream_payments.application.ports.in.CancelPaymentUseCase;
import com.pedroonietoo.pymt_srv_stream_payments.application.ports.out.PaymentRepositoryPort;
import com.pedroonietoo.pymt_srv_stream_payments.domain.exceptions.PaymentNotFoundException;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.Payment;
import com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.out.persistence.outbox.OutboxEventJpaEntity;
import com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.out.persistence.outbox.SpringDataOutboxRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CancelPaymentService implements CancelPaymentUseCase {

    private final PaymentRepositoryPort paymentRepositoryPort;
    private final SpringDataOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public CancelPaymentService(PaymentRepositoryPort paymentRepositoryPort,
                                SpringDataOutboxRepository outboxRepository,
                                ObjectMapper objectMapper) {
        this.paymentRepositoryPort = paymentRepositoryPort;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void execute(UUID paymentId) {
        Payment payment = paymentRepositoryPort.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        payment.cancel();

        paymentRepositoryPort.save(payment);

        try {
            String payload = objectMapper.writeValueAsString(payment);
            OutboxEventJpaEntity outboxEvent = new OutboxEventJpaEntity(
                    UUID.randomUUID(),
                    "payment",
                    payment.getId().toString(),
                    "PAYMENT_CANCELLED",
                    payload
            );
            outboxRepository.save(outboxEvent);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing cancelled payment event", e);
        }
    }
}
