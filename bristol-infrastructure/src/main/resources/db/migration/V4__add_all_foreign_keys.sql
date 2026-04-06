-- ============================================
-- Add foreign keys and indexes to the schema
-- ============================================
-- This migration is idempotent so it works both on old databases and on
-- clean databases created from V1.

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_orders_user') THEN
        ALTER TABLE orders
            ADD CONSTRAINT fk_orders_user
                FOREIGN KEY (user_id) REFERENCES users(id)
                ON DELETE CASCADE;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_orders_distributor') THEN
        ALTER TABLE orders
            ADD CONSTRAINT fk_orders_distributor
                FOREIGN KEY (distributor_id) REFERENCES distributors(id)
                ON DELETE SET NULL;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_orders_delivery_zone') THEN
        ALTER TABLE orders
            ADD CONSTRAINT fk_orders_delivery_zone
                FOREIGN KEY (delivery_zone_id) REFERENCES delivery_zones(id)
                ON DELETE RESTRICT;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_orders_order_coupon') THEN
        ALTER TABLE orders
            ADD CONSTRAINT fk_orders_order_coupon
                FOREIGN KEY (order_discount_coupon_id) REFERENCES coupons(id)
                ON DELETE SET NULL;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_orders_shipping_coupon') THEN
        ALTER TABLE orders
            ADD CONSTRAINT fk_orders_shipping_coupon
                FOREIGN KEY (shipping_discount_coupon_id) REFERENCES coupons(id)
                ON DELETE SET NULL;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_order_items_order') THEN
        ALTER TABLE order_items
            ADD CONSTRAINT fk_order_items_order
                FOREIGN KEY (order_id) REFERENCES orders(id)
                ON DELETE CASCADE;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_order_items_product') THEN
        ALTER TABLE order_items
            ADD CONSTRAINT fk_order_items_product
                FOREIGN KEY (product_id) REFERENCES products(id)
                ON DELETE RESTRICT;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_order_items_variant') THEN
        ALTER TABLE order_items
            ADD CONSTRAINT fk_order_items_variant
                FOREIGN KEY (product_variant_id) REFERENCES product_variants(id)
                ON DELETE SET NULL;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_order_items_coupon') THEN
        ALTER TABLE order_items
            ADD CONSTRAINT fk_order_items_coupon
                FOREIGN KEY (item_discount_coupon_id) REFERENCES coupons(id)
                ON DELETE SET NULL;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_distributors_user') THEN
        ALTER TABLE distributors
            ADD CONSTRAINT fk_distributors_user
                FOREIGN KEY (user_id) REFERENCES users(id)
                ON DELETE CASCADE;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_user_addresses_user') THEN
        ALTER TABLE user_addresses
            ADD CONSTRAINT fk_user_addresses_user
                FOREIGN KEY (user_id) REFERENCES users(id)
                ON DELETE CASCADE;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_user_addresses_delivery_zone') THEN
        ALTER TABLE user_addresses
            ADD CONSTRAINT fk_user_addresses_delivery_zone
                FOREIGN KEY (delivery_zone_id) REFERENCES delivery_zones(id)
                ON DELETE RESTRICT;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_deliveries_order') THEN
        ALTER TABLE deliveries
            ADD CONSTRAINT fk_deliveries_order
                FOREIGN KEY (order_id) REFERENCES orders(id)
                ON DELETE CASCADE;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_deliveries_calendar') THEN
        ALTER TABLE deliveries
            ADD CONSTRAINT fk_deliveries_calendar
                FOREIGN KEY (delivery_calendar_id) REFERENCES delivery_calendars(id)
                ON DELETE CASCADE;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_delivery_calendars_zone') THEN
        ALTER TABLE delivery_calendars
            ADD CONSTRAINT fk_delivery_calendars_zone
                FOREIGN KEY (delivery_zone_id) REFERENCES delivery_zones(id)
                ON DELETE CASCADE;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_product_images_product') THEN
        ALTER TABLE product_images
            ADD CONSTRAINT fk_product_images_product
                FOREIGN KEY (product_id) REFERENCES products(id)
                ON DELETE CASCADE;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_product_variants_product') THEN
        ALTER TABLE product_variants
            ADD CONSTRAINT fk_product_variants_product
                FOREIGN KEY (product_id) REFERENCES products(id)
                ON DELETE CASCADE;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_product_reviews_product') THEN
        ALTER TABLE product_reviews
            ADD CONSTRAINT fk_product_reviews_product
                FOREIGN KEY (product_id) REFERENCES products(id)
                ON DELETE CASCADE;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_product_reviews_user') THEN
        ALTER TABLE product_reviews
            ADD CONSTRAINT fk_product_reviews_user
                FOREIGN KEY (user_id) REFERENCES users(id)
                ON DELETE CASCADE;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_product_price_history_product') THEN
        ALTER TABLE product_price_history
            ADD CONSTRAINT fk_product_price_history_product
                FOREIGN KEY (product_id) REFERENCES products(id)
                ON DELETE CASCADE;
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_distributor_id ON orders(distributor_id);
CREATE INDEX IF NOT EXISTS idx_orders_delivery_zone_id ON orders(delivery_zone_id);
CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_order_items_product_id ON order_items(product_id);
CREATE INDEX IF NOT EXISTS idx_distributors_user_id ON distributors(user_id);
CREATE INDEX IF NOT EXISTS idx_user_addresses_user_id ON user_addresses(user_id);
CREATE INDEX IF NOT EXISTS idx_deliveries_order_id ON deliveries(order_id);
CREATE INDEX IF NOT EXISTS idx_product_images_product_id ON product_images(product_id);
CREATE INDEX IF NOT EXISTS idx_product_variants_product_id ON product_variants(product_id);
CREATE INDEX IF NOT EXISTS idx_product_reviews_product_id ON product_reviews(product_id);
CREATE INDEX IF NOT EXISTS idx_product_reviews_user_id ON product_reviews(user_id);
CREATE INDEX IF NOT EXISTS idx_product_price_history_product_id ON product_price_history(product_id);
