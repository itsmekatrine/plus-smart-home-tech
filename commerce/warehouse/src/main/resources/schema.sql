CREATE TABLE IF NOT EXISTS warehouse_product (
    product_id       UUID PRIMARY KEY,
    fragile          boolean,
    width            DOUBLE PRECISION,
    height           DOUBLE PRECISION,
    depth            DOUBLE PRECISION,
    weight           DOUBLE PRECISION,
    quantity         BIGINT
);

CREATE TABLE IF NOT EXISTS order_booking (
    order_id    UUID PRIMARY KEY,
    delivery_id UUID
);

CREATE TABLE IF NOT EXISTS booking_products (
    order_id   UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity   BIGINT,
    CONSTRAINT booking_products_pk PRIMARY KEY (order_id, product_id),
    CONSTRAINT booking_products_booking_fk FOREIGN KEY (order_id) REFERENCES order_booking(order_id) ON DELETE CASCADE
);