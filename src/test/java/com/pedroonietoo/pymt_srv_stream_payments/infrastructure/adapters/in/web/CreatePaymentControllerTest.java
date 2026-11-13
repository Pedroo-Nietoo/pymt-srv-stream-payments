package com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedroonietoo.pymt_srv_stream_payments.application.ports.in.CreatePaymentInput;
import com.pedroonietoo.pymt_srv_stream_payments.application.ports.in.CreatePaymentUseCase;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.Money;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.Payment;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.PaymentMethod;
import com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.in.web.dto.CreatePaymentRequest;
import com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.in.web.exception.RestExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CreatePaymentControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CreatePaymentUseCase createPaymentUseCase;

    @InjectMocks
    private CreatePaymentController createPaymentController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(createPaymentController)
                .setControllerAdvice(new RestExceptionHandler())
                .build();
    }

    @Test
    void shouldReturn201WhenPaymentIsCreatedSuccessfully() throws Exception {
        CreatePaymentRequest request = new CreatePaymentRequest(
                new BigDecimal("150.00"), "BRL", PaymentMethod.PIX
        );

        Payment mockPayment = Payment.create("cust-123", "idemp-key", Money.of(new BigDecimal("150.00"), "BRL"), PaymentMethod.PIX);
        when(createPaymentUseCase.execute(any(CreatePaymentInput.class))).thenReturn(mockPayment);

        mockMvc.perform(post("/payments")
                        .header("Idempotency-Key", "idemp-key")
                        .header("X-Customer-Id", "cust-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.customerId").value("cust-123"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldReturn400WhenHeadersAreMissing() throws Exception {
        CreatePaymentRequest request = new CreatePaymentRequest(
                new BigDecimal("150.00"), "BRL", PaymentMethod.PIX
        );

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.apiError.errorCode").value(400));
    }
}