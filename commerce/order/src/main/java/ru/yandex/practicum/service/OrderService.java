package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.order.CreateOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    List<OrderDto> getClientOrders(String username);

    OrderDto createNewOrder(CreateOrderRequest request);

    OrderDto returnProducts(ProductReturnRequest request);

    OrderDto payForOrder(UUID orderId);

    OrderDto setFailedPayment(UUID orderId);

    OrderDto setDeliverySuccess(UUID orderId);

    OrderDto setDeliveryFailed(UUID orderId);

    OrderDto setOrderComplete(UUID orderId);

    OrderDto calculateTotalCost(UUID orderId);

    OrderDto calculateDeliveryCost(UUID orderId);

    OrderDto assembleOrder(UUID orderId);

    OrderDto setAssemblyFailed(UUID orderId);
}
