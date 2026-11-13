package com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.in.web;

import com.pedroonietoo.pymt_srv_stream_payments.application.ports.in.CancelPaymentUseCase;
import com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.in.web.exception.RestExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CancelPaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CancelPaymentUseCase cancelPaymentUseCase;

    @InjectMocks
    private CancelPaymentController cancelPaymentController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cancelPaymentController)
                .setControllerAdvice(new RestExceptionHandler())
                .build();
    }

    @Test
    void shouldReturn204WhenCancellationIsSuccessful() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(cancelPaymentUseCase).execute(id);

        mockMvc.perform(post("/payments/{id}/cancel", id))
                .andExpect(status().isNoContent());
    }
}