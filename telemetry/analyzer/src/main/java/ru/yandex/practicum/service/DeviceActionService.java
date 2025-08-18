package ru.yandex.practicum.service;

import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;

public interface DeviceActionService {

    void send(String hubId, String scenarioName, DeviceActionProto action);
}
