package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.dto.cart.CartDto;
import ru.yandex.practicum.model.ShoppingCart;

@Mapper(componentModel = "spring")
public interface ShoppingCartMapper {
    CartDto toDto(ShoppingCart shoppingCart);
}