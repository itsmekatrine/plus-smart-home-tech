package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.store.ProductCategory;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.ProductState;
import ru.yandex.practicum.dto.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ProductRepository;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository repository;
    private final ProductMapper mapper;

    @Override
    @Transactional
    public List<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        Pageable p = pageable.getSort().isSorted()
                ? pageable
                : PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("productName"));

        return repository
                .findAllByProductCategoryAndProductState(category, ProductState.ACTIVE, p)
                .map(mapper::toDto)
                .getContent();
    }

    @Override
    public ProductDto getProductById(UUID productId) {
        return mapper.toDto(findProductById(productId));
    }

    @Override
    public ProductDto createNewProduct(ProductDto productDto) {
        Product product = mapper.toEntity(productDto);
        return mapper.toDto(repository.save(product));
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        findProductById(productDto.getProductId());
        return mapper.toDto(repository.save(mapper.toEntity(productDto)));
    }

    @Override
    public boolean setQuantityState(SetProductQuantityStateRequest request) {
        Product product = findProductById(request.getProductId());
        if (product.getQuantityState().equals(request.getQuantityState())) {
            return false;
        }
        product.setQuantityState(request.getQuantityState());
        repository.save(product);
        return true;
    }

    @Override
    public boolean removeProductFromStore(UUID productId) {
        Product product = findProductById(productId);

        if (product.getProductState() == ProductState.DEACTIVATE) {
            return true;
        }
        product.setProductState(ProductState.DEACTIVATE);
        repository.save(product);
        return true;
    }

    private Product findProductById(UUID productId) {
        return repository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with id `%s` not found".formatted(productId)));
    }
}
