package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.dto.order.CreateOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
@Validated
public class OrderController implements OrderClient {
    private final OrderService service;

    @Override
    @GetMapping
    public List<OrderDto> getClientOrders(@RequestParam("username") @NotBlank String username) {
        return service.getClientOrders(username);
    }

    @Override
    @PutMapping
    public OrderDto createNewOrder(@Valid @RequestBody CreateOrderRequest request) {
        return service.createNewOrder(request);
    }

    @Override
    @PostMapping("/return")
    public OrderDto returnProducts(@Valid @RequestBody ProductReturnRequest request) {
        return service.returnProducts(request);
    }

    @Override
    @PostMapping("/payment")
    public OrderDto payOrder(@RequestBody @NotNull UUID orderId) {
        return service.payForOrder(orderId);
    }

    @Override
    @PostMapping("/payment/failed")
    public OrderDto failPayOrder(@RequestBody @NotNull UUID orderId) {
        return service.setFailedPayment(orderId);
    }

    @Override
    @PostMapping("/payment/success")
    public OrderDto successPayOrder(@RequestBody @NotNull UUID orderId) {
        return service.payForOrder(orderId);
    }

    @Override
    @PostMapping("/delivery")
    public OrderDto deliverOrder(@RequestBody @NotNull UUID orderId) {
        return service.setDeliverySuccess(orderId);
    }

    @Override
    @PostMapping("/delivery/failed")
    public OrderDto failDeliverOrder(@RequestBody @NotNull UUID orderId) {
        return service.setDeliveryFailed(orderId);
    }

    @Override
    @PostMapping("/completed")
    public OrderDto completeOrder(@RequestBody @NotNull UUID orderId) {
        return service.setOrderComplete(orderId);
    }

    @Override
    @PostMapping("/calculate/total")
    public OrderDto calculateTotalPrice(@RequestBody @NotNull UUID orderId) {
        return service.calculateTotalCost(orderId);
    }

    @Override
    @PostMapping("/calculate/delivery")
    public OrderDto calculateDeliveryPrice(@RequestBody @NotNull UUID orderId) {
        return service.calculateDeliveryCost(orderId);
    }

    @Override
    @PostMapping("/assembly")
    public OrderDto assemblyOrder(@RequestBody @NotNull UUID orderId) {
        return service.assembleOrder(orderId);
    }

    @Override
    @PostMapping("/assembly/failed")
    public OrderDto failAssemblyOrder(@RequestBody @NotNull UUID orderId) {
        return service.setAssemblyFailed(orderId);
    }
}
