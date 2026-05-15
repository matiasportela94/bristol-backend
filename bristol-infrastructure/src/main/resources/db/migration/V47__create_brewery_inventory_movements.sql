CREATE TABLE brewery_inventory_movements (
    id              UUID            PRIMARY KEY,
    beer_style_id   UUID            NOT NULL REFERENCES beer_styles(id),
    type            VARCHAR(30)     NOT NULL,
    cans_delta      INTEGER         NOT NULL,
    cans_before     INTEGER         NOT NULL,
    cans_after      INTEGER         NOT NULL,
    reference_id    UUID,
    reference_type  VARCHAR(20),
    notes           TEXT,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_inventory_movements_style_id ON brewery_inventory_movements(beer_style_id);
CREATE INDEX idx_inventory_movements_created_at ON brewery_inventory_movements(created_at DESC);
