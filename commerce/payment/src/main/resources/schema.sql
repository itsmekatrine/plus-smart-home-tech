CREATE TABLE IF NOT EXISTS payments (
    payment_id     UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    order_id       UUID NOT NULL,
    total_payment  NUMERIC(19,2),
    delivery_total NUMERIC(19,2),
    fee_total      NUMERIC(19,2),
    payment_status VARCHAR(20) NOT NULL
);