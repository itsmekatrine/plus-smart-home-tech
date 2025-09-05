package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.dto.cart.CartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.OrderDto;
import ru.yandex.practicum.exception.AlreadyExistsException;
import ru.yandex.practicum.exception.NotEnoughProductsInWarehouseException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.Dimension;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.WarehouseRepository;

import java.security.SecureRandom;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
@Validated
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository repository;
    private final WarehouseMapper mapper;

    private static final String[] ADDRESSES =
            new String[] {"ADDRESS_1", "ADDRESS_2"};

    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, 1)];

    @Override
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        if (repository.existsById(request.getProductId())) {
            throw new AlreadyExistsException("Product `%s` already exists in warehouse".formatted(request.getProductId()));
        }
        repository.save(mapper.newProductRequestToEntity(request));
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        WarehouseProduct product = findProductById(request.getProductId());
        product.setQuantity(product.getQuantity() + request.getQuantity());
        repository.save(product);
    }

    @Override
    public OrderDto checkProductQuantity(CartDto shoppingCart) {
        if (shoppingCart == null || shoppingCart.getProducts() == null || shoppingCart.getProducts().isEmpty()) {
            return new OrderDto(0.0, 0.0, false);
        }

        Map<UUID, Long> cartProducts = shoppingCart.getProducts();

        for (Map.Entry<UUID, Long> e : cartProducts.entrySet()) {
            if (e.getKey() == null || e.getValue() == null || e.getValue() <= 0) {
                throw new IllegalArgumentException("Invalid quantity for product " + e.getKey() + ": " + e.getValue());
            }
        }

        Map<UUID, WarehouseProduct> products = repository.findAllById(cartProducts.keySet())
                .stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));

        List<UUID> missing = cartProducts.keySet().stream()
                .filter(id -> !products.containsKey(id))
                .toList();

        double totalWeight = 0.0;
        double totalVolume = 0.0;
        boolean fragile = false;
        List<String> insufficient = new ArrayList<>();

        for (Map.Entry<UUID, Long> cartProduct : cartProducts.entrySet()) {
            UUID productId = cartProduct.getKey();
            long requestedQty = cartProduct.getValue();

            WarehouseProduct product = products.get(productId);
            if (product == null) {
                continue;
            }

            long available = product.getQuantity();
            if (requestedQty > available) {
                insufficient.add(String.format("id=%s requested=%d available=%d", productId, requestedQty, available));
                continue;
            }

            Dimension d = product.getDimension();
            if (d == null || d.getWidth() == null || d.getHeight() == null || d.getDepth() == null
                    || d.getWidth() <= 0 || d.getHeight() <= 0 || d.getDepth() <= 0) {
                throw new IllegalStateException("Invalid dimensions for product id=" + productId);
            }

            totalWeight += product.getWeight() * requestedQty;
            totalVolume += (d.getWidth() * d.getHeight() * d.getDepth()) * requestedQty;
            fragile |= product.isFragile();
        }

        if (!missing.isEmpty() || !insufficient.isEmpty()) {
            StringBuilder sb = new StringBuilder("Insufficient stock");
            if (!missing.isEmpty()) {
                sb.append("Missing products: ").append(missing).append(".");
            }
            if (!insufficient.isEmpty()) {
                sb.append("Not enough quantity: ").append(String.join("; ", insufficient)).append(".");
            }
            throw new NotEnoughProductsInWarehouseException(sb.toString());
        }

        return new OrderDto(
                totalWeight,
                totalVolume,
                fragile
        );
    }

    @Override
    public AddressDto getWarehouseAddress() {
        String address = CURRENT_ADDRESS;
        return AddressDto.builder()
                .country(address)
                .city(address)
                .street(address)
                .house(address)
                .flat(address)
                .build();
    }

    private WarehouseProduct findProductById(UUID productId) {
        return repository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with id `%s` not found".formatted(productId)));
    }
}
