package ru.yandex.practicum.service;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.cart.CartDto;
import ru.yandex.practicum.dto.cart.UpdateProductQuantityRequest;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.repository.ShoppingCartRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository repository;
    private final ShoppingCartMapper mapper;
    private final WarehouseClient warehouseClient;

    @Override
    public CartDto getShoppingCart(String username) {
        return mapper.toDto(getOrElseCreateShoppingCart(username));
    }

    @Override
    public CartDto addProductToShoppingCart(String username, Map<UUID, Long> products) {
        ShoppingCart shoppingCart = getOrElseCreateShoppingCart(username);
        updateCartProducts(shoppingCart, products);
        warehouseClient.checkProductQuantity(mapper.toDto(shoppingCart));
        return mapper.toDto(repository.save(shoppingCart));
    }

    @Override
    public CartDto removeFromShoppingCart(String username, List<UUID> products) {
        ShoppingCart shoppingCart = findCartByUsername(username);

        for (UUID product : products) {
            shoppingCart.getProducts().remove(product);
        }
        return mapper.toDto((repository.save(shoppingCart)));
    }

    @Override
    public CartDto changeProductQuantity(String username, UpdateProductQuantityRequest request) {
        ShoppingCart shoppingCart = findCartByUsername(username);
        if (!shoppingCart.getProducts().containsKey(request.getProductId())) {
            throw new NotFoundException("Product `%s` not found in cart".formatted(request.getProductId()));
        }
        shoppingCart.getProducts().put(request.getProductId(), request.getNewQuantity());
        warehouseClient.checkProductQuantity(mapper.toDto(shoppingCart));
        return mapper.toDto(repository.save(shoppingCart));
    }

    @Override
    public void deleteShoppingCart(String username) {
        repository.deleteByUsername(username);
    }

    private ShoppingCart createCart(String username, Map<UUID, Long> products) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUsername(username);
        shoppingCart.setProducts(products);
        shoppingCart.setIsActive(true);
        return shoppingCart;
    }

    private void updateCartProducts(ShoppingCart shoppingCart, Map<UUID, Long> newProducts) {
        newProducts.forEach((productId, quantity) -> shoppingCart.getProducts().merge(productId, quantity, Long::sum));}

    private ShoppingCart getOrElseCreateShoppingCart(String username) {
        return repository.findByUsername(username)
                .orElseGet(() -> repository.save(createCart(username, new HashMap<>())));
    }

    private ShoppingCart findCartByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User `%s` cart not found".formatted(username)));
    }
}
