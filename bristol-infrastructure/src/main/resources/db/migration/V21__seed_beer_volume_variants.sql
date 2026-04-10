INSERT INTO product_variants (
    id,
    product_id,
    sku,
    variant_name,
    size_ml,
    color,
    additional_price,
    stock_quantity,
    is_active,
    created_at,
    updated_at
)
SELECT
    seed.id,
    seed.product_id,
    seed.sku,
    '433ml',
    433,
    NULL,
    0.00,
    p.stock_quantity,
    TRUE,
    NOW(),
    NOW()
FROM (
    VALUES
        ('3f7a5dc1-8d8e-4f9d-bad8-3d8e4d9e4101'::uuid, 'd20afd4a-8f33-43b8-824b-4fbb596857e6'::uuid, 'BRI-LATA-IPA-433'),
        ('5dbcb642-8b7d-4eb2-989b-26d8e5fd4102'::uuid, 'f3ce0c8a-e8bb-412c-878e-89916578dae3'::uuid, 'BRI-6PACK-IPA-433'),
        ('6c9fd9d0-46ef-4f0f-a25c-7bb338574103'::uuid, '9c239e80-e51b-43cc-851b-bd5cfdcf5d59'::uuid, 'BRI-6PACK-LAGER-433'),
        ('8f2b4df4-5c0b-40c4-8f9d-1f2f5ec54104'::uuid, 'f414d403-976d-4a9e-abf2-c4bf367ee967'::uuid, 'BRI-6PACK-PALE-433')
) AS seed(id, product_id, sku)
JOIN products p ON p.id = seed.product_id
WHERE NOT EXISTS (
    SELECT 1
    FROM product_variants pv
    WHERE pv.product_id = seed.product_id
);
