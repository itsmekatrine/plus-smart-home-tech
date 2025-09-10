CREATE TABLE IF NOT EXISTS cart (
    cart_id          UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    username        varchar(200) NOT NULL,
    is_active boolean DEFAULT true
);

CREATE TABLE IF NOT EXISTS cart_products (
    cart_id          UUID NOT NULL,
    product_id       UUID NOT NULL,
    quantity         BIGINT,
    CONSTRAINT cart_products_pk PRIMARY KEY (cart_id, product_id),
    CONSTRAINT cart_products_cart_fk FOREIGN KEY (cart_id) REFERENCES cart(cart_id) ON DELETE CASCADE
);