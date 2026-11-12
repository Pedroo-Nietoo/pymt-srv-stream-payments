package com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.in.web.doc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Payments")
public interface CancelPaymentDoc {

    @Operation(summary = "Cancelar intenção de pagamento", description = "Cancela um pagamento que ainda está com status PENDING.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pagamento cancelado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pagamento não encontrado"),
            @ApiResponse(responseCode = "422", description = "Transição de status inválida (Ex: Pagamento não está PENDING)")
    })
    ResponseEntity<Void> cancel(
            @Parameter(in = ParameterIn.PATH, name = "id", required = true, description = "ID (UUID) do pagamento")
            UUID id
    );
}