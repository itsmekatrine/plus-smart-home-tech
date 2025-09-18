package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.cart.CartDto;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.service.WarehouseService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController implements WarehouseClient {
    private final WarehouseService service;

    @Override
    @PutMapping
    public void newProductInWarehouse(@RequestBody @Valid NewProductInWarehouseRequest request) {
        service.newProductInWarehouse(request);
    }

    @Override
    public void shipToDelivery(@Valid @RequestBody ShipToDeliveryRequest request) {
        service.shipToDelivery(request);
    }

    @Override
    public void acceptReturn(@NotNull @NotEmpty @RequestBody Map<UUID, Long> returnedProducts) {
        service.acceptReturn(returnedProducts);
    }

    @Override
    @PostMapping("/check")
    public DataOrderDto checkProductQuantity(@RequestBody @Valid CartDto shoppingCart) {
        return service.checkProductQuantity(shoppingCart);
    }

    @Override
    public DataOrderDto assemblyProductsForOrder(@Valid @RequestBody AssemblyProductsForOrderRequest request) {
        return service.assemblyProductsForOrder(request);
    }

    @Override
    @PostMapping("/add")
    public void addProductToWarehouse(@RequestBody @Valid AddProductToWarehouseRequest request) {
        service.addProductToWarehouse(request);
    }

    @Override
    @GetMapping("/address")
    public AddressDto getWarehouseAddress() {
        return service.getWarehouseAddress();
    }
}
