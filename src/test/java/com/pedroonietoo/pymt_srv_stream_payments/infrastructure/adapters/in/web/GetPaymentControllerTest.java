package com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.in.web;

import com.pedroonietoo.pymt_srv_stream_payments.application.ports.in.GetPaymentUseCase;
import com.pedroonietoo.pymt_srv_stream_payments.domain.exceptions.PaymentNotFoundException;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.Money;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.Payment;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.PaymentMethod;
import com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.in.web.exception.RestExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GetPaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GetPaymentUseCase getPaymentUseCase;

    @InjectMocks
    private GetPaymentController getPaymentController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(getPaymentController)
                .setControllerAdvice(new RestExceptionHandler())
                .build();
    }

    @Test
    void shouldReturn200WhenPaymentExists() throws Exception {
        UUID id = UUID.randomUUID();
        Payment mockPayment = Payment.create("cust-123", "idemp-key", Money.of(new BigDecimal("150.00"), "BRL"), PaymentMethod.PIX);
        when(getPaymentUseCase.execute(id)).thenReturn(mockPayment);

        mockMvc.perform(get("/payments/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value("cust-123"));
    }

    @Test
    void shouldReturn404WhenPaymentNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(getPaymentUseCase.execute(id)).thenThrow(new PaymentNotFoundException(id));

        mockMvc.perform(get("/payments/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.apiError.errorCode").value(404));
    }
}