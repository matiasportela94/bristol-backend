ALTER TABLE brewery_inventory DROP COLUMN IF EXISTS image_url;
ALTER TABLE beer_styles ADD COLUMN image_url VARCHAR(500);
