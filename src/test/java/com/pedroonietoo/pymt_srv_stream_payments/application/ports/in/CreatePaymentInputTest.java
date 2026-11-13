package com.pedroonietoo.pymt_srv_stream_payments.application.ports.in;

import com.pedroonietoo.pymt_srv_stream_payments.domain.models.PaymentMethod;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class CreatePaymentInputTest {

    @Test
    void shouldCreateRecordWhenAllFieldsAreValid() {
        PaymentMethod mockMethod = mock(PaymentMethod.class);
        BigDecimal amount = new BigDecimal("150.00");

        CreatePaymentInput input = new CreatePaymentInput(
                "customer-456",
                "idemp-123",
                amount,
                "BRL",
                mockMethod
        );

        assertNotNull(input);
        assertEquals("customer-456", input.customerId());
        assertEquals("idemp-123", input.idempotencyKey());
        assertEquals(amount, input.amount());
        assertEquals("BRL", input.currency());
        assertEquals(mockMethod, input.method());
    }

    @Test
    void shouldThrowExceptionWhenCustomerIdIsNull() {
        PaymentMethod mockMethod = mock(PaymentMethod.class);
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                new CreatePaymentInput(null, "idemp-123", BigDecimal.TEN, "BRL", mockMethod)
        );

        assertEquals("CustomerId must not be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenIdempotencyKeyIsNull() {
        PaymentMethod mockMethod = mock(PaymentMethod.class);
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                new CreatePaymentInput("customer-456", null, BigDecimal.TEN, "BRL", mockMethod)
        );

        assertEquals("IdempotencyKey must not be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAmountIsNull() {
        PaymentMethod mockMethod = mock(PaymentMethod.class);
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                new CreatePaymentInput("customer-456", "idemp-123", null, "BRL", mockMethod)
        );

        assertEquals("Amount must not be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCurrencyIsNull() {
        PaymentMethod mockMethod = mock(PaymentMethod.class);
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                new CreatePaymentInput("customer-456", "idemp-123", BigDecimal.TEN, null, mockMethod)
        );

        assertEquals("Currency must not be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPaymentMethodIsNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                new CreatePaymentInput("customer-456", "idemp-123", BigDecimal.TEN, "BRL", null)
        );

        assertEquals("PaymentMethod must not be null", exception.getMessage());
    }
}