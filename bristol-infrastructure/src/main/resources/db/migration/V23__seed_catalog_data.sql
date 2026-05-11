-- ============================================
-- BRISTOL PLATFORM - CATALOG SEED DATA
-- ============================================
-- Inserts initial catalog data for beer styles, merch types, and special types.
-- These can be modified through the admin panel after initial setup.

-- Beer Styles
INSERT INTO beer_styles (id, code, name, description, category, active, display_order, created_at, updated_at) VALUES
    (gen_random_uuid(), 'IPA', 'India Pale Ale', 'Cerveza con alto contenido de lúpulo y amargor pronunciado', 'ALE', TRUE, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'HAZY_IPA', 'Hazy IPA', 'IPA turbia con sabor frutal y menos amargor', 'ALE', TRUE, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'WEST_COAST_IPA', 'West Coast IPA', 'IPA americana clásica, amarga y cristalina', 'ALE', TRUE, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'APA', 'American Pale Ale', 'Ale americana con balance entre malta y lúpulo', 'ALE', TRUE, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'BLONDE_ALE', 'Blonde Ale', 'Ale rubia ligera y refrescante', 'ALE', TRUE, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'AMBER_ALE', 'Amber Ale', 'Ale ámbar con notas de caramelo', 'ALE', TRUE, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'LAGER', 'Lager', 'Cerveza de baja fermentación, suave y refrescante', 'LAGER', TRUE, 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'PILSNER', 'Pilsner', 'Lager checa dorada con amargor noble', 'LAGER', TRUE, 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'VIENNA_LAGER', 'Vienna Lager', 'Lager ámbar con sabor maltoso', 'LAGER', TRUE, 9, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'STOUT', 'Stout', 'Cerveza oscura con notas de café y chocolate', 'STOUT', TRUE, 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'OATMEAL_STOUT', 'Oatmeal Stout', 'Stout con avena, cremosa y suave', 'STOUT', TRUE, 11, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'IMPERIAL_STOUT', 'Imperial Stout', 'Stout fuerte con alto contenido alcohólico', 'STOUT', TRUE, 12, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'PORTER', 'Porter', 'Cerveza oscura más ligera que la Stout', 'STOUT', TRUE, 13, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'WHEAT_BEER', 'Wheat Beer', 'Cerveza de trigo ligera y refrescante', 'WHEAT', TRUE, 14, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'HEFEWEIZEN', 'Hefeweizen', 'Cerveza de trigo alemana con notas de plátano y clavo', 'WHEAT', TRUE, 15, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'WITBIER', 'Witbier', 'Cerveza de trigo belga con cilantro y cáscara de naranja', 'WHEAT', TRUE, 16, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'SOUR_ALE', 'Sour Ale', 'Cerveza ácida con fermentación salvaje', 'SOUR', TRUE, 17, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'GOSE', 'Gose', 'Sour alemana con sal y cilantro', 'SOUR', TRUE, 18, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'BERLINER_WEISSE', 'Berliner Weisse', 'Sour ligera y refrescante', 'SOUR', TRUE, 19, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'FRUIT_BEER', 'Fruit Beer', 'Cerveza con frutas', 'SPECIALTY', TRUE, 20, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Merch Types
INSERT INTO merch_types (id, code, name, description, category, active, display_order, created_at, updated_at) VALUES
    (gen_random_uuid(), 'REMERA', 'Remera', 'Remeras de algodón con diseños de Bristol', 'CLOTHING', TRUE, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'BUZO', 'Buzo', 'Buzos y hoodies', 'CLOTHING', TRUE, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'GORRA', 'Gorra', 'Gorras y sombreros', 'CLOTHING', TRUE, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'CAMPERA', 'Campera', 'Camperas y chaquetas', 'CLOTHING', TRUE, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'VASO', 'Vaso', 'Vasos cerveceros', 'GLASSWARE', TRUE, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'JARRA', 'Jarra', 'Jarras de vidrio', 'GLASSWARE', TRUE, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'COPA', 'Copa', 'Copas cerveceras', 'GLASSWARE', TRUE, 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'POSAVASOS', 'Posavasos', 'Posavasos con diseños de Bristol', 'ACCESSORIES', TRUE, 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'ABRIDOR', 'Abridor', 'Abridores de botellas', 'ACCESSORIES', TRUE, 9, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'LLAVERO', 'Llavero', 'Llaveros con logo de Bristol', 'ACCESSORIES', TRUE, 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Special Types
INSERT INTO special_types (id, code, name, description, requires_quote, active, display_order, created_at, updated_at) VALUES
    (gen_random_uuid(), 'PLOTEO', 'Ploteo de Vehículo', 'Servicio de ploteo para delivery', TRUE, TRUE, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'EVENTO', 'Evento Privado', 'Servicio de cerveza para eventos', TRUE, TRUE, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'CATERING', 'Catering', 'Servicio de catering con cervezas', TRUE, TRUE, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'BARRIL_CHOP', 'Barril de Chopp', 'Alquiler de barril de chopp para eventos', TRUE, TRUE, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
