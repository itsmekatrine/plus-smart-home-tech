package ru.yandex.practicum.mapper;

import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.*;

import java.util.List;

public class EntityMapper {

    // Avro → Entity

    public static Sensor mapToSensor(HubEventAvro hubEvent, DeviceAddedEventAvro deviceAdded) {
        return new Sensor(
                deviceAdded.getId(),
                hubEvent.getHubId()
        );
    }

    public static Scenario mapToScenario(HubEventAvro hubEvent, ScenarioAddedEventAvro scenarioAvro) {
        Scenario scenario = new Scenario();
        scenario.setHubId(hubEvent.getHubId());
        scenario.setName(scenarioAvro.getName());

        scenario.setConditions(
                scenarioAvro.getConditions().stream()
                        .map(conditionAvro -> mapToCondition(scenario, conditionAvro))
                        .toList()
        );

        scenario.setActions(
                scenarioAvro.getActions().stream()
                        .map(actionAvro -> mapToAction(scenario, actionAvro))
                        .toList()
        );

        return scenario;
    }

    public static Condition mapToCondition(Scenario scenario, ScenarioConditionAvro conditionAvro) {
        return Condition.builder()
                .sensor(new Sensor(conditionAvro.getSensorId(), scenario.getHubId()))
                .type(toConditionType(conditionAvro.getType()))
                .operation(toConditionOperation(conditionAvro.getOperation()))
                .value(getConditionValue(conditionAvro.getValue()))
                .scenarios(List.of(scenario))
                .build();
    }

    public static Action mapToAction(Scenario scenario, DeviceActionAvro actionAvro) {
        if (actionAvro.getSensorId() == null || actionAvro.getSensorId().isBlank()) {
            throw new IllegalArgumentException("DeviceActionAvro.sensorId is null");
        }
        if (scenario.getHubId() == null || scenario.getHubId().isBlank()) {
            throw new IllegalArgumentException("Scenario.hubId is null");
        }

        Integer value = actionAvro.getValue();
        if (value == null) {
            switch (actionAvro.getType()) {
                case ACTIVATE -> value = 1;
                case DEACTIVATE -> value = 0;
                default -> throw new IllegalArgumentException(
                        "Action value is required for type=" + actionAvro.getType() + ", sensorId=" + actionAvro.getSensorId());
            }
        }

        return Action.builder()
                .sensor(new Sensor(actionAvro.getSensorId(), scenario.getHubId()))
                .type(toActionType(actionAvro.getType()))
                .value(actionAvro.getValue())
                .build();
    }

    public static ConditionType toConditionType(ConditionTypeAvro typeAvro) {
        return ConditionType.valueOf(typeAvro.name());
    }

    public static ConditionOperation toConditionOperation(ConditionOperationAvro operationAvro) {
        return ConditionOperation.valueOf(operationAvro.name());
    }

    public static ActionType toActionType(ActionTypeAvro typeAvro) {
        return ActionType.valueOf(typeAvro.name());
    }

    public static Integer getConditionValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean b) {
            return b ? 1 : 0;
        }
        if (value instanceof Integer i) {
            return i;
        }
        throw new ClassCastException("Ошибка преобразования значения условия: " + value.getClass());
    }

    // Entity → Protobuf

    public static DeviceActionProto actionToProto(Action action) {
        if (action.getValue() == null) {
            throw new IllegalStateException("Action.value is null, actionId=" + action.getId());
        }
        if (action.getSensor() == null) {
            throw new IllegalStateException("Action.sensor is null, actionId=" + action.getId());
        }
        if (action.getSensor().getId() == null || action.getSensor().getId().isBlank()) {
            throw new IllegalStateException("Sensor.id is null/blank for actionId=" + action.getId());
        }
        if (action.getSensor().getHubId() == null || action.getSensor().getHubId().isBlank()) {
            throw new IllegalStateException("Sensor.hubId is null/blank for actionId=" + action.getId());
        }
        if (action.getType() == null) {
            throw new IllegalStateException("Action.type is null for actionId=" + action.getId());
        }

        return DeviceActionProto.newBuilder()
                .setSensorId(action.getSensor().getId())
                .setType(toActionTypeProto(action.getType()))
                .setValue(action.getValue())
                .build();
    }

    private static ActionTypeProto toActionTypeProto(ActionType actionType) {
        return switch (actionType) {
            case ACTIVATE -> ActionTypeProto.ACTIVATE;
            case DEACTIVATE -> ActionTypeProto.DEACTIVATE;
            case INVERSE -> ActionTypeProto.INVERSE;
            case SET_VALUE -> ActionTypeProto.SET_VALUE;
        };
    }
}
