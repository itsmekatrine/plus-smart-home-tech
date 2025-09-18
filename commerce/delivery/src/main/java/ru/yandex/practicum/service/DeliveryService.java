package ru.yandex.practicum.service;

import jakarta.validation.Valid;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryService {
    DeliveryDto planDelivery(@Valid DeliveryDto deliveryDto);

    void setSuccessful(UUID orderId);

    void setPicked(UUID orderId);

    void setFailed(UUID orderId);

    BigDecimal calculateDeliveryCost(OrderDto orderDto);
}
