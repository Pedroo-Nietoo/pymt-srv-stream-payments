package com.pedroonietoo.pymt_srv_stream_payments.application.ports.out;

public interface OutboxEventPort {

    void saveEvent(String aggregateType, String aggregateId, String eventType, Object eventPayload);
}
