package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.dto.store.ProductCategory;
import ru.yandex.practicum.dto.store.ProductState;
import ru.yandex.practicum.dto.store.QuantityState;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "products")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID productId;

    @Column(name = "name", length = 200, nullable = false)
    String productName;

    @Column(name = "description", length = 2000, columnDefinition = "TEXT", nullable = false)
    String description;

    @Column(name = "image_src", length = 1000, columnDefinition = "TEXT")
    String imageSrc;

    @Enumerated(EnumType.STRING)
    @Column(name = "quantity_state", length = 50, nullable = false)
    QuantityState quantityState;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_state", length = 50, nullable = false)
    ProductState productState;

    @Column(name = "rating")
    Double rating;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 50, nullable = false)
    ProductCategory productCategory;

    @Column(name = "price")
    BigDecimal price;
}