package ru.yandex.practicum.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import ru.yandex.practicum.dto.cart.CartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.DataOrderDto;

public interface WarehouseService {

    void newProductInWarehouse(@Valid @NotNull NewProductInWarehouseRequest request);

    void addProductToWarehouse(@Valid @NotNull AddProductToWarehouseRequest request);

    DataOrderDto checkProductQuantity(@Valid @NotNull CartDto shoppingCart);

    AddressDto getWarehouseAddress();
}
