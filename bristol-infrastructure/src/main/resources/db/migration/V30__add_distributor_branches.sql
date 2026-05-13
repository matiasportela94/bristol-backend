-- ============================================================================
-- V30: Distributor Branches
-- Adds support for distributors with multiple branches (franchises/locations).
-- Each branch can have its own user who sees only that branch's data.
-- ============================================================================

-- New table for distributor branches
CREATE TABLE distributor_branches (
    id          UUID         NOT NULL PRIMARY KEY,
    distributor_id UUID      NOT NULL REFERENCES distributors(id),
    name        VARCHAR(150) NOT NULL,
    address     VARCHAR(255),
    city        VARCHAR(100),
    province    VARCHAR(100),
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_distributor_branches_distributor_id ON distributor_branches(distributor_id);

-- Add distributor/branch context to users
ALTER TABLE users
    ADD COLUMN distributor_id UUID REFERENCES distributors(id),
    ADD COLUMN branch_id      UUID REFERENCES distributor_branches(id);

-- role column is VARCHAR(20) — new values work automatically with JPA @Enumerated(STRING)
