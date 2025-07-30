package ru.smarthome.collector.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Slf4j
@Configuration
public class KafkaConfig {
    @Value("${collector.kafka.producer.properties.bootstrap.servers}")
    String bootstrapServers;

    @Value("${collector.kafka.producer.properties.key.serializer}")
    String keySerializer;

    @Value("${collector.kafka.producer.properties.value.serializer}")
    String valueSerializer;

    @Value("${collector.kafka.producer.topics.sensors-events}")
    String topicSensors;

    @Value("${collector.kafka.producer.topics.hubs-events}")
    String topicHubs;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);

        log.info("Create ProducerFactory: {}", props);

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        log.info("Create KafkaTemplate for messages: {} и {}", topicSensors, topicHubs);

        return new KafkaTemplate<>(producerFactory());
    }
}