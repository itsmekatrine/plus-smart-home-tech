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
        var type = toActionType(actionAvro.getType());
        Integer val = actionAvro.getValue();

        if (val == null && typeRequiresValue(type)) {
            throw new IllegalArgumentException(
                    "Action value is required for type=" + type + " (sensorId=" + actionAvro.getSensorId() + ")"
            );
        }
        if (val == null) {
            val = defaultValueFor(type);
        }
        return Action.builder()
                .sensor(new Sensor(actionAvro.getSensorId(), scenario.getHubId()))
                .type(type)
                .value(val)
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
        var type = action.getType();
        Integer val = action.getValue();

        if (val == null) {
            val = defaultValueFor(type);
        }
        if (val == null && typeRequiresValue(type)) {
            throw new IllegalStateException(
                    "Action.value is missing for type=" + type + ", actionId=" + action.getId()
            );
        }

        if (val == null) {
            val = 0;
        }

        return DeviceActionProto.newBuilder()
                .setSensorId(action.getSensor().getId())
                .setType(toActionTypeProto(action.getType()))
                .setValue(val)
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

    private static Integer defaultValueFor(ActionType type) {
        return switch (type) {
            case ACTIVATE -> 1;
            case DEACTIVATE -> 0;
            case INVERSE -> 1;
            case SET_VALUE -> null;
        };
    }

    private static boolean typeRequiresValue(ActionType type) {
        return switch (type) {
            case SET_VALUE -> true;
            default -> false;
        };
    }
}
