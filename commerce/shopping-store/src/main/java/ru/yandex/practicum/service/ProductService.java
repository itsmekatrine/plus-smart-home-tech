package ru.yandex.practicum.service;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.ProductCategory;
import ru.yandex.practicum.dto.store.SetProductQuantityStateRequest;

public interface ProductService {

    List<ProductDto> getProducts(ProductCategory category, Pageable pageable);

    ProductDto getProductById(UUID productId);

    ProductDto createNewProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto);

    boolean removeProductFromStore(UUID productId);

    boolean setQuantityState(SetProductQuantityStateRequest request);
}
