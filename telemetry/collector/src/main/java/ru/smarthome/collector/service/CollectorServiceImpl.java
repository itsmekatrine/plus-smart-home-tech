package ru.smarthome.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.smarthome.collector.dto.hubDto.HubEvent;
import ru.smarthome.collector.dto.sensorDto.SensorEvent;
import ru.smarthome.collector.mapper.AvroMapper;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollectorServiceImpl implements CollectorService {

    @Value("${collector.kafka.producer.topics.sensors-events}")
    private String sensorsEventsTopic;

    @Value("${collector.kafka.producer.topics.hubs-events}")
    private String hubsEventsTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AvroMapper avroMapper;

    @Override
    public void collectSensorEvent(SensorEvent event) {
        SensorEventAvro avro = avroMapper.toAvro(event);

        log.info("Send to topic {} event: {}", sensorsEventsTopic, avro);

        long timestamp = avro.getTimestamp().toEpochMilli();

        kafkaTemplate.send(sensorsEventsTopic, null, timestamp, avro.getHubId(), avro)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Sensor event for hubId {} sent successfully, offset {}",
                                avro.getHubId(), result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send sensor event for hubId {}: {}",
                                avro.getHubId(), ex.getMessage());
                    }
                });
    }

    @Override
    public void collectHubEvent(HubEvent event) {
        HubEventAvro avro = avroMapper.toAvro(event);

        log.info("Send to topic {} event: {}", hubsEventsTopic, avro);

        long timestamp = avro.getTimestamp().toEpochMilli();

        kafkaTemplate.send(hubsEventsTopic, null, timestamp, avro.getHubId(), avro)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Hub event for hubId {} sent successfully, offset {}",
                                avro.getHubId(), result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send hub event for hubId {}: {}",
                                avro.getHubId(), ex.getMessage());
                    }
                });
    }
}
