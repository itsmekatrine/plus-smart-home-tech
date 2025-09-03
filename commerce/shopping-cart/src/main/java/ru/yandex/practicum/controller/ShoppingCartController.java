package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.client.CartClient;
import ru.yandex.practicum.dto.cart.CartDto;
import ru.yandex.practicum.dto.cart.UpdateProductQuantityRequest;
import ru.yandex.practicum.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController implements CartClient {
    private final ShoppingCartService service;

    @Override
    public CartDto getShoppingCart(@RequestParam String username) {
        return service.getShoppingCart(username);
    }

    @Override
    public CartDto addProductToShoppingCart(@RequestParam String username, Map<UUID, @NotNull Long> products) {
        return service.addProductToShoppingCart(username, products);
    }

    @Override
    public CartDto removeFromShoppingCart(@RequestParam String username, @RequestBody List<UUID> products) {
        return service.removeFromShoppingCart(username, products);
    }

    @Override
    public CartDto changeProductQuantity(@RequestParam String username, @Valid @RequestBody UpdateProductQuantityRequest request) {
        return service.changeProductQuantity(username, request);
    }

    @Override
    public void deleteShoppingCart(@RequestParam String username) {
        service.deleteShoppingCart(username);
    }
}
