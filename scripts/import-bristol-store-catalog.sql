BEGIN;

UPDATE products
SET description = 'Importado desde la tienda Bristol. Producto visible como disponible al 2026-03-31. Fuente: https://cervezabristol.mitiendanube.com/productos/six-pack-bristol-lager/',
    category = 'PRODUCTOS',
    subcategory = 'SIX_PACK',
    beer_type = 'LAGER',
    base_price = 11060.00,
    stock_quantity = 1,
    low_stock_threshold = 1,
    is_active = TRUE,
    is_featured = FALSE,
    discount_percentage = 0,
    total_reviews = 0,
    deleted_at = NULL,
    updated_at = NOW()
WHERE name = 'Six Pack Bristol Lager';

INSERT INTO products (id, name, description, category, subcategory, beer_type, base_price, discount_percentage, stock_quantity, low_stock_threshold, is_active, is_featured, total_reviews, created_at, updated_at, deleted_at)
SELECT '91000000-0000-0000-0000-000000000001', 'Six Pack Bristol Lager', 'Importado desde la tienda Bristol. Producto visible como disponible al 2026-03-31. Fuente: https://cervezabristol.mitiendanube.com/productos/six-pack-bristol-lager/', 'PRODUCTOS', 'SIX_PACK', 'LAGER', 11060.00, 0, 1, 1, TRUE, FALSE, 0, NOW(), NOW(), NULL
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Six Pack Bristol Lager');

UPDATE products
SET description = 'Importado desde la tienda Bristol. Producto visible como disponible al 2026-03-31. Fuente: https://cervezabristol.mitiendanube.com/productos/six-pack-bristol-pale-ale/',
    category = 'PRODUCTOS',
    subcategory = 'SIX_PACK',
    beer_type = 'APA',
    base_price = 11500.00,
    stock_quantity = 1,
    low_stock_threshold = 1,
    is_active = TRUE,
    is_featured = FALSE,
    discount_percentage = 0,
    total_reviews = 0,
    deleted_at = NULL,
    updated_at = NOW()
WHERE name = 'Six pack Bristol Pale Ale';

INSERT INTO products (id, name, description, category, subcategory, beer_type, base_price, discount_percentage, stock_quantity, low_stock_threshold, is_active, is_featured, total_reviews, created_at, updated_at, deleted_at)
SELECT '91000000-0000-0000-0000-000000000002', 'Six pack Bristol Pale Ale', 'Importado desde la tienda Bristol. Producto visible como disponible al 2026-03-31. Fuente: https://cervezabristol.mitiendanube.com/productos/six-pack-bristol-pale-ale/', 'PRODUCTOS', 'SIX_PACK', 'APA', 11500.00, 0, 1, 1, TRUE, FALSE, 0, NOW(), NOW(), NULL
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Six pack Bristol Pale Ale');

UPDATE products
SET description = 'Importado desde la tienda Bristol. Producto visible como disponible al 2026-03-31. Fuente: https://cervezabristol.mitiendanube.com/productos/six-pack-bristol-ipa/',
    category = 'PRODUCTOS',
    subcategory = 'SIX_PACK',
    beer_type = 'IPA',
    base_price = 13770.00,
    stock_quantity = 1,
    low_stock_threshold = 1,
    is_active = TRUE,
    is_featured = FALSE,
    discount_percentage = 0,
    total_reviews = 0,
    deleted_at = NULL,
    updated_at = NOW()
WHERE name = 'Six Pack Bristol IPA';

INSERT INTO products (id, name, description, category, subcategory, beer_type, base_price, discount_percentage, stock_quantity, low_stock_threshold, is_active, is_featured, total_reviews, created_at, updated_at, deleted_at)
SELECT '91000000-0000-0000-0000-000000000003', 'Six Pack Bristol IPA', 'Importado desde la tienda Bristol. Producto visible como disponible al 2026-03-31. Fuente: https://cervezabristol.mitiendanube.com/productos/six-pack-bristol-ipa/', 'PRODUCTOS', 'SIX_PACK', 'IPA', 13770.00, 0, 1, 1, TRUE, FALSE, 0, NOW(), NOW(), NULL
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Six Pack Bristol IPA');

