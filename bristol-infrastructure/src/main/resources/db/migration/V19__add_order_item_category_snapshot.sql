ALTER TABLE order_items
    ADD COLUMN IF NOT EXISTS product_category VARCHAR(32),
    ADD COLUMN IF NOT EXISTS product_subcategory VARCHAR(32);
