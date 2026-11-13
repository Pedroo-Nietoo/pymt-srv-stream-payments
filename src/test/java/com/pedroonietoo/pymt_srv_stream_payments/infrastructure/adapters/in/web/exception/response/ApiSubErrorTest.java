package com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.in.web.exception.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiSubErrorTest {

    @Test
    void shouldCreateAndGetFields() {
        ApiSubError error = new ApiSubError("RESOURCE_NOT_FOUND", "The resource was not found");

        assertEquals("RESOURCE_NOT_FOUND", error.getCode());
        assertEquals("The resource was not found", error.getMessage());
    }
}