package com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.in.web.doc;

import com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.in.web.dto.PaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Payments", description = "Endpoints para gerenciamento do fluxo de pagamentos")
public interface GetPaymentDoc {

    @Operation(
            summary = "Consultar pagamento por ID",
            description = "Recupera os detalhes de um pagamento existente através do seu identificador único."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pagamento retornado com sucesso",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pagamento não encontrado",
                    content = @Content
            )
    })
    ResponseEntity<PaymentResponse> getById(
            @Parameter(
                    in = ParameterIn.PATH,
                    name = "id",
                    required = true,
                    description = "ID (UUID) do pagamento",
                    example = "b7a2e2f4-7e82-4217-91f9-9eb10c9c223a"
            )
            UUID id
    );
}