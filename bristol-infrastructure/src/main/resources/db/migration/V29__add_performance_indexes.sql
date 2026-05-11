-- ============================================================================
-- Performance Optimization: Strategic Database Indexes
-- Version: V29
-- Description: Adds indexes for frequently queried columns and filter operations
-- Note: Simplified to only include tables/columns that exist in current schema
-- ============================================================================

-- ============================================================================
-- ORDERS TABLE INDEXES
-- ============================================================================
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(order_status);
CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_distributor_id ON orders(distributor_id) WHERE distributor_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_orders_order_date ON orders(order_date DESC);
CREATE INDEX IF NOT EXISTS idx_orders_user_status ON orders(user_id, order_status);
CREATE INDEX IF NOT EXISTS idx_orders_status_date ON orders(order_status, order_date DESC);
CREATE INDEX IF NOT EXISTS idx_orders_date_range ON orders(order_date DESC, order_status);

-- ============================================================================
-- ORDER ITEMS TABLE INDEXES
-- ============================================================================
CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_order_items_product_id ON order_items(product_id);

-- ============================================================================
-- PRODUCTS TABLE INDEXES
-- ============================================================================
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);
CREATE INDEX IF NOT EXISTS idx_products_featured ON products(is_featured) WHERE is_featured = true;
CREATE INDEX IF NOT EXISTS idx_products_active ON products(is_active) WHERE is_active = true;

-- ============================================================================
-- USERS TABLE INDEXES
-- ============================================================================
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_email_unique ON users(LOWER(email));
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

-- ============================================================================
-- DISTRIBUTORS TABLE INDEXES
-- ============================================================================
CREATE UNIQUE INDEX IF NOT EXISTS idx_distributors_user_id ON distributors(user_id);
CREATE INDEX IF NOT EXISTS idx_distributors_status ON distributors(status);
CREATE INDEX IF NOT EXISTS idx_distributors_zone ON distributors(delivery_zone_id) WHERE delivery_zone_id IS NOT NULL;

-- ============================================================================
-- DELIVERIES TABLE INDEXES
-- ============================================================================
CREATE INDEX IF NOT EXISTS idx_deliveries_order_id ON deliveries(order_id);
CREATE INDEX IF NOT EXISTS idx_deliveries_status ON deliveries(delivery_status);
CREATE INDEX IF NOT EXISTS idx_deliveries_scheduled_date ON deliveries(scheduled_date DESC);

-- ============================================================================
-- DELIVERY ZONES TABLE INDEXES
-- ============================================================================
CREATE INDEX IF NOT EXISTS idx_delivery_zones_active ON delivery_zones(is_active) WHERE is_active = true;
CREATE INDEX IF NOT EXISTS idx_delivery_zones_name ON delivery_zones(name);

-- ============================================================================
-- COUPONS TABLE INDEXES
-- ============================================================================
CREATE UNIQUE INDEX IF NOT EXISTS idx_coupons_code_unique ON coupons(UPPER(code));
CREATE INDEX IF NOT EXISTS idx_coupons_status ON coupons(status);
CREATE INDEX IF NOT EXISTS idx_coupons_discount_type ON coupons(discount_type);

-- ============================================================================
-- USER ADDRESSES TABLE INDEXES
-- ============================================================================
CREATE INDEX IF NOT EXISTS idx_user_addresses_user_id ON user_addresses(user_id);
CREATE INDEX IF NOT EXISTS idx_user_addresses_zone_id ON user_addresses(delivery_zone_id);
CREATE INDEX IF NOT EXISTS idx_user_addresses_default ON user_addresses(user_id, is_default) WHERE is_default = true;

-- ============================================================================
-- PERFORMANCE NOTES
-- ============================================================================
-- 1. All indexes use IF NOT EXISTS for idempotency
-- 2. Partial indexes (with WHERE clauses) reduce index size
-- 3. Composite indexes ordered with most selective columns first
-- 4. DESC indexes on date columns for recent-first queries
-- 5. Unique indexes also serve as lookup indexes
-- 6. LOWER() and UPPER() functions ensure case-insensitive uniqueness
-- ============================================================================
