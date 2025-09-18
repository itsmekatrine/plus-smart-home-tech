package ru.yandex.practicum.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.client.OrderClient;

@Configuration
@EnableFeignClients(clients = { OrderClient.class })
public class FeignClientsOrderConfig {
}
