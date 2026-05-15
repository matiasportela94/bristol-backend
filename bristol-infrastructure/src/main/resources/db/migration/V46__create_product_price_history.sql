CREATE TABLE product_price_history (
    id          UUID           PRIMARY KEY,
    product_id  UUID           NOT NULL REFERENCES products(id),
    old_price   DECIMAL(10, 2),
    new_price   DECIMAL(10, 2),
    changed_at  TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_product_price_history_product_id ON product_price_history(product_id);
