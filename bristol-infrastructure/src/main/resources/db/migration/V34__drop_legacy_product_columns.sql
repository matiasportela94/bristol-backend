-- Drop legacy columns from products table.
-- These were left in V24 (commented out) for gradual migration.
-- ProductEntity replaced by BaseProductEntity + BeerProductEntity / MerchProductEntity / SpecialProductEntity hierarchy.

ALTER TABLE products DROP COLUMN IF EXISTS category;
ALTER TABLE products DROP COLUMN IF EXISTS subcategory;
ALTER TABLE products DROP COLUMN IF EXISTS beer_type;
ALTER TABLE products DROP COLUMN IF EXISTS brewing_method;
ALTER TABLE products DROP COLUMN IF EXISTS flavor;
ALTER TABLE products DROP COLUMN IF EXISTS bitterness;
ALTER TABLE products DROP COLUMN IF EXISTS discount_percentage;
ALTER TABLE products DROP COLUMN IF EXISTS is_active;
ALTER TABLE products DROP COLUMN IF EXISTS average_rating;
ALTER TABLE products DROP COLUMN IF EXISTS total_reviews;
