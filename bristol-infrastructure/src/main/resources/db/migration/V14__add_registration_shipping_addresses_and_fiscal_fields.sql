ALTER TABLE distributor_registration_requests
    ADD COLUMN IF NOT EXISTS provincia VARCHAR(100),
    ADD COLUMN IF NOT EXISTS ciudad VARCHAR(100),
    ADD COLUMN IF NOT EXISTS codigo_postal VARCHAR(20);

CREATE TABLE IF NOT EXISTS distributor_registration_addresses (
    id UUID PRIMARY KEY,
    registration_request_id UUID NOT NULL,
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    province VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20),
    delivery_zone_id UUID NOT NULL,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_distributor_registration_addresses_request
        FOREIGN KEY (registration_request_id) REFERENCES distributor_registration_requests (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_distributor_registration_addresses_request
    ON distributor_registration_addresses (registration_request_id);
