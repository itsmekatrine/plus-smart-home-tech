package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.client.DeliveryClient;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController implements DeliveryClient {
    private final DeliveryService service;

    @Override
    public DeliveryDto planDelivery(@Valid @RequestBody DeliveryDto deliveryDto) {
        return service.planDelivery(deliveryDto);
    }

    @Override
    public void deliverySuccessful(@RequestBody UUID orderId) {
        service.setSuccessful(orderId);
    }

    @Override
    public void deliveryPicked(@RequestBody UUID orderId) {
        service.setPicked(orderId);
    }

    @Override
    public void deliveryFailed(@RequestBody UUID orderId) {
        service.setFailed(orderId);
    }

    @Override
    public BigDecimal deliveryCost(OrderDto orderDto) {
        return service.calculateDeliveryCost(orderDto);
    }
}
