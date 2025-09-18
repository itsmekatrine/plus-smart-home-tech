package ru.yandex.practicum.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.client.PaymentClient;

@Configuration
@EnableFeignClients(clients = { PaymentClient.class })
public class FeignClientsPaymentConfig {
}
