package com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.in.web.exception.response;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApiErrorResponseTest {

    @Test
    void shouldCreateWithStatusOnlyAndUseSetters() {
        ApiErrorResponse response = new ApiErrorResponse(HttpStatus.BAD_REQUEST);

        assertNotNull(response.getTimestamp());
        assertEquals(400, response.getErrorCode());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertTrue(response.getSubErrors().isEmpty());

        response.setMessage("New Message");
        response.setDetailedMessage("Detailed Message");

        assertEquals("New Message", response.getMessage());
        assertEquals("Detailed Message", response.getDetailedMessage());
    }

    @Test
    void shouldCreateWithStatusAndMessages() {
        ApiErrorResponse response = new ApiErrorResponse(HttpStatus.NOT_FOUND, "Not Found", "Resource is missing");

        assertEquals(404, response.getErrorCode());
        assertEquals("Not Found", response.getMessage());
        assertEquals("Resource is missing", response.getDetailedMessage());
    }

    @Test
    void shouldCreateWithThrowable() {
        RuntimeException ex = new RuntimeException("Exception details inside throwable");
        ApiErrorResponse response = new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error", ex);

        assertEquals("Internal Error", response.getMessage());
        assertEquals("Exception details inside throwable", response.getDetailedMessage());
    }

    @Test
    void shouldCreateWithNullThrowable() {
        ApiErrorResponse response = new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error", (Throwable) null);

        assertEquals("Internal Error", response.getMessage());
        assertNull(response.getDetailedMessage());
    }

    @Test
    void shouldAddValidationErrors() {
        ApiErrorResponse response = new ApiErrorResponse(HttpStatus.BAD_REQUEST);
        FieldError fieldError = new FieldError("paymentRequest", "amount", "must not be null");
        response.addValidationErrors(List.of(fieldError));

        assertEquals(1, response.getSubErrors().size());
        assertEquals("amount", response.getSubErrors().get(0).getCode());
        assertEquals("must not be null", response.getSubErrors().get(0).getMessage());
    }

    @Test
    void shouldAddGlobalValidationErrors() {
        ApiErrorResponse response = new ApiErrorResponse(HttpStatus.BAD_REQUEST);
        ObjectError objectError = new ObjectError("paymentRequest", "global validation failed");
        response.addGlobalValidationErrors(List.of(objectError));

        assertEquals(1, response.getSubErrors().size());
        assertEquals("paymentRequest", response.getSubErrors().get(0).getCode());
        assertEquals("global validation failed", response.getSubErrors().get(0).getMessage());
    }
}