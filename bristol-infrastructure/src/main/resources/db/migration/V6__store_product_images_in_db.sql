ALTER TABLE product_images
    RENAME COLUMN image_url TO legacy_image_url;

ALTER TABLE product_images
    ADD COLUMN IF NOT EXISTS image_data BYTEA,
    ADD COLUMN IF NOT EXISTS content_type VARCHAR(100),
    ADD COLUMN IF NOT EXISTS file_name VARCHAR(255);

COMMENT ON COLUMN product_images.legacy_image_url IS 'Legacy external URL kept temporarily for old rows; new writes use image_data.';
COMMENT ON COLUMN product_images.image_data IS 'Binary image content stored directly in the database.';
