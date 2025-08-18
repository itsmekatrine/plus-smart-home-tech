package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.mapper.EntityMapper;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubEventServiceImpl implements HubEventService {

    private final ScenarioRepository scenarioRepository;
    private final SensorRepository sensorRepository;

    @Override
    public void process(HubEventAvro event) {
        String eventType = event.getPayload().getClass().getSimpleName();
        log.info("Обрабатываем событие {} для хаба {}", eventType, event.getHubId());

        switch (event.getPayload()) {
            case DeviceAddedEventAvro deviceAdded -> {
                sensorRepository.save(EntityMapper.mapToSensor(event, deviceAdded));
                log.info("Добавлен сенсор {} в хаб {}", deviceAdded.getId(), event.getHubId());
            }
            case DeviceRemovedEventAvro deviceRemoved -> {
                sensorRepository.deleteById(deviceRemoved.getId());
                log.info("Удалён сенсор {} из хаба {}", deviceRemoved.getId(), event.getHubId());
            }
            case ScenarioAddedEventAvro scenarioAdded -> {
                scenarioRepository.save(EntityMapper.mapToScenario(event, scenarioAdded));
                log.info("Добавлен сценарий {} в хаб {}", scenarioAdded.getName(), event.getHubId());
            }
            case ScenarioRemovedEventAvro scenarioRemoved -> {
                scenarioRepository.findByHubIdAndName(event.getHubId(), scenarioRemoved.getName())
                        .ifPresent(scenarioRepository::delete);
                log.info("Удалён сценарий {} из хаба {}", scenarioRemoved.getName(), event.getHubId());
            }
            default -> log.warn("Неизвестный тип события: {}", eventType);
        }
    }
}