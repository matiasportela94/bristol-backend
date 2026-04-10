ALTER TABLE coupons
    ADD COLUMN IF NOT EXISTS priority INTEGER;

UPDATE coupons
SET priority = 0
WHERE priority IS NULL;

ALTER TABLE coupons
    ALTER COLUMN priority SET NOT NULL;

