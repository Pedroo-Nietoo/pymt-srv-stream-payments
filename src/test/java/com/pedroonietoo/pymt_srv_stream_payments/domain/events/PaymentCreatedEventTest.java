package com.pedroonietoo.pymt_srv_stream_payments.domain.events;

import com.pedroonietoo.pymt_srv_stream_payments.domain.models.Money;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.Payment;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.PaymentMethod;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentCreatedEventTest {

    @Mock
    private Payment payment;

    @Test
    void shouldCreateEventFromPayment() {
        UUID paymentId = UUID.randomUUID();
        Money money = mock(Money.class);
        PaymentMethod method = mock(PaymentMethod.class);

        when(payment.getId()).thenReturn(paymentId);
        when(payment.getCustomerId()).thenReturn("cust-123");
        when(payment.getIdempotencyKey()).thenReturn("idemp-key");
        when(payment.getMoney()).thenReturn(money);
        when(money.amount()).thenReturn(new BigDecimal("250.00"));
        when(money.currency()).thenReturn("BRL");
        when(payment.getMethod()).thenReturn(method);
        when(payment.getStatus()).thenReturn(PaymentStatus.PENDING);

        PaymentCreatedEvent event = PaymentCreatedEvent.from(payment);

        assertNotNull(event.eventId());
        assertNotNull(event.occurredAt());
        assertEquals(paymentId, event.paymentId());
        assertEquals("cust-123", event.customerId());
        assertEquals("idemp-key", event.idempotencyKey());
        assertEquals(new BigDecimal("250.00"), event.amount());
        assertEquals("BRL", event.currency());
        assertEquals(method, event.method());
        assertEquals(PaymentStatus.PENDING, event.status());
    }
}