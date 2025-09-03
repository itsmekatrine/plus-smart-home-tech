package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.cart.CartDto;
import ru.yandex.practicum.dto.cart.UpdateProductQuantityRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartService {

    CartDto getShoppingCart(String userName);

    CartDto addProductToShoppingCart(String username, Map<UUID, Long> products);

    CartDto removeFromShoppingCart(String userName, List<UUID> products);

    CartDto changeProductQuantity(String userName, UpdateProductQuantityRequest request);

    void deleteShoppingCart(String userName);
}