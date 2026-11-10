CREATE TABLE payments (
                          id UUID PRIMARY KEY,
                          customer_id VARCHAR(100) NOT NULL,
                          idempotency_key VARCHAR(150) NOT NULL,
                          amount NUMERIC(19, 2) NOT NULL,
                          currency VARCHAR(3) NOT NULL,
                          method VARCHAR(30) NOT NULL,
                          status VARCHAR(30) NOT NULL,
                          failure_reason VARCHAR(255),
                          created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                          updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

                          CONSTRAINT uk_payments_idempotency_key UNIQUE (idempotency_key)
);

CREATE INDEX idx_payments_customer_id ON payments (customer_id);
CREATE INDEX idx_payments_status ON payments (status);