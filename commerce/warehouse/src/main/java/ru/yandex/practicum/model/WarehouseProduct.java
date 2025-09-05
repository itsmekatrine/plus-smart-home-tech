package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "warehouse_product")
public class WarehouseProduct {
    @Id
    @Column(name = "product_id", nullable = false, unique = true)
    private UUID productId;

    @Embedded
    private Dimension dimension;

    @Column(name = "weight", nullable = false)
    private Double weight;

    @Column(name = "fragile", nullable = false)
    private boolean fragile;

    @Column(name = "quantity")
    private long quantity;
}