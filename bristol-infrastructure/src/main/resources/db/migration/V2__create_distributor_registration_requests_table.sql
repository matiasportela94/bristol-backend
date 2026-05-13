-- Create distributor_registration_requests table when it does not exist yet.
-- Note: delivery_zone_id was in the original design but removed in V15; it is
-- intentionally absent here so V2 has no cross-migration dependency on delivery_zones.
CREATE TABLE IF NOT EXISTS distributor_registration_requests
(
    id               UUID         PRIMARY KEY,
    razon_social     VARCHAR(255) NOT NULL,
    cuit             VARCHAR(20)  NOT NULL,
    email            VARCHAR(255) NOT NULL UNIQUE,
    telefono         VARCHAR(50)  NOT NULL,
    direccion        VARCHAR(500) NOT NULL,
    status           VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    rejection_reason TEXT,
    created_at       TIMESTAMP    NOT NULL,
    updated_at       TIMESTAMP    NOT NULL
);

-- Create indexes for faster queries
CREATE INDEX IF NOT EXISTS idx_registration_status ON distributor_registration_requests (status);
CREATE INDEX IF NOT EXISTS idx_registration_email ON distributor_registration_requests (email);
CREATE INDEX IF NOT EXISTS idx_registration_created_at ON distributor_registration_requests (created_at DESC);
