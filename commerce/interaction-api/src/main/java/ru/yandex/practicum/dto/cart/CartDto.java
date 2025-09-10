package ru.yandex.practicum.dto.cart;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartDto {
    @NotNull
    UUID shoppingCartId;

    @NotNull
    Map<UUID, Long> products;
}
