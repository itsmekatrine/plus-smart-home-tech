package ru.yandex.practicum.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "delivery.cost")
public class DeliveryCost {
    private final BigDecimal base;
    private final BigDecimal address1;
    private final BigDecimal address2;
    private final BigDecimal fragile;
    private final BigDecimal weight;
    private final BigDecimal volume;
    private final BigDecimal street;
}
