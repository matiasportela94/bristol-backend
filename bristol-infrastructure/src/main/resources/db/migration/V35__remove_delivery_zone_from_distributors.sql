DROP INDEX IF EXISTS idx_distributors_zone;
ALTER TABLE distributors DROP COLUMN IF EXISTS delivery_zone_id;
