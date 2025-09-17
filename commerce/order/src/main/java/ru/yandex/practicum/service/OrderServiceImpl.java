package ru.yandex.practicum.service;

import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.client.CartClient;
import ru.yandex.practicum.client.DeliveryClient;
import ru.yandex.practicum.client.PaymentClient;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.delivery.DeliveryState;
import ru.yandex.practicum.dto.order.CreateOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.OrderState;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.DataOrderDto;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.repository.OrderRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final WarehouseClient warehouseClient;
    private final CartClient cartClient;
    private final DeliveryClient deliveryClient;
    private final PaymentClient paymentClient;

    @Override
    public List<OrderDto> getClientOrders(String username) {
        return repository.findAllByUserName(username)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public OrderDto createNewOrder(CreateOrderRequest request) {
        DataOrderDto orderData = warehouseClient.checkProductQuantity(request.getShoppingCart());

        Order order = repository.save(mapper.createRequestToOrder(request, orderData));

        DeliveryDto delivery = deliveryClient.planDelivery(
                buildDeliveryRequest(order.getOrderId(), request.getDeliveryAddress())
        );
        order.setDeliveryId(delivery.getDeliveryId());

        return mapper.toDto(repository.save(order));
    }

    @Override
    public OrderDto returnProducts(ProductReturnRequest request) {
        Order order = findOrderById(orderIdOrThrow(request));

        request.getProducts().forEach((pid, qty) -> {
            Long current = order.getProducts().get(pid);
            if (current == null) return;
            long newQty = current - qty;
            if (newQty > 0) order.getProducts().put(pid, newQty);
            else            order.getProducts().remove(pid);
        });
        order.setState(OrderState.PRODUCT_RETURNED);
        return mapper.toDto(repository.save(order));
    }


    @Override
    public OrderDto payForOrder(UUID orderId) {
        return mapper.toDto(updateState(orderId, OrderState.PAID));
    }

    @Override
    public OrderDto setFailedPayment(UUID orderId) {
        return mapper.toDto(updateState(orderId, OrderState.PAYMENT_FAILED));
    }

    @Override
    public OrderDto setDeliverySuccess(UUID orderId) {
        return mapper.toDto(updateState(orderId, OrderState.DELIVERED));
    }

    @Override
    public OrderDto setDeliveryFailed(UUID orderId) {
        return mapper.toDto(updateState(orderId, OrderState.DELIVERY_FAILED));
    }

    @Override
    public OrderDto setOrderComplete(UUID orderId) {
        return mapper.toDto(updateState(orderId, OrderState.COMPLETED));
    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        Order order = findOrderById(orderId);
        order.setTotalPrice(paymentClient.getTotalCost(mapper.toDto(order)));
        return mapper.toDto(repository.save(order));
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        Order order = findOrderById(orderId);
        order.setDeliveryPrice(deliveryClient.deliveryCost(mapper.toDto(order)));
        return mapper.toDto(repository.save(order));
    }

    @Override
    public OrderDto assembleOrder(UUID orderId) {
        return mapper.toDto(updateState(orderId, OrderState.ASSEMBLED));
    }

    @Override
    public OrderDto setAssemblyFailed(UUID orderId) {
        return mapper.toDto(updateState(orderId, OrderState.ASSEMBLY_FAILED));
    }

    private Order updateState(UUID orderId, OrderState newState) {
        Order order = findOrderById(orderId);
        order.setState(newState);
        return repository.save(order);
    }

    private Order findOrderById(UUID orderId) {
        return repository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order `%s` not found".formatted(orderId)));
    }

    private static UUID orderIdOrThrow(ProductReturnRequest r) {
        if (r.getOrderId() == null) {
            throw new BadRequestException("The orderId is required");
        }
        return r.getOrderId();
    }

    private DeliveryDto buildDeliveryRequest(UUID orderId, AddressDto toAddress) {
        return DeliveryDto.builder()
                .fromAddress(warehouseClient.getWarehouseAddress())
                .toAddress(toAddress)
                .orderId(orderId)
                .deliveryState(DeliveryState.CREATED)
                .build();
    }
}
