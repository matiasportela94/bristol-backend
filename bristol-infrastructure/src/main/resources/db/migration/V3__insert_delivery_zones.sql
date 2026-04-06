-- Insert initial delivery zones with fixed UUIDs
INSERT INTO delivery_zones (id, name, description, is_active, created_at, updated_at)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'SUR', 'Zona Sur', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('22222222-2222-2222-2222-222222222222', 'NORTE', 'Zona Norte', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('33333333-3333-3333-3333-333333333333', 'CENTRO', 'Zona Centro', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
