CREATE TABLE IF NOT EXISTS orders (
    order_id         UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    shopping_cart_id UUID NOT NULL,
    payment_id       UUID,
    delivery_id      UUID,
    state            VARCHAR(20) NOT NULL,
    delivery_weight  DOUBLE PRECISION,
    delivery_volume  DOUBLE PRECISION,
    fragile          BOOLEAN NOT NULL DEFAULT FALSE,
    total_price      NUMERIC(19,2),
    delivery_price   NUMERIC(19,2),
    product_price    NUMERIC(19,2)
);

CREATE TABLE IF NOT EXISTS order_products (
    order_id   UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity   BIGINT,
    CONSTRAINT order_products_pk PRIMARY KEY (order_id, product_id),
    CONSTRAINT order_products_order_fk FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
);