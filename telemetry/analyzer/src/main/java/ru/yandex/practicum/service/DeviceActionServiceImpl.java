package ru.yandex.practicum.service;

import com.google.protobuf.util.Timestamps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.HubRouterGrpcClient;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceActionServiceImpl implements DeviceActionService {

    private final HubRouterGrpcClient hubRouterGrpcClient;

    @Override
    public void send(String hubId, String scenarioName, DeviceActionProto action) {
        log.info("Отправляем действие {} для сценария {} в хаб {}", action, scenarioName, hubId);

        DeviceActionRequest request = DeviceActionRequest.newBuilder()
                .setHubId(hubId)
                .setScenarioName(scenarioName)
                .setAction(action)
                .setTimestamp(Timestamps.fromMillis(System.currentTimeMillis()))
                .build();

        hubRouterGrpcClient.send(request);
    }
}
