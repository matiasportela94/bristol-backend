CREATE SEQUENCE IF NOT EXISTS payment_number_seq
    START WITH 1000
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS payments (
    id UUID PRIMARY KEY,
    payment_number BIGINT NOT NULL DEFAULT nextval('payment_number_seq'),
    order_id UUID NOT NULL REFERENCES orders(id),
    user_id UUID NOT NULL REFERENCES users(id),
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    provider VARCHAR(30) NOT NULL,
    provider_reference VARCHAR(255),
    amount NUMERIC(10, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'ARS',
    approved_at TIMESTAMP,
    rejected_at TIMESTAMP,
    rejection_reason TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

SELECT setval(
    'payment_number_seq',
    GREATEST(COALESCE((SELECT MAX(payment_number) FROM payments), 999), 999),
    true
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_payments_payment_number
    ON payments(payment_number);

CREATE INDEX IF NOT EXISTS idx_payments_order_id
    ON payments(order_id);

CREATE INDEX IF NOT EXISTS idx_payments_user_id
    ON payments(user_id);

CREATE INDEX IF NOT EXISTS idx_payments_status
    ON payments(payment_status);
