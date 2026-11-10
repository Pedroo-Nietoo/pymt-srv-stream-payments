package com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.in.web.doc;

import com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.in.web.dto.CreatePaymentRequest;
import com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.in.web.dto.PaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Payments", description = "Endpoints para gerenciamento do fluxo de pagamentos")
public interface CreatePaymentDoc {

    @Operation(
            summary = "Criar intenção de pagamento",
            description = "Cria um novo pagamento de forma idempotente com base nos headers informados."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Pagamento criado com sucesso",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Payload inválido ou headers ausentes",
                    content = @Content
            )
    })
    ResponseEntity<PaymentResponse> create(
            @Parameter(
                    in = ParameterIn.HEADER,
                    name = "Idempotency-Key",
                    required = true,
                    description = "Chave única da requisição para prevenir duplicidade",
                    example = "b7a2e2f4-7e82-4217-91f9-9eb10c9c223a"
            )
            String idempotencyKey,

            @Parameter(
                    in = ParameterIn.HEADER,
                    name = "X-Customer-Id",
                    required = true,
                    description = "Identificador do cliente pagador",
                    example = "cust_123456"
            )
            String customerId,

            @RequestBody(
                    description = "Informações monetárias e método do pagamento",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreatePaymentRequest.class))
            )
            CreatePaymentRequest request
    );
}