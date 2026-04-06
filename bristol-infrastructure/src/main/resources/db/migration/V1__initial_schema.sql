-- ============================================
-- BRISTOL PLATFORM - INITIAL SCHEMA
-- ============================================
-- This migration creates the current application schema from scratch.
-- Enums are stored as VARCHAR columns to keep the schema aligned with the
-- JPA model without depending on PostgreSQL enum types.

CREATE TABLE IF NOT EXISTS delivery_zones (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255),
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone VARCHAR(20),
    date_of_birth DATE,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    is_distributor BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS distributors (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    cuit VARCHAR(50) NOT NULL,
    business_name VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    total_spent DECIMAL(10, 2) NOT NULL DEFAULT 0,
    total_beers_purchased INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS user_addresses (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    province VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20),
    delivery_zone_id UUID NOT NULL,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS products (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL,
    subcategory VARCHAR(50),
    beer_type VARCHAR(50),
    brewing_method VARCHAR(50),
    abv DECIMAL(3, 1),
    ibu DECIMAL(4, 1),
    srm DECIMAL(4, 1),
    flavor VARCHAR(50),
    bitterness VARCHAR(50),
    base_price DECIMAL(10, 2) NOT NULL,
    discount_percentage DECIMAL(5, 2),
    stock_quantity INTEGER NOT NULL DEFAULT 0,
    low_stock_threshold INTEGER,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_featured BOOLEAN NOT NULL DEFAULT FALSE,
    average_rating DECIMAL(2, 1),
    total_reviews BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS product_variants (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    sku VARCHAR(100) UNIQUE,
    variant_name VARCHAR(100) NOT NULL,
    size_ml INTEGER,
    color VARCHAR(50),
    additional_price DECIMAL(10, 2) NOT NULL DEFAULT 0,
    stock_quantity INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS product_images (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    image_url TEXT NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS product_price_history (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    old_price DECIMAL(10, 2) NOT NULL,
    new_price DECIMAL(10, 2) NOT NULL,
    changed_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS product_reviews (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    user_id UUID NOT NULL,
    rating INTEGER NOT NULL,
    comment TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS coupons (
    id UUID PRIMARY KEY,
    code VARCHAR(50) UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    method VARCHAR(20) NOT NULL,
    discount_type VARCHAR(20) NOT NULL,
    value_type VARCHAR(20) NOT NULL,
    value DECIMAL(10, 2) NOT NULL,
    schedule_type VARCHAR(20) NOT NULL,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    minimum_requirement_type VARCHAR(30),
    minimum_purchase_amount DECIMAL(10, 2),
    minimum_item_quantity INTEGER,
    usage_limit_total INTEGER,
    usage_limit_per_customer INTEGER,
    times_used INTEGER NOT NULL DEFAULT 0,
    is_customer_specific BOOLEAN NOT NULL DEFAULT FALSE,
    applicable_product_category VARCHAR(20),
    applicable_product_subcategory VARCHAR(30),
    combine_with_product_discounts BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    order_status VARCHAR(30) NOT NULL DEFAULT 'PENDING_PAYMENT',
    distributor_id UUID,
    order_date TIMESTAMP NOT NULL,
    shipping_address_line1 VARCHAR(255) NOT NULL,
    shipping_address_line2 VARCHAR(255),
    shipping_city VARCHAR(100) NOT NULL,
    shipping_province VARCHAR(100) NOT NULL,
    shipping_postal_code VARCHAR(20),
    delivery_zone_id UUID NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    order_discount_coupon_id UUID,
    order_discount_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
    shipping_cost DECIMAL(10, 2) NOT NULL DEFAULT 0,
    shipping_discount_coupon_id UUID,
    shipping_discount_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
    total DECIMAL(10, 2) NOT NULL,
    stock_updated BOOLEAN NOT NULL DEFAULT FALSE,
    notes TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS order_items (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    product_variant_id UUID,
    product_name VARCHAR(255) NOT NULL,
    product_type VARCHAR(20) NOT NULL,
    beer_type VARCHAR(50),
    quantity INTEGER NOT NULL,
    price_per_unit DECIMAL(10, 2) NOT NULL,
    item_discount_coupon_id UUID,
    item_discount_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
    subtotal DECIMAL(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS delivery_calendars (
    id UUID PRIMARY KEY,
    delivery_zone_id UUID NOT NULL,
    delivery_date DATE NOT NULL,
    capacity INTEGER NOT NULL,
    current_bookings INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS deliveries (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL UNIQUE,
    delivery_calendar_id UUID NOT NULL,
    delivery_status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    scheduled_date DATE NOT NULL,
    actual_delivery_date DATE,
    driver_notes TEXT,
    customer_notes TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_distributors_user_id ON distributors(user_id);
CREATE INDEX IF NOT EXISTS idx_distributors_cuit ON distributors(cuit);
CREATE INDEX IF NOT EXISTS idx_user_addresses_user_id ON user_addresses(user_id);
CREATE INDEX IF NOT EXISTS idx_user_addresses_delivery_zone_id ON user_addresses(delivery_zone_id);
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);
CREATE INDEX IF NOT EXISTS idx_products_beer_type ON products(beer_type);
CREATE INDEX IF NOT EXISTS idx_products_deleted_at ON products(deleted_at);
CREATE INDEX IF NOT EXISTS idx_product_variants_product_id ON product_variants(product_id);
CREATE INDEX IF NOT EXISTS idx_product_images_product_id ON product_images(product_id);
CREATE INDEX IF NOT EXISTS idx_product_price_history_product_id ON product_price_history(product_id);
CREATE INDEX IF NOT EXISTS idx_product_reviews_product_id ON product_reviews(product_id);
CREATE INDEX IF NOT EXISTS idx_product_reviews_user_id ON product_reviews(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_order_status ON orders(order_status);
CREATE INDEX IF NOT EXISTS idx_orders_distributor_id ON orders(distributor_id);
CREATE INDEX IF NOT EXISTS idx_orders_delivery_zone_id ON orders(delivery_zone_id);
CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_order_items_product_id ON order_items(product_id);
CREATE INDEX IF NOT EXISTS idx_order_items_product_variant_id ON order_items(product_variant_id);
CREATE INDEX IF NOT EXISTS idx_delivery_calendars_zone_date ON delivery_calendars(delivery_zone_id, delivery_date);
CREATE INDEX IF NOT EXISTS idx_deliveries_order_id ON deliveries(order_id);
