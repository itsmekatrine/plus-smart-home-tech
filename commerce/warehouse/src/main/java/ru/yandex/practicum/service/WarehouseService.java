package ru.yandex.practicum.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import ru.yandex.practicum.dto.cart.CartDto;
import ru.yandex.practicum.dto.warehouse.*;

import java.util.Map;
import java.util.UUID;

public interface WarehouseService {

    void newProductInWarehouse(@Valid @NotNull NewProductInWarehouseRequest request);

    void shipToDelivery(ShipToDeliveryRequest request);

    void acceptReturn(Map<UUID, Long> returnedProducts);

    void addProductToWarehouse(@Valid @NotNull AddProductToWarehouseRequest request);

    DataOrderDto checkProductQuantity(@Valid @NotNull CartDto shoppingCart);

    DataOrderDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request);

    AddressDto getWarehouseAddress();
}
