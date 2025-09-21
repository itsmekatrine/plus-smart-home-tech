package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.config.DeliveryCost;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.delivery.DeliveryState;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.warehouse.ShipToDeliveryRequest;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.repository.DeliveryRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository repository;
    private final DeliveryMapper mapper;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;
    private final DeliveryCost deliveryCost;

    @Override
    public DeliveryDto planDelivery(DeliveryDto dto) {
        Delivery entity = mapper.toEntity(dto);
        entity.setDeliveryState(DeliveryState.CREATED);
        return mapper.toDto(repository.save(entity));
    }

    @Override
    public void setPicked(UUID orderId) {
        Delivery d = findByOrderId(orderId);
        d.setDeliveryState(DeliveryState.IN_PROGRESS);
        warehouseClient.shipToDelivery(new ShipToDeliveryRequest(orderId, d.getDeliveryId()));
        orderClient.assemblyOrder(orderId);
        repository.save(d);
    }

    @Override
    public void setSuccessful(UUID orderId) {
        Delivery d = findByOrderId(orderId);
        d.setDeliveryState(DeliveryState.DELIVERED);
        orderClient.deliverOrder(orderId);
        repository.save(d);
    }

    @Override
    public void setFailed(UUID orderId) {
        Delivery d = findByOrderId(orderId);
        d.setDeliveryState(DeliveryState.FAILED);
        orderClient.failDeliverOrder(orderId);
        repository.save(d);
    }

    @Override
    public BigDecimal calculateDeliveryCost(OrderDto orderDto) {
        Delivery delivery = findById(orderDto.getDeliveryId());
        Address fromAddress = delivery.getFromAddress();
        Address toAddress = delivery.getToAddress();

        BigDecimal totalCost = deliveryCost.getBase();

        BigDecimal cityMultiplier = fromAddress.getCity().equals("ADDRESS_1")
                ? deliveryCost.getAddress1()
                : deliveryCost.getAddress2();
        totalCost = totalCost.add(totalCost.multiply(cityMultiplier));

        if (orderDto.isFragile()) {
            totalCost = totalCost.add(totalCost.multiply(deliveryCost.getFragile()));
        }

        totalCost = totalCost.add(
                BigDecimal.valueOf(orderDto.getDeliveryWeight()).multiply(deliveryCost.getWeight())
        );
        totalCost = totalCost.add(
                BigDecimal.valueOf(orderDto.getDeliveryVolume()).multiply(deliveryCost.getVolume())
        );

        if (!fromAddress.getStreet().equals(toAddress.getStreet())) {
            totalCost = totalCost.add(totalCost.multiply(deliveryCost.getStreet()));
        }

        return totalCost;
    }

    private Delivery findByOrderId(UUID orderId) {
        return repository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("Delivery for order `%s` not found".formatted(orderId)));
    }

    private Delivery findById(UUID deliveryId) {
        return repository.findById(deliveryId)
                .orElseThrow(() -> new NotFoundException("Delivery `%s` not found".formatted(deliveryId)));
    }
}
