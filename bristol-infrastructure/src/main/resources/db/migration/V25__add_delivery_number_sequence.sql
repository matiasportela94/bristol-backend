CREATE SEQUENCE IF NOT EXISTS delivery_number_seq
    START WITH 1000
    INCREMENT BY 1;

ALTER TABLE deliveries
    ADD COLUMN IF NOT EXISTS delivery_number BIGINT;

UPDATE deliveries
SET delivery_number = nextval('delivery_number_seq')
WHERE delivery_number IS NULL;

SELECT setval(
    'delivery_number_seq',
    GREATEST(COALESCE((SELECT MAX(delivery_number) FROM deliveries), 999), 999),
    true
);

ALTER TABLE deliveries
    ALTER COLUMN delivery_number SET NOT NULL;

ALTER TABLE deliveries
    ADD CONSTRAINT uk_deliveries_delivery_number UNIQUE (delivery_number);
