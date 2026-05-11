-- ============================================
-- BRISTOL PLATFORM - CATALOG TABLES
-- ============================================
-- Creates catalog tables for beer styles, merch types, and special types.
-- This allows dynamic management of product categories without code changes.

-- Beer Styles Catalog
CREATE TABLE IF NOT EXISTS beer_styles (
    id UUID PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL,  -- ALE, LAGER, STOUT, WHEAT, SOUR, SPECIALTY
    active BOOLEAN NOT NULL DEFAULT TRUE,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_beer_styles_active ON beer_styles(active);
CREATE INDEX idx_beer_styles_category ON beer_styles(category);
CREATE INDEX idx_beer_styles_display_order ON beer_styles(display_order);

-- Merch Types Catalog
CREATE TABLE IF NOT EXISTS merch_types (
    id UUID PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL,  -- CLOTHING, GLASSWARE, ACCESSORIES, OTHER
    active BOOLEAN NOT NULL DEFAULT TRUE,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_merch_types_active ON merch_types(active);
CREATE INDEX idx_merch_types_category ON merch_types(category);
CREATE INDEX idx_merch_types_display_order ON merch_types(display_order);

-- Special Types Catalog
CREATE TABLE IF NOT EXISTS special_types (
    id UUID PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    requires_quote BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_special_types_active ON special_types(active);
CREATE INDEX idx_special_types_display_order ON special_types(display_order);
