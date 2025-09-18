package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.client.PaymentClient;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentResponseDto;
import ru.yandex.practicum.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController implements PaymentClient {
    private final PaymentService service;

    @Override
    public PaymentResponseDto addPayment(@Valid @RequestBody OrderDto orderDto) {
        return service.addPayment(orderDto);
    }

    @Override
    public BigDecimal getTotalCost(@Valid @RequestBody OrderDto orderDto) {
        return service.getTotalCost(orderDto);
    }

    @Override
    public void paymentSuccess(@RequestBody UUID paymentId) {
        service.processSuccessPayment(paymentId);
    }

    @Override
    public BigDecimal productCost(@Valid @RequestBody OrderDto orderDto) {
        return service.calculateProductsCost(orderDto);
    }

    @Override
    public void paymentFailed(@RequestBody UUID paymentId) {
        service.processFailedPayment(paymentId);
    }
}
