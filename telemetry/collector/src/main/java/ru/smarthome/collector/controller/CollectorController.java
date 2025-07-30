package ru.smarthome.collector.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.smarthome.collector.dto.hubDto.HubEvent;
import ru.smarthome.collector.dto.sensorDto.SensorEvent;
import ru.smarthome.collector.service.CollectorService;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class CollectorController {
    private final CollectorService service;

    @PostMapping("/sensors")
    public void collectSensorEvent(@Valid @RequestBody SensorEvent event) {
        service.collectSensorEvent(event);
    }

    @PostMapping("/hubs")
    public void collectHubEvent(@RequestBody HubEvent event) {
        service.collectHubEvent(event);
    }
}