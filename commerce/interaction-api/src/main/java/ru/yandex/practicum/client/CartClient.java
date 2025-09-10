package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.cart.CartDto;
import ru.yandex.practicum.dto.cart.UpdateProductQuantityRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart")
public interface CartClient {

    @GetMapping
    CartDto getShoppingCart(@RequestParam(name = "username") String userName);

    @PutMapping
    CartDto addProductToShoppingCart(@RequestParam(name = "username") String userName, @RequestBody Map<UUID, Long> products);

    @DeleteMapping
    void deleteShoppingCart(@RequestParam(name = "username") String userName);

    @PostMapping("/remove")
    CartDto removeFromShoppingCart(@RequestParam(name = "username") String userName, @RequestBody List<UUID> products);

    @PostMapping("/change-quantity")
    CartDto changeProductQuantity(@RequestParam(name = "username") String userName, @RequestBody UpdateProductQuantityRequest request);
}