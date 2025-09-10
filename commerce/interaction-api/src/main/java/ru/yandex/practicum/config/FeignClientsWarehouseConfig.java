package ru.yandex.practicum.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.client.WarehouseClient;

@Configuration
@EnableFeignClients(clients = { WarehouseClient.class })
public class FeignClientsWarehouseConfig {
}
