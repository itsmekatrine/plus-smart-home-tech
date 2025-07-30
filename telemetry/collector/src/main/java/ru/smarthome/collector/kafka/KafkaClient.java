package ru.smarthome.collector.kafka;

import org.apache.avro.specific.SpecificRecordBase;

public interface KafkaClient extends AutoCloseable {
    void send(String topic, Long timestamp, String hubId, SpecificRecordBase event);
}