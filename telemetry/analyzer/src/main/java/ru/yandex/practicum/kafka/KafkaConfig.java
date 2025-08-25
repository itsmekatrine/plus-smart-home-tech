package ru.yandex.practicum.kafka;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
@Configuration
public class KafkaConfig {

    // общие
    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String consumerBootstrap;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.consumer.key-deserializer}")
    private String keyDeserializer;

    @Value("${app.kafka.consumer.attempt-timeout}")
    private long attemptTimeout;

    // десериализаторы
    @Value("${app.kafka.deserializers.hub-events}")
    private String hubEventsValueDeserializer;

    @Value("${app.kafka.deserializers.sensor-snapshots}")
    private String snapshotsValueDeserializer;

    // producer
    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String producerBootstrap;

    @Value("${spring.kafka.producer.key-serializer}")
    private String keySerializer;

    @Value("${spring.kafka.producer.value-serializer}")
    private String valueSerializer;

    // топики
    @Value("${app.kafka.topics.hub-events}")
    private String hubEventsTopic;

    @Value("${app.kafka.topics.sensor-snapshots}")
    private String sensorSnapshotsTopic;

    @Bean
    public ConsumerFactory<String, SensorsSnapshotAvro> snapshotConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerBootstrap);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId + "-snapshots");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, snapshotsValueDeserializer);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        log.info("Analyzer snapshot consumer props: {}", props);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConsumerFactory<String, HubEventAvro> hubEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerBootstrap);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId + "-events");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, hubEventsValueDeserializer);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        log.info("Analyzer hubEvent consumer props: {}", props);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ProducerFactory<String, SpecificRecordBase> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producerBootstrap);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);

        log.info("Analyzer producer props: {}", props);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public Consumer<String, HubEventAvro> hubEventConsumer(ConsumerFactory<String, HubEventAvro> hubEventConsumerFactory) {
        return hubEventConsumerFactory.createConsumer();
    }

    @Bean
    public Consumer<String, SensorsSnapshotAvro> snapshotConsumer(ConsumerFactory<String, SensorsSnapshotAvro> snapshotConsumerFactory) {
        return snapshotConsumerFactory.createConsumer();
    }

    @Bean
    public KafkaTemplate<String, SpecificRecordBase> kafkaTemplate(ProducerFactory<String, SpecificRecordBase> pf) {
        return new KafkaTemplate<>(pf);
    }
}