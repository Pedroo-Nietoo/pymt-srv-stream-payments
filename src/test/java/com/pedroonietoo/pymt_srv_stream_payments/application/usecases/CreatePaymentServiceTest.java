package com.pedroonietoo.pymt_srv_stream_payments.application.usecases;

import com.pedroonietoo.pymt_srv_stream_payments.application.ports.in.CreatePaymentInput;
import com.pedroonietoo.pymt_srv_stream_payments.application.ports.out.OutboxEventPort;
import com.pedroonietoo.pymt_srv_stream_payments.application.ports.out.PaymentRepositoryPort;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.Payment;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.PaymentMethod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreatePaymentServiceTest {

    @Mock
    private PaymentRepositoryPort paymentRepositoryPort;

    @Mock
    private OutboxEventPort outboxEventPort;

    @InjectMocks
    private CreatePaymentService createPaymentService;

    @Test
    void shouldReturnExistingPaymentWhenIdempotencyKeyExists() {
        CreatePaymentInput input = mock(CreatePaymentInput.class);
        when(input.idempotencyKey()).thenReturn("existing-key");
        Payment existingPayment = mock(Payment.class);
        when(paymentRepositoryPort.findByIdempotencyKey("existing-key")).thenReturn(Optional.of(existingPayment));
        Payment result = createPaymentService.execute(input);

        assertEquals(existingPayment, result);
        verify(paymentRepositoryPort, never()).save(any());
        verify(outboxEventPort, never()).saveEvent(anyString(), anyString(), anyString(), any());
    }

    @Test
    void shouldCreateSaveAndPublishEventForNewPayment() {
        CreatePaymentInput input = mock(CreatePaymentInput.class);
        when(input.idempotencyKey()).thenReturn("new-key");
        when(input.amount()).thenReturn(new BigDecimal("100.00"));
        when(input.currency()).thenReturn("BRL");
        when(input.customerId()).thenReturn("cust-1");
        when(input.method()).thenReturn(mock(PaymentMethod.class));
        when(paymentRepositoryPort.findByIdempotencyKey("new-key")).thenReturn(Optional.empty());
        when(paymentRepositoryPort.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Payment result = createPaymentService.execute(input);

        verify(paymentRepositoryPort).save(any(Payment.class));
        verify(outboxEventPort).saveEvent(eq("PAYMENT"), eq(result.getId().toString()), eq("PAYMENT_CREATED"), any());
    }
}