UPDATE products
SET description = 'Importado desde la tienda Bristol. Fuente: https://cervezabristol.mitiendanube.com/productos/buzo-bristol-feat-luys/',
    category = 'MERCHANDISING',
    subcategory = 'BUZO',
    beer_type = NULL,
    base_price = 75000.00,
    stock_quantity = 1,
    low_stock_threshold = 1,
    is_active = TRUE,
    is_featured = FALSE,
    discount_percentage = 0,
    total_reviews = 0,
    deleted_at = NULL,
    updated_at = NOW()
WHERE name = 'BUZO BRISTOL FEAT LUYS';

INSERT INTO products (id, name, description, category, subcategory, beer_type, base_price, discount_percentage, stock_quantity, low_stock_threshold, is_active, is_featured, total_reviews, created_at, updated_at, deleted_at)
SELECT '91000000-0000-0000-0000-000000000004', 'BUZO BRISTOL FEAT LUYS', 'Importado desde la tienda Bristol. Fuente: https://cervezabristol.mitiendanube.com/productos/buzo-bristol-feat-luys/', 'MERCHANDISING', 'BUZO', NULL, 75000.00, 0, 1, 1, TRUE, FALSE, 0, NOW(), NOW(), NULL
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'BUZO BRISTOL FEAT LUYS');

UPDATE products
SET description = 'Importado desde la tienda Bristol. Fuente: https://cervezabristol.mitiendanube.com/productos/remera-bristol-amanecer/',
    category = 'MERCHANDISING',
    subcategory = 'REMERA',
    beer_type = NULL,
    base_price = 45000.00,
    stock_quantity = 14,
    low_stock_threshold = 2,
    is_active = TRUE,
    is_featured = FALSE,
    discount_percentage = 0,
    total_reviews = 0,
    deleted_at = NULL,
    updated_at = NOW()
WHERE name = 'REMERA BRISTOL AMANECER';

INSERT INTO products (id, name, description, category, subcategory, beer_type, base_price, discount_percentage, stock_quantity, low_stock_threshold, is_active, is_featured, total_reviews, created_at, updated_at, deleted_at)
SELECT '91000000-0000-0000-0000-000000000005', 'REMERA BRISTOL AMANECER', 'Importado desde la tienda Bristol. Fuente: https://cervezabristol.mitiendanube.com/productos/remera-bristol-amanecer/', 'MERCHANDISING', 'REMERA', NULL, 45000.00, 0, 14, 2, TRUE, FALSE, 0, NOW(), NOW(), NULL
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'REMERA BRISTOL AMANECER');

UPDATE products
SET description = 'Importado desde la tienda Bristol. Fuente: https://cervezabristol.mitiendanube.com/productos/remera-bristol-copa/',
    category = 'MERCHANDISING',
    subcategory = 'REMERA',
    beer_type = NULL,
    base_price = 45000.00,
    stock_quantity = 14,
    low_stock_threshold = 2,
    is_active = TRUE,
    is_featured = FALSE,
    discount_percentage = 0,
    total_reviews = 0,
    deleted_at = NULL,
    updated_at = NOW()
WHERE name = 'REMERA BRISTOL COPA';

INSERT INTO products (id, name, description, category, subcategory, beer_type, base_price, discount_percentage, stock_quantity, low_stock_threshold, is_active, is_featured, total_reviews, created_at, updated_at, deleted_at)
SELECT '91000000-0000-0000-0000-000000000006', 'REMERA BRISTOL COPA', 'Importado desde la tienda Bristol. Fuente: https://cervezabristol.mitiendanube.com/productos/remera-bristol-copa/', 'MERCHANDISING', 'REMERA', NULL, 45000.00, 0, 14, 2, TRUE, FALSE, 0, NOW(), NOW(), NULL
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'REMERA BRISTOL COPA');

UPDATE products
SET description = 'Importado desde la tienda Bristol. Fuente: https://cervezabristol.mitiendanube.com/productos/remera-bristol-ola/',
    category = 'MERCHANDISING',
    subcategory = 'REMERA',
    beer_type = NULL,
    base_price = 45000.00,
    stock_quantity = 14,
    low_stock_threshold = 2,
    is_active = TRUE,
    is_featured = FALSE,
    discount_percentage = 0,
    total_reviews = 0,
    deleted_at = NULL,
    updated_at = NOW()
