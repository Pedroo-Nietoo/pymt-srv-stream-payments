package com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.in.web.dto;

import com.pedroonietoo.pymt_srv_stream_payments.domain.models.Payment;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.PaymentMethod;
import com.pedroonietoo.pymt_srv_stream_payments.domain.models.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Representação detalhada do pagamento")
public record PaymentResponse(
        @Schema(description = "Identificador único do pagamento", example = "d94b0593-39d9-4b2a-bf39-3d1f114bbbc6")
        UUID id,

        @Schema(description = "Identificador do cliente", example = "cust_987654321")
        String customerId,

        @Schema(description = "Chave de idempotência associada", example = "req-uuid-12345")
        String idempotencyKey,

        @Schema(description = "Valor processado", example = "150.00")
        BigDecimal amount,

        @Schema(description = "Moeda", example = "BRL")
        String currency,

        @Schema(description = "Método de pagamento", example = "PIX")
        PaymentMethod method,

        @Schema(description = "Status atual da transação", example = "PENDING")
        PaymentStatus status,

        @Schema(description = "Motivo de recusa caso tenha falhado", example = "null")
        String failureReason,

        @Schema(description = "Data/hora de criação")
        Instant createdAt,

        @Schema(description = "Data/hora da última atualização")
        Instant updatedAt
) {
    public static PaymentResponse fromDomain(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getCustomerId(),
                payment.getIdempotencyKey(),
                payment.getMoney().amount(),
                payment.getMoney().currency(),
                payment.getMethod(),
                payment.getStatus(),
                payment.getFailureReason(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
}