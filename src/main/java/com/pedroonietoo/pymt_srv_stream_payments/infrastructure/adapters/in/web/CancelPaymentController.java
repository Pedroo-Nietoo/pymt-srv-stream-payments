package com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.in.web;

import com.pedroonietoo.pymt_srv_stream_payments.application.ports.in.CancelPaymentUseCase;
import com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.in.web.doc.CancelPaymentDoc;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class CancelPaymentController implements CancelPaymentDoc {

    private final CancelPaymentUseCase cancelPaymentUseCase;

    public CancelPaymentController(CancelPaymentUseCase cancelPaymentUseCase) {
        this.cancelPaymentUseCase = cancelPaymentUseCase;
    }

    @PostMapping("/{id}/cancel")
    @RateLimiter(name = "payment-api")
    public ResponseEntity<Void> cancel(@PathVariable UUID id) {
        cancelPaymentUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}