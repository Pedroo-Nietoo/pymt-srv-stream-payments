package com.pedroonietoo.pymt_srv_stream_payments.domain.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PaymentTest {

    @Mock
    private Money money;

    @Mock
    private PaymentMethod method;

    @Test
    void shouldCreateNewPaymentInPendingState() {
        Payment payment = Payment.create("customer-123", "idemp-key", money, method);

        assertNotNull(payment.getId());
        assertEquals("customer-123", payment.getCustomerId());
        assertEquals("idemp-key", payment.getIdempotencyKey());
        assertEquals(money, payment.getMoney());
        assertEquals(method, payment.getMethod());
        assertEquals(PaymentStatus.PENDING, payment.getStatus());
        assertNotNull(payment.getCreatedAt());
        assertNotNull(payment.getUpdatedAt());
        assertNull(payment.getFailureReason());
    }

    @Test
    void shouldTransitionToProcessing() {
        Payment payment = Payment.create("customer-123", "idemp-key", money, method);
        payment.markAsProcessing();

        assertEquals(PaymentStatus.PROCESSING, payment.getStatus());
    }

    @Test
    void shouldTransitionToApproved() {
        Payment payment = Payment.create("customer-123", "idemp-key", money, method);
        payment.markAsProcessing();
        payment.approve();

        assertEquals(PaymentStatus.APPROVED, payment.getStatus());
    }

    @Test
    void shouldTransitionToFailedWithReason() {
        Payment payment = Payment.create("customer-123", "idemp-key", money, method);
        payment.markAsProcessing();
        payment.fail("Insufficient funds");

        assertEquals(PaymentStatus.FAILED, payment.getStatus());
        assertEquals("Insufficient funds", payment.getFailureReason());
    }

    @Test
    void shouldTransitionToCancelled() {
        Payment payment = Payment.create("customer-123", "idemp-key", money, method);
        payment.cancel();

        assertEquals(PaymentStatus.CANCELLED, payment.getStatus());
    }

    @Test
    void shouldFailWhenTransitionIsInvalid() {
        Payment payment = Payment.create("customer-123", "idemp-key", money, method);
        payment.cancel();
        IllegalStateException exception = assertThrows(IllegalStateException.class, payment::approve);

        assertTrue(exception.getMessage().contains("Cannot transition payment"));
    }

    @Test
    void shouldThrowExceptionWhenRequiredConstructorArgumentsAreNull() {
        assertThrows(NullPointerException.class, () ->
                new Payment(null, "customer-123", "idemp-key", money, method, PaymentStatus.PENDING, null, Instant.now(), Instant.now())
        );

        assertThrows(NullPointerException.class, () ->
                new Payment(UUID.randomUUID(), null, "idemp-key", money, method, PaymentStatus.PENDING, null, Instant.now(), Instant.now())
        );

        assertThrows(NullPointerException.class, () ->
                new Payment(UUID.randomUUID(), "customer-123", "idemp-key", null, method, PaymentStatus.PENDING, null, Instant.now(), Instant.now())
        );
    }
}