package ru.yandex.practicum.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import ru.yandex.practicum.dto.cart.CartDto;
import ru.yandex.practicum.dto.cart.UpdateProductQuantityRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartService {

    CartDto getShoppingCart(@NotBlank String username);

    CartDto addProductToShoppingCart(@NotBlank String username, @NotEmpty Map<UUID, Long> products);

    CartDto removeFromShoppingCart(@NotBlank String username, @NotEmpty List<UUID> products);

    CartDto changeProductQuantity(@NotBlank String username, @Valid @NotNull UpdateProductQuantityRequest request);

    void deleteShoppingCart(@NotBlank String username);
}