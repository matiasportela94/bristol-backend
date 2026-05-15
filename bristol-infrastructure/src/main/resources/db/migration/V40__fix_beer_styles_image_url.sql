-- V37 was applied with old content (added image_url to brewery_inventory).
-- V37 was later updated to target beer_styles instead, but Flyway had already recorded V37.
-- This migration applies the intended change: move image_url to beer_styles.
ALTER TABLE brewery_inventory DROP COLUMN IF EXISTS image_url;
ALTER TABLE beer_styles ADD COLUMN IF NOT EXISTS image_url VARCHAR(500);
