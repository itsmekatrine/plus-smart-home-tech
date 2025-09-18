package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.dto.payment.PaymentResponseDto;
import ru.yandex.practicum.model.Payment;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentResponseDto toResponseDto(Payment payment);
}
