package ru.yandex.practicum.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.client.DeliveryClient;

@Configuration
@EnableFeignClients(clients = { DeliveryClient.class })
public class FeignClientsDeliveryConfig {
}
