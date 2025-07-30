package ru.smarthome.collector.service;

import ru.smarthome.collector.dto.hubDto.HubEvent;
import ru.smarthome.collector.dto.sensorDto.SensorEvent;

public interface CollectorService {
    void collectSensorEvent(SensorEvent event);

    void collectHubEvent(HubEvent event);
}
