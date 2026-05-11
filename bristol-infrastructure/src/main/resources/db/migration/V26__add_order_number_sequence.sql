CREATE SEQUENCE IF NOT EXISTS order_number_seq
    START WITH 1000
    INCREMENT BY 1;

ALTER TABLE orders
    ADD COLUMN IF NOT EXISTS order_number BIGINT;

WITH ordered_orders AS (
    SELECT id,
           999 + ROW_NUMBER() OVER (ORDER BY created_at, id) AS generated_number
    FROM orders
    WHERE order_number IS NULL
)
UPDATE orders o
SET order_number = ordered_orders.generated_number
FROM ordered_orders
WHERE o.id = ordered_orders.id;

SELECT setval(
    'order_number_seq',
    GREATEST(COALESCE((SELECT MAX(order_number) FROM orders), 999), 999),
    true
);

ALTER TABLE orders
    ALTER COLUMN order_number SET DEFAULT nextval('order_number_seq');

ALTER TABLE orders
    ALTER COLUMN order_number SET NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS idx_orders_order_number ON orders(order_number);