WHERE name = 'REMERA BRISTOL OLA';

INSERT INTO products (id, name, description, category, subcategory, beer_type, base_price, discount_percentage, stock_quantity, low_stock_threshold, is_active, is_featured, total_reviews, created_at, updated_at, deleted_at)
SELECT '91000000-0000-0000-0000-000000000007', 'REMERA BRISTOL OLA', 'Importado desde la tienda Bristol. Fuente: https://cervezabristol.mitiendanube.com/productos/remera-bristol-ola/', 'MERCHANDISING', 'REMERA', NULL, 45000.00, 0, 14, 2, TRUE, FALSE, 0, NOW(), NOW(), NULL
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'REMERA BRISTOL OLA');

UPDATE products
SET description = 'Importado desde la tienda Bristol. Fuente: https://cervezabristol.mitiendanube.com/productos/remera-bristol-latita/',
    category = 'MERCHANDISING',
    subcategory = 'REMERA',
    beer_type = NULL,
    base_price = 45000.00,
    stock_quantity = 10,
    low_stock_threshold = 2,
    is_active = TRUE,
    is_featured = FALSE,
    discount_percentage = 0,
    total_reviews = 0,
    deleted_at = NULL,
    updated_at = NOW()
WHERE name = 'REMERA BRISTOL LATITA';

INSERT INTO products (id, name, description, category, subcategory, beer_type, base_price, discount_percentage, stock_quantity, low_stock_threshold, is_active, is_featured, total_reviews, created_at, updated_at, deleted_at)
SELECT '91000000-0000-0000-0000-000000000008', 'REMERA BRISTOL LATITA', 'Importado desde la tienda Bristol. Fuente: https://cervezabristol.mitiendanube.com/productos/remera-bristol-latita/', 'MERCHANDISING', 'REMERA', NULL, 45000.00, 0, 10, 2, TRUE, FALSE, 0, NOW(), NOW(), NULL
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'REMERA BRISTOL LATITA');

UPDATE product_variants
SET product_id = (SELECT id FROM products WHERE name = 'BUZO BRISTOL FEAT LUYS' LIMIT 1),
    variant_name = 'XL',
    size_ml = NULL,
    color = NULL,
    additional_price = 0,
    stock_quantity = 1,
    is_active = TRUE,
    updated_at = NOW()
WHERE sku = 'TN-285439096-1277876403';
INSERT INTO product_variants (id, product_id, sku, variant_name, size_ml, color, additional_price, stock_quantity, is_active, created_at, updated_at)
SELECT '92000000-0000-0000-0000-000000000001', (SELECT id FROM products WHERE name = 'BUZO BRISTOL FEAT LUYS' LIMIT 1), 'TN-285439096-1277876403', 'XL', NULL, NULL, 0, 1, TRUE, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM product_variants WHERE sku = 'TN-285439096-1277876403');

UPDATE product_variants
SET product_id = (SELECT id FROM products WHERE name = 'BUZO BRISTOL FEAT LUYS' LIMIT 1),
    variant_name = 'XXL',
    size_ml = NULL,
    color = NULL,
    additional_price = 0,
    stock_quantity = 0,
    is_active = TRUE,
    updated_at = NOW()
WHERE sku = 'TN-285439096-1277876407';
INSERT INTO product_variants (id, product_id, sku, variant_name, size_ml, color, additional_price, stock_quantity, is_active, created_at, updated_at)
SELECT '92000000-0000-0000-0000-000000000002', (SELECT id FROM products WHERE name = 'BUZO BRISTOL FEAT LUYS' LIMIT 1), 'TN-285439096-1277876407', 'XXL', NULL, NULL, 0, 0, TRUE, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM product_variants WHERE sku = 'TN-285439096-1277876407');

UPDATE product_variants
SET product_id = (SELECT id FROM products WHERE name = 'REMERA BRISTOL AMANECER' LIMIT 1),
    variant_name = 'S', size_ml = NULL, color = NULL, additional_price = 0, stock_quantity = 0, is_active = TRUE, updated_at = NOW()
