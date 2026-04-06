ALTER TABLE coupons
    ADD COLUMN IF NOT EXISTS applies_to VARCHAR(30),
    ADD COLUMN IF NOT EXISTS selected_items TEXT,
    ADD COLUMN IF NOT EXISTS combine_with_order_discounts BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS combine_with_shipping_discounts BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS trigger_type VARCHAR(30),
    ADD COLUMN IF NOT EXISTS trigger_product_id VARCHAR(255),
    ADD COLUMN IF NOT EXISTS trigger_product_name VARCHAR(255),
    ADD COLUMN IF NOT EXISTS applies_to_future_orders BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS specific_customers TEXT,
    ADD COLUMN IF NOT EXISTS rule_config TEXT,
    ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;

UPDATE coupons
SET applies_to = 'ENTIRE_ORDER'
WHERE applies_to IS NULL;

UPDATE coupons
SET selected_items = '[]'
WHERE selected_items IS NULL;

UPDATE coupons
SET trigger_type = 'NONE'
WHERE trigger_type IS NULL;

UPDATE coupons
SET specific_customers = '[]'
WHERE specific_customers IS NULL;

UPDATE coupons
SET rule_config = '{}'
WHERE rule_config IS NULL;

ALTER TABLE coupons
    ALTER COLUMN applies_to SET NOT NULL;

ALTER TABLE coupons
    ALTER COLUMN trigger_type SET NOT NULL;

ALTER TABLE coupons
    ALTER COLUMN combine_with_product_discounts SET DEFAULT FALSE;
