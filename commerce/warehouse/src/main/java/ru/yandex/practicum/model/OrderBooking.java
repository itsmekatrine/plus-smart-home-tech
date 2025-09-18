package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "order_booking")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderBooking {
    @Id
    @Column(name = "order_id", nullable = false)
    UUID orderId;

    @Column(name = "delivery_id")
    UUID deliveryId;

    @ElementCollection
    @Column(name = "quantity")
    @MapKeyColumn(name = "product_id")
    @CollectionTable(name = "booking_products", joinColumns = @JoinColumn(name = "order_id"))
    Map<UUID, Long> products;
}