WHERE sku = 'TN-285435789-1277864559';
INSERT INTO product_variants (id, product_id, sku, variant_name, size_ml, color, additional_price, stock_quantity, is_active, created_at, updated_at)
SELECT '92000000-0000-0000-0000-000000000003', (SELECT id FROM products WHERE name = 'REMERA BRISTOL AMANECER' LIMIT 1), 'TN-285435789-1277864559', 'S', NULL, NULL, 0, 0, TRUE, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM product_variants WHERE sku = 'TN-285435789-1277864559');

UPDATE product_variants
SET product_id = (SELECT id FROM products WHERE name = 'REMERA BRISTOL AMANECER' LIMIT 1),
    variant_name = 'L', size_ml = NULL, color = NULL, additional_price = 0, stock_quantity = 2, is_active = TRUE, updated_at = NOW()
WHERE sku = 'TN-285435789-1277864562';
INSERT INTO product_variants (id, product_id, sku, variant_name, size_ml, color, additional_price, stock_quantity, is_active, created_at, updated_at)
SELECT '92000000-0000-0000-0000-000000000004', (SELECT id FROM products WHERE name = 'REMERA BRISTOL AMANECER' LIMIT 1), 'TN-285435789-1277864562', 'L', NULL, NULL, 0, 2, TRUE, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM product_variants WHERE sku = 'TN-285435789-1277864562');

UPDATE product_variants
SET product_id = (SELECT id FROM products WHERE name = 'REMERA BRISTOL AMANECER' LIMIT 1),
    variant_name = 'XL', size_ml = NULL, color = NULL, additional_price = 0, stock_quantity = 6, is_active = TRUE, updated_at = NOW()
WHERE sku = 'TN-285435789-1277864564';
INSERT INTO product_variants (id, product_id, sku, variant_name, size_ml, color, additional_price, stock_quantity, is_active, created_at, updated_at)
SELECT '92000000-0000-0000-0000-000000000005', (SELECT id FROM products WHERE name = 'REMERA BRISTOL AMANECER' LIMIT 1), 'TN-285435789-1277864564', 'XL', NULL, NULL, 0, 6, TRUE, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM product_variants WHERE sku = 'TN-285435789-1277864564');

UPDATE product_variants
SET product_id = (SELECT id FROM products WHERE name = 'REMERA BRISTOL AMANECER' LIMIT 1),
    variant_name = 'XXL', size_ml = NULL, color = NULL, additional_price = 0, stock_quantity = 6, is_active = TRUE, updated_at = NOW()
WHERE sku = 'TN-285435789-1277864567';
INSERT INTO product_variants (id, product_id, sku, variant_name, size_ml, color, additional_price, stock_quantity, is_active, created_at, updated_at)
SELECT '92000000-0000-0000-0000-000000000006', (SELECT id FROM products WHERE name = 'REMERA BRISTOL AMANECER' LIMIT 1), 'TN-285435789-1277864567', 'XXL', NULL, NULL, 0, 6, TRUE, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM product_variants WHERE sku = 'TN-285435789-1277864567');

UPDATE product_variants
SET product_id = (SELECT id FROM products WHERE name = 'REMERA BRISTOL COPA' LIMIT 1),
    variant_name = 'S', size_ml = NULL, color = NULL, additional_price = 0, stock_quantity = 1, is_active = TRUE, updated_at = NOW()
WHERE sku = 'TN-285436373-1277867043';
INSERT INTO product_variants (id, product_id, sku, variant_name, size_ml, color, additional_price, stock_quantity, is_active, created_at, updated_at)
SELECT '92000000-0000-0000-0000-000000000007', (SELECT id FROM products WHERE name = 'REMERA BRISTOL COPA' LIMIT 1), 'TN-285436373-1277867043', 'S', NULL, NULL, 0, 1, TRUE, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM product_variants WHERE sku = 'TN-285436373-1277867043');

UPDATE product_variants
SET product_id = (SELECT id FROM products WHERE name = 'REMERA BRISTOL COPA' LIMIT 1),
    variant_name = 'L', size_ml = NULL, color = NULL, additional_price = 0, stock_quantity = 1, is_active = TRUE, updated_at = NOW()
WHERE sku = 'TN-285436373-1277867047';
INSERT INTO product_variants (id, product_id, sku, variant_name, size_ml, color, additional_price, stock_quantity, is_active, created_at, updated_at)
SELECT '92000000-0000-0000-0000-000000000008', (SELECT id FROM products WHERE name = 'REMERA BRISTOL COPA' LIMIT 1), 'TN-285436373-1277867047', 'L', NULL, NULL, 0, 1, TRUE, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM product_variants WHERE sku = 'TN-285436373-1277867047');

