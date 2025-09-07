package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "warehouse_product")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseProduct {
    @Id
    @Column(name = "product_id", nullable = false, unique = true)
    UUID productId;

    @Embedded
    Dimension dimension;

    @Column(name = "weight", nullable = false)
    Double weight;

    @Column(name = "fragile", nullable = false)
    boolean fragile;

    @Column(name = "quantity")
    long quantity;
}