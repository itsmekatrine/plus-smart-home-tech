package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressDto {
    @NotNull
    String country;

    @NotNull
    String city;

    @NotNull
    String street;

    @NotNull
    String house;

    @NotNull
    String flat;
}
