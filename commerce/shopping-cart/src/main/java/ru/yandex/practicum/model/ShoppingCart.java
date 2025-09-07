package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "cart")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShoppingCart {
    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID shoppingCartId;

    @Column(name = "username", nullable = false)
    String username;

    @ElementCollection
    @Column(name = "quantity")
    @MapKeyColumn(name = "product_id")
    @CollectionTable(name="cart_products", joinColumns = @JoinColumn(name = "cart_id"))
    Map<UUID, Long> products = new HashMap<>();

    @Column(name = "is_active")
    Boolean isActive = true;
}
