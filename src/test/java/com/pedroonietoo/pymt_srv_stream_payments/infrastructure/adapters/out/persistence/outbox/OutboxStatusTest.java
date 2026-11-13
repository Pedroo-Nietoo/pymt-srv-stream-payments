package com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.out.persistence.outbox;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OutboxStatusTest {

    @Test
    void shouldCoverEnumMethods() {
        assertEquals(OutboxStatus.PENDING, OutboxStatus.valueOf("PENDING"));
        assertEquals(3, OutboxStatus.values().length);
    }
}