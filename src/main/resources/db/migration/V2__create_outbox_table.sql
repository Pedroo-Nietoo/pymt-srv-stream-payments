CREATE TABLE outbox_events (
                               id UUID PRIMARY KEY,
                               aggregate_type VARCHAR(64) NOT NULL,
                               aggregate_id VARCHAR(64) NOT NULL,
                               event_type VARCHAR(64) NOT NULL,
                               payload JSONB NOT NULL,
                               status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
                               attempts INT NOT NULL DEFAULT 0,
                               created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                               processed_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_outbox_events_pending
    ON outbox_events (created_at ASC)
    WHERE status = 'PENDING';

CREATE INDEX idx_outbox_events_aggregate
    ON outbox_events (aggregate_type, aggregate_id);