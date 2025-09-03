package ru.yandex.practicum.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
    CartDto getShoppingCart(@RequestParam(name = "username") @NotBlank String userName);

    @PutMapping
    CartDto addProductToShoppingCart(@RequestParam(name = "username") @NotBlank String userName,
                                      @RequestBody Map<UUID, Long> products);

    @DeleteMapping
    void deleteShoppingCart(@RequestParam(name = "username") @NotBlank String userName);

    @PostMapping("/remove")
    CartDto removeFromShoppingCart(@RequestParam(name = "username") @NotBlank String userName, @RequestBody List<UUID> products);

    @PostMapping("/change-quantity")
    CartDto changeProductQuantity(@RequestParam(name = "username") @NotBlank String userName,
                                  @RequestBody @Valid UpdateProductQuantityRequest request);
}