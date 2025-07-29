package ru.smarthome.collector.controller;

import lombok.RequiredArgsConstructor;
import org.apache.avro.generic.GenericRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.smarthome.collector.dto.sensorDto.*;
import ru.smarthome.collector.mapper.AvroMapper;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@RestController
@RequestMapping("/events/sensors")
@RequiredArgsConstructor
public class SensorController {

    private final AvroMapper mapper;
    private final KafkaTemplate<String, GenericRecord> kafka;

    @PostMapping("/events/sensors")
    public ResponseEntity<?> post(@RequestBody SensorEvent dto) {
        SensorEventAvro avro = mapper.toAvro(dto);
        kafka.send("telemetry.sensors.v1", avro);
        return ResponseEntity.ok().build();
    }
}