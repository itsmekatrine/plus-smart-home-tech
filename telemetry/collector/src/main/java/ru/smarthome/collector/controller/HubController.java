package ru.smarthome.collector.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.smarthome.collector.dto.hubDto.*;
import ru.smarthome.collector.mapper.AvroMapper;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@RestController
@RequestMapping("/events/hubs")
@RequiredArgsConstructor
public class HubController {

    private final AvroMapper mapper;
    private final KafkaTemplate<String, HubEventAvro> hubKafkaTemplate;

    @PostMapping
    public ResponseEntity<?> post(@RequestBody HubEvent dto) {
        HubEventAvro avro = mapper.toAvro(dto);
        hubKafkaTemplate.send("telemetry.hubs.v1", avro);
        return ResponseEntity.ok().build();
    }
}