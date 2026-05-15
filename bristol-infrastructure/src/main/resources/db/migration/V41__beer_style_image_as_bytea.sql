ALTER TABLE beer_styles DROP COLUMN IF EXISTS image_url;
ALTER TABLE beer_styles
    ADD COLUMN image_data         BYTEA,
    ADD COLUMN image_content_type VARCHAR(100),
    ADD COLUMN image_file_name    VARCHAR(255);
