package ru.smarthome.collector.dto.hubDto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

import java.util.List;

@Getter
@Setter
@ToString(callSuper=true)
public class ScenarioAddedEvent extends HubEvent {
    private String name;
    private List<ScenarioConditionAvro> conditions;
    private List<DeviceActionAvro> actions;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
