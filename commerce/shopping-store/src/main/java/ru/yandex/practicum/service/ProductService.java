package ru.yandex.practicum.service;

import java.util.UUID;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.ProductCategory;
import ru.yandex.practicum.dto.store.SetProductQuantityStateRequest;

public interface ProductService {

    Page<ProductDto> getProducts(@Nullable ProductCategory category, Pageable pageable);

    ProductDto getProductById(@NotNull UUID productId);

    ProductDto createNewProduct(@Valid @NotNull ProductDto productDto);

    ProductDto updateProduct(@Valid @NotNull ProductDto productDto);

    boolean removeProductFromStore(@NotNull UUID productId);

    boolean setQuantityState(@Valid @NotNull SetProductQuantityStateRequest request);
}
