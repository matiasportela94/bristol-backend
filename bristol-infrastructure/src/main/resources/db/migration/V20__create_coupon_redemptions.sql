CREATE TABLE IF NOT EXISTS coupon_redemptions (
    id UUID PRIMARY KEY,
    coupon_id UUID NOT NULL REFERENCES coupons(id),
    order_id UUID NOT NULL REFERENCES orders(id),
    user_id UUID NOT NULL REFERENCES users(id),
    applied_amount NUMERIC(10, 2) NOT NULL,
    applied_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_coupon_redemptions_coupon_order UNIQUE (coupon_id, order_id)
);

CREATE INDEX IF NOT EXISTS idx_coupon_redemptions_coupon_id
    ON coupon_redemptions (coupon_id);

CREATE INDEX IF NOT EXISTS idx_coupon_redemptions_order_id
    ON coupon_redemptions (order_id);

CREATE INDEX IF NOT EXISTS idx_coupon_redemptions_coupon_user
    ON coupon_redemptions (coupon_id, user_id);
