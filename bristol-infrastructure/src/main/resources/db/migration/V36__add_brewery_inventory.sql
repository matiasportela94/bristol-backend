-- Brewery inventory: total cans available per beer style
CREATE TABLE brewery_inventory (
    id          UUID        NOT NULL PRIMARY KEY,
    beer_style_id UUID      NOT NULL UNIQUE REFERENCES beer_styles(id),
    total_cans  INTEGER     NOT NULL DEFAULT 0 CHECK (total_cans >= 0),
    created_at  TIMESTAMPTZ NOT NULL,
    updated_at  TIMESTAMPTZ NOT NULL
);

-- Brewing batches: history of cans added to inventory
CREATE TABLE brewery_batches (
    id              UUID        NOT NULL PRIMARY KEY,
    beer_style_id   UUID        NOT NULL REFERENCES beer_styles(id),
    cans_produced   INTEGER     NOT NULL CHECK (cans_produced > 0),
    notes           TEXT,
    created_at      TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_brewery_batches_style ON brewery_batches(beer_style_id);

-- How many cans a beer product contains (1=single can, 6=six-pack, 24=case, etc.)
ALTER TABLE beer_products ADD COLUMN cans_per_unit INTEGER NOT NULL DEFAULT 1 CHECK (cans_per_unit > 0);
