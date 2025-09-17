package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.dto.order.OrderState;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "orders")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id", unique = true)
    UUID orderId;

    @Column(name = "shopping_cart_id")
    UUID shoppingCartId;

    @ElementCollection
    @Column(name = "quantity")
    @MapKeyColumn(name = "product_id")
    @CollectionTable(name = "order_products", joinColumns = @JoinColumn(name = "order_id"))
    Map<UUID, Long> products = new HashMap<>();

    @Column(name = "payment_id")
    UUID paymentId;

    @Column(name = "delivery_id")
    UUID deliveryId;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    OrderState state;

    @Column(name = "delivery_weight")
    Double deliveryWeight;

    @Column(name = "delivery_volume")
    Double deliveryVolume;

    @Column(name = "fragile")
    boolean fragile;

    @Column(name = "total_price")
    BigDecimal totalPrice;

    @Column(name = "delivery_price")
    BigDecimal deliveryPrice;

    @Column(name = "product_price")
    BigDecimal productPrice;
}
