package ru.smarthome.collector.config;

import org.apache.kafka.common.serialization.StringSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Bean
    public ProducerFactory<String, HubEventAvro> hubProducerFactory(
            @Value("${spring.kafka.bootstrap-servers}") String brokers,
            @Value("${spring.kafka.producer.properties.schema.registry.url}") String schemaRegistry
    ) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put("schema.registry.url", schemaRegistry);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public ProducerFactory<String, SensorEventAvro> sensorProducerFactory(
            @Value("${spring.kafka.bootstrap-servers}") String brokers,
            @Value("${spring.kafka.producer.properties.schema.registry.url}") String schemaRegistry
    ) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put("schema.registry.url", schemaRegistry);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, HubEventAvro> hubKafkaTemplate(
            ProducerFactory<String, HubEventAvro> pf
    ) {
        return new KafkaTemplate<>(pf);
    }

    @Bean
    public KafkaTemplate<String, SensorEventAvro> sensorKafkaTemplate(
            ProducerFactory<String, SensorEventAvro> pf
    ) {
        return new KafkaTemplate<>(pf);
    }
}
