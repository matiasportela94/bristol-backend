-- ============================================
-- BRISTOL PLATFORM - PRODUCTS REFACTORING WITH INHERITANCE
-- ============================================
-- This migration refactors the products table to use table-per-subclass inheritance.
-- Products are split into beer_products, merch_products, and special_products.
-- This allows proper typing and catalog integration.

-- Step 1: Add product_type discriminator column to products table
ALTER TABLE products ADD COLUMN IF NOT EXISTS product_type VARCHAR(20);

-- Step 2: Set product_type based on existing category
UPDATE products
SET product_type = CASE
    WHEN category = 'PRODUCTOS' THEN 'BEER'
    WHEN category = 'MERCHANDISING' THEN 'MERCH'
    WHEN category = 'ESPECIALES' THEN 'SPECIAL'
    ELSE 'BEER'  -- Default fallback
END
WHERE product_type IS NULL;

-- Step 3: Make product_type NOT NULL after setting values
ALTER TABLE products ALTER COLUMN product_type SET NOT NULL;

-- Step 4: Create beer_products table
CREATE TABLE IF NOT EXISTS beer_products (
    id UUID PRIMARY KEY REFERENCES products(id) ON DELETE CASCADE,
    beer_style_id UUID REFERENCES beer_styles(id),
    beer_category VARCHAR(50),  -- Denormalized from beer_styles
    abv DECIMAL(3, 1),
    ibu INTEGER,
    srm INTEGER,
    origin VARCHAR(100),
    brewery VARCHAR(100)
);

-- Step 5: Create merch_products table
CREATE TABLE IF NOT EXISTS merch_products (
    id UUID PRIMARY KEY REFERENCES products(id) ON DELETE CASCADE,
    merch_type_id UUID REFERENCES merch_types(id),
    merch_category VARCHAR(50),  -- Denormalized from merch_types
    material VARCHAR(100),
    brand VARCHAR(100)
);

-- Step 6: Create special_products table
CREATE TABLE IF NOT EXISTS special_products (
    id UUID PRIMARY KEY REFERENCES products(id) ON DELETE CASCADE,
    special_type_id UUID REFERENCES special_types(id),
    notes TEXT,
    requires_quote BOOLEAN NOT NULL DEFAULT FALSE
);

-- Step 7: Migrate existing beer products (PRODUCTOS)
INSERT INTO beer_products (id, beer_category, abv, ibu, srm, origin, brewery)
SELECT
    id,
    'ALE' as beer_category,  -- Default category, will be updated when linking to beer_styles
    abv,
    CAST(ibu AS INTEGER),
    CAST(srm AS INTEGER),
    NULL as origin,
    NULL as brewery
FROM products
WHERE product_type = 'BEER' AND category = 'PRODUCTOS'
ON CONFLICT (id) DO NOTHING;

-- Step 8: Migrate existing merch products (MERCHANDISING)
INSERT INTO merch_products (id, merch_category, material, brand)
SELECT
    id,
    'OTHER' as merch_category,  -- Default category, will be updated when linking to merch_types
    NULL as material,
    NULL as brand
FROM products
WHERE product_type = 'MERCH' AND category = 'MERCHANDISING'
ON CONFLICT (id) DO NOTHING;

-- Step 9: Migrate existing special products (ESPECIALES)
INSERT INTO special_products (id, notes, requires_quote)
SELECT
    id,
    description as notes,
    TRUE as requires_quote  -- Special products typically require quotes
FROM products
WHERE product_type = 'SPECIAL' AND category = 'ESPECIALES'
ON CONFLICT (id) DO NOTHING;

-- Step 10: Remove old columns from products table (now in subclass tables)
-- Keep these columns for now to allow gradual migration, will remove in future version
-- ALTER TABLE products DROP COLUMN IF EXISTS category;
-- ALTER TABLE products DROP COLUMN IF EXISTS subcategory;
-- ALTER TABLE products DROP COLUMN IF EXISTS beer_type;
-- ALTER TABLE products DROP COLUMN IF EXISTS brewing_method;
-- ALTER TABLE products DROP COLUMN IF EXISTS flavor;
-- ALTER TABLE products DROP COLUMN IF EXISTS bitterness;
-- ALTER TABLE products DROP COLUMN IF EXISTS discount_percentage;
-- ALTER TABLE products DROP COLUMN IF EXISTS is_active;
-- ALTER TABLE products DROP COLUMN IF EXISTS average_rating;
-- ALTER TABLE products DROP COLUMN IF EXISTS total_reviews;

-- Step 11: Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_beer_products_style ON beer_products(beer_style_id);
CREATE INDEX IF NOT EXISTS idx_beer_products_category ON beer_products(beer_category);
CREATE INDEX IF NOT EXISTS idx_merch_products_type ON merch_products(merch_type_id);
CREATE INDEX IF NOT EXISTS idx_merch_products_category ON merch_products(merch_category);
CREATE INDEX IF NOT EXISTS idx_special_products_type ON special_products(special_type_id);
CREATE INDEX IF NOT EXISTS idx_products_type ON products(product_type);
CREATE INDEX IF NOT EXISTS idx_products_featured ON products(is_featured);
CREATE INDEX IF NOT EXISTS idx_products_deleted ON products(deleted_at);

-- Step 12: Add comments for documentation
COMMENT ON TABLE beer_products IS 'Beer-specific product attributes (joined with products table)';
COMMENT ON TABLE merch_products IS 'Merchandise-specific product attributes (joined with products table)';
COMMENT ON TABLE special_products IS 'Special product attributes for services and ploteos (joined with products table)';
COMMENT ON COLUMN products.product_type IS 'Discriminator column for product inheritance (BEER, MERCH, SPECIAL)';
