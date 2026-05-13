-- V31: Distributor branches + branch_id on orders
-- Written defensively with IF NOT EXISTS because V30 in this DB
-- was a different migration ("add total orders to distributors")
-- that did not create the distributor_branches table.

CREATE TABLE IF NOT EXISTS distributor_branches (
    id             UUID         NOT NULL PRIMARY KEY,
    distributor_id UUID         NOT NULL REFERENCES distributors(id),
    name           VARCHAR(150) NOT NULL,
    address        VARCHAR(255),
    city           VARCHAR(100),
    province       VARCHAR(100),
    active         BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_distributor_branches_distributor_id ON distributor_branches(distributor_id);

-- Add distributor/branch context to users (guard in case already added)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'users' AND column_name = 'distributor_id'
    ) THEN
        ALTER TABLE users ADD COLUMN distributor_id UUID REFERENCES distributors(id);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'users' AND column_name = 'branch_id'
    ) THEN
        ALTER TABLE users ADD COLUMN branch_id UUID REFERENCES distributor_branches(id);
    END IF;
END $$;

-- Add branch_id to orders
ALTER TABLE orders ADD COLUMN IF NOT EXISTS branch_id UUID REFERENCES distributor_branches(id);

CREATE INDEX IF NOT EXISTS idx_orders_branch_id ON orders(branch_id);
