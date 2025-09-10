package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DimensionDto {
    @NotNull(message = "Width is required")
    @Min(value = 1)
    Double width;

    @NotNull
    @Min(value = 1)
    Double height;

    @NotNull
    @Min(value = 1)
    Double depth;
}
