package ru.yandex.practicum.dto.order;

import jakarta.validation.constraints.NotEmpty;
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
public class ProductReturnRequest {
    @NotNull
    UUID orderId;

    @NotNull
    @NotEmpty
    Map<UUID, Long> products;
}
