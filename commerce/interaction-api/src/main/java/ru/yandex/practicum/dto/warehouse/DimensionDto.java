package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DimensionDto {
    @NotNull(message = "Width is required")
    @Min(value = 1)
    private Double width;

    @NotNull
    @Min(value = 1)
    private Double height;

    @NotNull
    @Min(value = 1)
    private Double depth;
}
