CREATE TABLE shopping_carts (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    subtotal DECIMAL(10, 2) NOT NULL DEFAULT 0,
    total_items INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_shopping_carts_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE shopping_cart_items (
    id UUID PRIMARY KEY,
    cart_id UUID NOT NULL,
    product_id UUID NOT NULL,
    product_variant_id UUID NULL,
    product_name VARCHAR(255) NOT NULL,
    product_type VARCHAR(32) NOT NULL,
    beer_type VARCHAR(32) NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_shopping_cart_items_cart FOREIGN KEY (cart_id) REFERENCES shopping_carts(id) ON DELETE CASCADE,
    CONSTRAINT fk_shopping_cart_items_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_shopping_cart_items_variant FOREIGN KEY (product_variant_id) REFERENCES product_variants(id)
);

CREATE INDEX idx_shopping_cart_items_cart_id ON shopping_cart_items(cart_id);
CREATE INDEX idx_shopping_cart_items_product_id ON shopping_cart_items(product_id);
