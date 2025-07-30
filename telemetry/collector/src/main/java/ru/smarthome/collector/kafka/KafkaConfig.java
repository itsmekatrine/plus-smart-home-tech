package ru.smarthome.collector.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.Future;
import org.apache.kafka.clients.producer.RecordMetadata;

@Slf4j
@Configuration
public class KafkaConfig {

    @Bean
    @ConfigurationProperties(prefix = "collector.kafka.producer.properties")
    public Properties kafkaProducerProperties() {
        return new Properties();
    }

    @Bean
    public Producer<String, SpecificRecordBase> kafkaProducer(Properties kafkaProducerProperties) {
        log.info("Create KafkaProducer");
        return new KafkaProducer<>(kafkaProducerProperties);
    }

    @Bean
    public KafkaClient kafkaClient(Producer<String, SpecificRecordBase> kafkaProducer) {
        return new KafkaClient() {
            @Override
            public void send(String topic, Long timestamp, String key, SpecificRecordBase event) {
                ProducerRecord<String, SpecificRecordBase> record =
                        new ProducerRecord<>(topic, null, timestamp, key, event);
                log.info("Send in topic {} the record: {}", topic, event);
                Future<RecordMetadata> future = kafkaProducer.send(record);
            }

            @Override
            public void close() {
                kafkaProducer.flush();
                log.info("Close KafkaProducer");
                kafkaProducer.close(Duration.ofSeconds(10));
            }
        };
    }
}
