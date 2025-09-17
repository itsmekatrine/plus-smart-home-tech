package ru.yandex.practicum.service;

import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.client.CartClient;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.order.CreateOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.OrderState;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.repository.OrderRepository;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final WarehouseClient warehouseClient;
    private final CartClient cartClient;


    @Override
    public List<OrderDto> getClientOrders(String username) {
        return repository.findAllByUserName(username)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public OrderDto createNewOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setShoppingCartId(request.getShoppingCart().getShoppingCartId());
        order.setProducts(new HashMap<>(request.getShoppingCart().getProducts()));
        order.setState(OrderState.NEW);

        order.setDeliveryWeight(request.getDeliveryWeight());
        order.setDeliveryVolume(request.getDeliveryVolume());
        order.setFragile(Boolean.TRUE.equals(request.getFragile()));

        Order saved = repository.save(order);
        return mapper.toDto(saved);
    }

    @Override
    public OrderDto returnProduct(ProductReturnRequest request) {
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
        order.setTotalPrice(paymentFeign.getTotalCost(mapper.toDto(order)));
        return mapper.toDto(repository.save(order));
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        Order order = findOrderById(orderId);
        order.setDeliveryPrice(deliveryFeign.deliveryCost(mapper.toDto(order)));
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
            throw new BadRequestException("orderId обязателен");
        }
        return r.getOrderId();
    }

}
