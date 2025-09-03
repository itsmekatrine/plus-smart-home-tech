package ru.yandex.practicum.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.StoreClient;
import ru.yandex.practicum.dto.store.ProductCategory;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.service.ProductService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ProductController implements StoreClient {
    private final ProductService service;

    @Override
    @GetMapping
    public List<ProductDto> getProducts(@NotNull ProductCategory category, Pageable pageable) {
        return service.getProducts(category, pageable);
    }

    @Override
    public ProductDto getProduct(@PathVariable UUID productId) {
        return service.getProductById(productId);
    }

    @Override
    public ProductDto createNewProduct(@RequestBody ProductDto productDto) {
        return service.createNewProduct(productDto);
    }

    @Override
    public ProductDto updateProduct(@RequestBody ProductDto productDto) {
        return service.updateProduct(productDto);
    }

    @Override
    public boolean removeProductFromStore(UUID productId) {
        return service.removeProductFromStore(productId);
    }

    @Override
    public boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        return service.setQuantityState(request);
    }
}
