package com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.in.web.dto;

import com.pedroonietoo.pymt_srv_stream_payments.domain.models.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Dados para criação de uma intenção de pagamento")
public record CreatePaymentRequest(

        @Schema(description = "Valor do pagamento", example = "150.00", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        BigDecimal amount,

        @Schema(description = "Código da moeda (ISO 4217)", example = "BRL", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Currency is required")
        @Size(min = 3, max = 3, message = "Currency must have exactly 3 characters")
        String currency,

        @Schema(description = "Método de pagamento", example = "PIX", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Payment method is required")
        PaymentMethod method
) { }