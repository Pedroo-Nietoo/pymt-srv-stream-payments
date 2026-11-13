package com.pedroonietoo.pymt_srv_stream_payments.application.usecases;

import com.pedroonietoo.pymt_srv_stream_payments.application.ports.out.PaymentRepositoryPort;
import com.pedroonietoo.pymt_srv_stream_payments.domain.exceptions.PaymentNotFoundException;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.Payment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetPaymentServiceTest {

    @Mock
    private PaymentRepositoryPort paymentRepositoryPort;

    @InjectMocks
    private GetPaymentService getPaymentService;

    @Test
    void shouldReturnPaymentWhenExists() {
        UUID id = UUID.randomUUID();
        Payment payment = mock(Payment.class);
        when(paymentRepositoryPort.findById(id)).thenReturn(Optional.of(payment));
        Payment result = getPaymentService.execute(id);

        assertEquals(payment, result);
    }

    @Test
    void shouldThrowExceptionWhenPaymentNotFound() {
        UUID id = UUID.randomUUID();
        when(paymentRepositoryPort.findById(id)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> getPaymentService.execute(id));
    }
}