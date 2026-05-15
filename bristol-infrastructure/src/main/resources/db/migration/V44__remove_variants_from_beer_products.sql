DELETE FROM product_variants
WHERE product_id IN (
    SELECT id FROM products WHERE product_type = 'BEER'
);
