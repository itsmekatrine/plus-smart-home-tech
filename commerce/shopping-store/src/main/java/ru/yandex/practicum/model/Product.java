package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.dto.store.ProductCategory;
import ru.yandex.practicum.dto.store.ProductState;
import ru.yandex.practicum.dto.store.QuantityState;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "products")
public class Product {
    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID productId;

    @Column(name = "name", nullable = false)
    private String productName;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "image_src", columnDefinition = "TEXT")
    private String imageSrc;

    @Enumerated(EnumType.STRING)
    @Column(name = "quantity_state", length = 50, nullable = false)
    private QuantityState quantityState;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_state", length = 50, nullable = false)
    private ProductState productState;

    @Column(name = "rating")
    private Double rating;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 50, nullable = false)
    private ProductCategory productCategory;

    @Column(name = "price")
    private Double price;
}