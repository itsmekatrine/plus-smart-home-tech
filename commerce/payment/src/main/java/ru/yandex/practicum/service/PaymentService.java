package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentResponseDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {
    PaymentResponseDto addPayment(OrderDto orderDto);

    BigDecimal getTotalCost(OrderDto orderDto);

    void processSuccessPayment(UUID paymentId);

    BigDecimal calculateProductsCost(OrderDto orderDto);

    void processFailedPayment(UUID paymentId);
}
