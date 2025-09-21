package ru.yandex.practicum.service;

import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.client.StoreClient;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.payment.PaymentResponseDto;
import ru.yandex.practicum.dto.payment.PaymentStatus;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.repository.PaymentRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository repository;
    private final PaymentMapper mapper;
    private final OrderClient orderClient;
    private final StoreClient storeClient;

    @Value("${payment.vat-rate:0.10}")
    private BigDecimal taxRate;

    @Override
    public PaymentResponseDto addPayment(OrderDto orderDto) {
        BigDecimal productsCost = calculateProductsCost(orderDto);
        BigDecimal delivery     = requireNonNegative("deliveryPrice", orderDto.getDeliveryPrice());
        BigDecimal tax          = calculateTax(productsCost);
        BigDecimal total        = normalize(productsCost.add(delivery).add(tax));

        Payment payment = new Payment();
        payment.setOrderId(orderDto.getOrderId());
        payment.setTotalPayment(total);
        payment.setDeliveryTotal(delivery);
        payment.setFeeTotal(tax);
        payment.setPaymentStatus(PaymentStatus.PENDING);

        return mapper.toResponseDto(repository.save(payment));
    }

    @Override
    public BigDecimal getTotalCost(OrderDto orderDto) {
        BigDecimal productsCost = calculateProductsCost(orderDto);
        BigDecimal delivery     = requireNonNegative("deliveryPrice", orderDto.getDeliveryPrice());
        BigDecimal tax          = calculateTax(productsCost);
        return normalize(productsCost.add(tax).add(delivery));
    }

    @Override
    public void processSuccessPayment(UUID paymentId) {
        Payment payment = findPaymentById(paymentId);
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        repository.save(payment);
        orderClient.successPayOrder(payment.getOrderId());
    }

    @Override
    public BigDecimal calculateProductsCost(OrderDto orderDto) {
        Map<UUID, Long> productQuantities = orderDto.getProducts();

        Map<UUID, BigDecimal> productPrices = productQuantities.keySet().stream()
                .map(storeClient::getProduct)
                .collect(Collectors.toMap(ProductDto::getProductId, ProductDto::getPrice));

        BigDecimal sum = productQuantities.entrySet().stream()
                .map(e -> productPrices.get(e.getKey()).multiply(BigDecimal.valueOf(e.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return normalize(sum);
    }

    @Override
    public void processFailedPayment(UUID paymentId) {
        Payment payment = findPaymentById(paymentId);
        payment.setPaymentStatus(PaymentStatus.FAILED);
        repository.save(payment);
        orderClient.failPayOrder(payment.getOrderId());
    }

    private BigDecimal calculateTax(BigDecimal base) {
        return normalize(base).multiply(taxRate);
    }

    private static BigDecimal normalize(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value.setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal requireNonNegative(String field, BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Not enough payment info: " + field);
        }
        return normalize(value);
    }

    private Payment findPaymentById(UUID paymentId) {
        return repository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment `%s` not found".formatted(paymentId)));
    }
}
