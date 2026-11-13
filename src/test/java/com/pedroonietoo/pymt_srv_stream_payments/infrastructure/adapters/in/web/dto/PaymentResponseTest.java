package com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.in.web.dto;

import com.pedroonietoo.pymt_srv_stream_payments.domain.models.Money;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.Payment;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.PaymentMethod;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PaymentResponseTest {

    @Test
    void shouldMapFromDomainCorrectly() {
        Payment payment = Payment.create("cust-1", "idemp-1", Money.of(new BigDecimal("100"), "BRL"), PaymentMethod.PIX);

        PaymentResponse response = PaymentResponse.fromDomain(payment);

        assertNotNull(response.id());
        assertEquals("cust-1", response.customerId());
        assertEquals("idemp-1", response.idempotencyKey());
        assertEquals(new BigDecimal("100.00"), response.amount());
        assertEquals("BRL", response.currency());
        assertEquals(PaymentMethod.PIX, response.method());

        assertNotNull(response.toString());
        assertEquals(response, response);
    }
}