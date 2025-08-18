package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.mapper.EntityMapper;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SnapshotServiceImpl implements SnapshotService {

    private final ScenarioRepository scenarioRepository;
    private final DeviceActionService deviceActionService;

    @Override
    public void process(SensorsSnapshotAvro snapshot) {
        log.info("Обрабатываем снапшот для hub {}", snapshot.getHubId());

        List<Scenario> scenarios = scenarioRepository.findByHubId(snapshot.getHubId());

        for (Scenario scenario : scenarios) {
            boolean conditionsMatch = scenario.getConditions().stream()
                    .allMatch(c -> checkCondition(c, snapshot));

            if (conditionsMatch) {
                scenario.getActions().forEach(action ->
                        deviceActionService.send(
                                snapshot.getHubId(),
                                scenario.getName(),
                                EntityMapper.actionToProto(action)
                        )
                );
            }
        }
    }

    private boolean checkCondition(Condition condition, SensorsSnapshotAvro snapshot) {
        SensorStateAvro state = snapshot.getSensorsState().get(condition.getSensor().getId());
        if (state == null) {
            return false;
        }

        Integer sensorValue = extractSensorValue(state);
        if (sensorValue == null) {
            return false;
        }

        return switch (condition.getOperation()) {
            case GREATER_THAN -> sensorValue > condition.getValue();
            case LOWER_THAN -> sensorValue < condition.getValue();
            case EQUALS -> sensorValue.equals(condition.getValue());
            default -> false;
        };
    }

    private Integer extractSensorValue(SensorStateAvro state) {
        Object data = state.getData();

        if (data instanceof ClimateSensorAvro climate) {
            return climate.getTemperatureC();
        } else if (data instanceof LightSensorAvro light) {
            return light.getLuminosity();
        } else if (data instanceof MotionSensorAvro motion) {
            return motion.getMotion() ? 1 : 0;
        } else if (data instanceof SwitchSensorAvro sw) {
            return sw.getState() ? 1 : 0;
        } else if (data instanceof TemperatureSensorAvro temp) {
            return temp.getTemperatureC();
        }

        return null;
    }
}