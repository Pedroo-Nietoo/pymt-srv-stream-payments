package com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.in.web;

import com.pedroonietoo.pymt_srv_stream_payments.application.ports.in.GetPaymentUseCase;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.Payment;
import com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.in.web.doc.GetPaymentDoc;
import com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.in.web.dto.PaymentResponse;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class GetPaymentController implements GetPaymentDoc {

    private final GetPaymentUseCase getPaymentUseCase;

    public GetPaymentController(GetPaymentUseCase getPaymentUseCase) {
        this.getPaymentUseCase = getPaymentUseCase;
    }

    @GetMapping("/{id}")
    @RateLimiter(name = "payment-api")
    public ResponseEntity<PaymentResponse> getById(@PathVariable UUID id) {
        Payment payment = getPaymentUseCase.execute(id);
        return ResponseEntity.ok(PaymentResponse.fromDomain(payment));
    }
}