UPDATE product_variants
SET product_id = (SELECT id FROM products WHERE name = 'REMERA BRISTOL COPA' LIMIT 1),
    variant_name = 'XL', size_ml = NULL, color = NULL, additional_price = 0, stock_quantity = 6, is_active = TRUE, updated_at = NOW()
WHERE sku = 'TN-285436373-1277867051';
INSERT INTO product_variants (id, product_id, sku, variant_name, size_ml, color, additional_price, stock_quantity, is_active, created_at, updated_at)
SELECT '92000000-0000-0000-0000-000000000009', (SELECT id FROM products WHERE name = 'REMERA BRISTOL COPA' LIMIT 1), 'TN-285436373-1277867051', 'XL', NULL, NULL, 0, 6, TRUE, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM product_variants WHERE sku = 'TN-285436373-1277867051');

UPDATE product_variants
SET product_id = (SELECT id FROM products WHERE name = 'REMERA BRISTOL COPA' LIMIT 1),
    variant_name = 'XXL', size_ml = NULL, color = NULL, additional_price = 0, stock_quantity = 6, is_active = TRUE, updated_at = NOW()
WHERE sku = 'TN-285436373-1277867054';
INSERT INTO product_variants (id, product_id, sku, variant_name, size_ml, color, additional_price, stock_quantity, is_active, created_at, updated_at)
SELECT '92000000-0000-0000-0000-000000000010', (SELECT id FROM products WHERE name = 'REMERA BRISTOL COPA' LIMIT 1), 'TN-285436373-1277867054', 'XXL', NULL, NULL, 0, 6, TRUE, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM product_variants WHERE sku = 'TN-285436373-1277867054');

UPDATE product_variants
SET product_id = (SELECT id FROM products WHERE name = 'REMERA BRISTOL OLA' LIMIT 1),
    variant_name = 'S', size_ml = NULL, color = NULL, additional_price = 0, stock_quantity = 1, is_active = TRUE, updated_at = NOW()
WHERE sku = 'TN-285436045-1277865638';
INSERT INTO product_variants (id, product_id, sku, variant_name, size_ml, color, additional_price, stock_quantity, is_active, created_at, updated_at)
SELECT '92000000-0000-0000-0000-000000000011', (SELECT id FROM products WHERE name = 'REMERA BRISTOL OLA' LIMIT 1), 'TN-285436045-1277865638', 'S', NULL, NULL, 0, 1, TRUE, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM product_variants WHERE sku = 'TN-285436045-1277865638');

UPDATE product_variants
SET product_id = (SELECT id FROM products WHERE name = 'REMERA BRISTOL OLA' LIMIT 1),
    variant_name = 'L', size_ml = NULL, color = NULL, additional_price = 0, stock_quantity = 3, is_active = TRUE, updated_at = NOW()
WHERE sku = 'TN-285436045-1277865641';
INSERT INTO product_variants (id, product_id, sku, variant_name, size_ml, color, additional_price, stock_quantity, is_active, created_at, updated_at)
SELECT '92000000-0000-0000-0000-000000000012', (SELECT id FROM products WHERE name = 'REMERA BRISTOL OLA' LIMIT 1), 'TN-285436045-1277865641', 'L', NULL, NULL, 0, 3, TRUE, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM product_variants WHERE sku = 'TN-285436045-1277865641');

UPDATE product_variants
SET product_id = (SELECT id FROM products WHERE name = 'REMERA BRISTOL OLA' LIMIT 1),
    variant_name = 'XL', size_ml = NULL, color = NULL, additional_price = 0, stock_quantity = 5, is_active = TRUE, updated_at = NOW()
WHERE sku = 'TN-285436045-1277865643';
INSERT INTO product_variants (id, product_id, sku, variant_name, size_ml, color, additional_price, stock_quantity, is_active, created_at, updated_at)
SELECT '92000000-0000-0000-0000-000000000013', (SELECT id FROM products WHERE name = 'REMERA BRISTOL OLA' LIMIT 1), 'TN-285436045-1277865643', 'XL', NULL, NULL, 0, 5, TRUE, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM product_variants WHERE sku = 'TN-285436045-1277865643');

