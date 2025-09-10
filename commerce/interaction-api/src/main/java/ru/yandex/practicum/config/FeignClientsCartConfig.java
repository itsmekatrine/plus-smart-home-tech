package ru.yandex.practicum.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.client.CartClient;
import ru.yandex.practicum.client.StoreClient;

@Configuration
@EnableFeignClients(clients = { CartClient.class, StoreClient.class })
public class FeignClientsCartConfig {
}
