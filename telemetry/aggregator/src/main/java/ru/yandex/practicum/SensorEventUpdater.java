package ru.yandex.practicum;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class SensorEventUpdater {

    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        SensorsSnapshotAvro snapshot = snapshots.computeIfAbsent(event.getHubId(), k ->
                SensorsSnapshotAvro.newBuilder()
                        .setHubId(event.getHubId())
                        .setSensorsState(new HashMap<>())
                        .setTimestamp(event.getTimestamp())
                        .build());

        Map<String, SensorStateAvro> sensorsState = snapshot.getSensorsState();
        if (isUpdated(sensorsState, event)) {
            return Optional.empty();
        }

        SensorStateAvro sensorStateAvro = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();

        sensorsState.put(event.getId(), sensorStateAvro);
        snapshot.setTimestamp(event.getTimestamp());

        return Optional.of(snapshot);
    }

    private boolean isUpdated(Map<String, SensorStateAvro> sensorsState, SensorEventAvro event) {
        SensorStateAvro old = sensorsState.get(event.getId());
        if (old == null) return false;
        return old.getTimestamp().isAfter(event.getTimestamp()) || old.getData().equals(event.getPayload());
    }
}