UPDATE product_variants
SET product_id = (SELECT id FROM products WHERE name = 'REMERA BRISTOL OLA' LIMIT 1),
    variant_name = 'XXL', size_ml = NULL, color = NULL, additional_price = 0, stock_quantity = 5, is_active = TRUE, updated_at = NOW()
WHERE sku = 'TN-285436045-1277865644';
INSERT INTO product_variants (id, product_id, sku, variant_name, size_ml, color, additional_price, stock_quantity, is_active, created_at, updated_at)
SELECT '92000000-0000-0000-0000-000000000014', (SELECT id FROM products WHERE name = 'REMERA BRISTOL OLA' LIMIT 1), 'TN-285436045-1277865644', 'XXL', NULL, NULL, 0, 5, TRUE, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM product_variants WHERE sku = 'TN-285436045-1277865644');

UPDATE product_variants
SET product_id = (SELECT id FROM products WHERE name = 'REMERA BRISTOL LATITA' LIMIT 1),
    variant_name = 'L', size_ml = NULL, color = NULL, additional_price = 0, stock_quantity = 1, is_active = TRUE, updated_at = NOW()
WHERE sku = 'TN-285435296-1277862882';
INSERT INTO product_variants (id, product_id, sku, variant_name, size_ml, color, additional_price, stock_quantity, is_active, created_at, updated_at)
SELECT '92000000-0000-0000-0000-000000000015', (SELECT id FROM products WHERE name = 'REMERA BRISTOL LATITA' LIMIT 1), 'TN-285435296-1277862882', 'L', NULL, NULL, 0, 1, TRUE, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM product_variants WHERE sku = 'TN-285435296-1277862882');

UPDATE product_variants
SET product_id = (SELECT id FROM products WHERE name = 'REMERA BRISTOL LATITA' LIMIT 1),
    variant_name = 'XL', size_ml = NULL, color = NULL, additional_price = 0, stock_quantity = 4, is_active = TRUE, updated_at = NOW()
WHERE sku = 'TN-285435296-1277862886';
INSERT INTO product_variants (id, product_id, sku, variant_name, size_ml, color, additional_price, stock_quantity, is_active, created_at, updated_at)
SELECT '92000000-0000-0000-0000-000000000016', (SELECT id FROM products WHERE name = 'REMERA BRISTOL LATITA' LIMIT 1), 'TN-285435296-1277862886', 'XL', NULL, NULL, 0, 4, TRUE, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM product_variants WHERE sku = 'TN-285435296-1277862886');

UPDATE product_variants
SET product_id = (SELECT id FROM products WHERE name = 'REMERA BRISTOL LATITA' LIMIT 1),
    variant_name = 'XXL', size_ml = NULL, color = NULL, additional_price = 0, stock_quantity = 4, is_active = TRUE, updated_at = NOW()
WHERE sku = 'TN-285435296-1277862889';
INSERT INTO product_variants (id, product_id, sku, variant_name, size_ml, color, additional_price, stock_quantity, is_active, created_at, updated_at)
SELECT '92000000-0000-0000-0000-000000000017', (SELECT id FROM products WHERE name = 'REMERA BRISTOL LATITA' LIMIT 1), 'TN-285435296-1277862889', 'XXL', NULL, NULL, 0, 4, TRUE, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM product_variants WHERE sku = 'TN-285435296-1277862889');

UPDATE product_variants
SET product_id = (SELECT id FROM products WHERE name = 'REMERA BRISTOL LATITA' LIMIT 1),
    variant_name = 'S', size_ml = NULL, color = NULL, additional_price = 0, stock_quantity = 1, is_active = TRUE, updated_at = NOW()
WHERE sku = 'TN-285435296-1277862892';
INSERT INTO product_variants (id, product_id, sku, variant_name, size_ml, color, additional_price, stock_quantity, is_active, created_at, updated_at)
SELECT '92000000-0000-0000-0000-000000000018', (SELECT id FROM products WHERE name = 'REMERA BRISTOL LATITA' LIMIT 1), 'TN-285435296-1277862892', 'S', NULL, NULL, 0, 1, TRUE, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM product_variants WHERE sku = 'TN-285435296-1277862892');

COMMIT;