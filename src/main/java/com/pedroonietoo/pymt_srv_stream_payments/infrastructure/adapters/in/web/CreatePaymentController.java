package com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.in.web;

import com.pedroonietoo.pymt_srv_stream_payments.application.ports.in.CreatePaymentInput;
import com.pedroonietoo.pymt_srv_stream_payments.application.ports.in.CreatePaymentUseCase;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.Payment;
import com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.in.web.doc.CreatePaymentDoc;
import com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.in.web.dto.CreatePaymentRequest;
import com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.in.web.dto.PaymentResponse;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class CreatePaymentController implements CreatePaymentDoc {

    private final CreatePaymentUseCase createPaymentUseCase;

    public CreatePaymentController(CreatePaymentUseCase createPaymentUseCase) {
        this.createPaymentUseCase = createPaymentUseCase;
    }

    @Override
    @PostMapping
    @RateLimiter(name = "payment-api")
    public ResponseEntity<PaymentResponse> create(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestHeader("X-Customer-Id") String customerId,
            @Valid @RequestBody CreatePaymentRequest request
    ) {
        var input = new CreatePaymentInput(
                customerId,
                idempotencyKey,
                request.amount(),
                request.currency(),
                request.method()
        );

        Payment payment = createPaymentUseCase.execute(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(PaymentResponse.fromDomain(payment));
    }
}