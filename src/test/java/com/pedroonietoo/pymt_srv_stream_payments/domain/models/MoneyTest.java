package com.pedroonietoo.pymt_srv_stream_payments.domain.models;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoneyTest {

    @Test
    void shouldCreateMoneyAndScaleToTwoDecimalPlaces() {
        Money money = Money.of(new BigDecimal("100.5"), "BRL");

        assertNotNull(money);
        assertEquals(new BigDecimal("100.50"), money.amount());
        assertEquals("BRL", money.currency());
    }

    @Test
    void shouldRoundUpWhenThirdDecimalIsFiveOrGreater() {
        Money money = Money.of(new BigDecimal("100.125"), "BRL");

        assertEquals(new BigDecimal("100.13"), money.amount());
    }

    @Test
    void shouldAllowZeroAmount() {
        Money money = Money.of(BigDecimal.ZERO, "USD");

        assertEquals(new BigDecimal("0.00"), money.amount());
    }

    @Test
    void shouldThrowExceptionWhenAmountIsNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Money.of(new BigDecimal("-1.00"), "BRL")
        );

        assertEquals("Payment amount must be greater than zero", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAmountIsNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                Money.of(null, "BRL")
        );

        assertEquals("Amount cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCurrencyIsNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                Money.of(BigDecimal.TEN, null)
        );

        assertEquals("Currency cannot be null", exception.getMessage());
    }
}