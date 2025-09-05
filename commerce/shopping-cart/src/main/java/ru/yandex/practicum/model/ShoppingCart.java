package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "cart")
public class ShoppingCart {
    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID shoppingCartId;

    @Column(name = "username", nullable = false)
    private String username;

    @ElementCollection
    @Column(name = "quantity")
    @MapKeyColumn(name = "product_id")
    @CollectionTable(name="cart_products", joinColumns = @JoinColumn(name = "cart_id"))
    private Map<UUID, Long> products = new HashMap<>();

    @Column(name = "is_active")
    private Boolean isActive = true;
}
