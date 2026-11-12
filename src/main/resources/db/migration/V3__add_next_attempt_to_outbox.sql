ALTER TABLE outbox_events
    ADD COLUMN next_attempt_at TIMESTAMP WITH TIME ZONE;

UPDATE outbox_events
SET next_attempt_at = created_at
WHERE next_attempt_at IS NULL;

ALTER TABLE outbox_events
    ALTER COLUMN next_attempt_at SET NOT NULL;

DROP INDEX IF EXISTS idx_outbox_events_pending;

CREATE INDEX idx_outbox_events_pending
    ON outbox_events (next_attempt_at ASC)
    WHERE status = 'PENDING';