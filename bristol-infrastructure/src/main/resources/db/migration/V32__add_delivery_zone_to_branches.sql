-- V32: Add shipping address fields to distributor_branches
-- Branches are now the shipping address bearer — orders are received at branches,
-- not at the distributor level. delivery_zone_id is required for new branches.

ALTER TABLE distributor_branches
    ADD COLUMN IF NOT EXISTS delivery_zone_id UUID REFERENCES delivery_zones(id),
    ADD COLUMN IF NOT EXISTS codigo_postal    VARCHAR(20);

CREATE INDEX IF NOT EXISTS idx_distributor_branches_delivery_zone ON distributor_branches(delivery_zone_id);
