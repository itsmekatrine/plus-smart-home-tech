package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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
    @GetMapping
    public CartDto getShoppingCart(@RequestParam(name = "username") String username) {
        return service.getShoppingCart(username);
    }

    @Override
    @PutMapping
    public CartDto addProductToShoppingCart(@RequestParam(name = "username") String username, @RequestBody Map<UUID, Long> products) {
        return service.addProductToShoppingCart(username, products);
    }

    @Override
    @PostMapping("/remove")
    public CartDto removeFromShoppingCart(@RequestParam(name = "username") String username, @RequestBody List<UUID> products) {
        return service.removeFromShoppingCart(username, products);
    }

    @Override
    @PostMapping("/change-quantity")
    public CartDto changeProductQuantity(@RequestParam(name = "username") String username,
                                         @Valid @RequestBody UpdateProductQuantityRequest request) {
        return service.changeProductQuantity(username, request);
    }

    @Override
    @DeleteMapping
    public void deleteShoppingCart(@RequestParam(name = "username") String username) {
        service.deleteShoppingCart(username);
    }
}
