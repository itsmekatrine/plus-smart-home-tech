package ru.smarthome.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.smarthome.collector.dto.hubDto.HubEvent;
import ru.smarthome.collector.dto.sensorDto.SensorEvent;
import ru.smarthome.collector.kafka.KafkaClient;
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

    private final KafkaClient kafkaClient;
    private final AvroMapper avroMapper;

    @Override
    public void collectSensorEvent(SensorEvent event) {
        SensorEventAvro avro = avroMapper.toAvro(event);

        log.info("Send to topic {} event: {}", sensorsEventsTopic, avro);
        kafkaClient.send(
                sensorsEventsTopic,
                avro.getTimestamp().toEpochMilli(),
                avro.getHubId(),
                avro
        );
    }

    @Override
    public void collectHubEvent(HubEvent event) {
        HubEventAvro avro = avroMapper.toAvro(event);

        log.info("Send to topic {} event: {}", hubsEventsTopic, avro);
        kafkaClient.send(
                hubsEventsTopic,
                avro.getTimestamp().toEpochMilli(),
                avro.getHubId(),
                avro
        );
    }
}
