package com.pedroonietoo.pymt_srv_stream_payments.infrastructure.configuration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SchedulingConfigTest {

    @Test
    void shouldInstantiateConfig() {
        SchedulingConfig config = new SchedulingConfig();
        assertNotNull(config);
    }
}