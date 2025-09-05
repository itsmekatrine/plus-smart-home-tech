package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddProductToWarehouseRequest {
    @NotNull
    private UUID productId;

    @NotNull
    @PositiveOrZero
    private Long quantity;
}
