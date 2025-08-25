package ru.smarthome.collector.mapper;

import org.springframework.stereotype.Component;
import ru.smarthome.collector.dto.hubDto.*;
import ru.smarthome.collector.dto.hubDto.HubEvent;
import ru.smarthome.collector.dto.sensorDto.SensorEvent;
import ru.smarthome.collector.dto.sensorDto.*;
import ru.yandex.practicum.grpc.telemetry.event.*;

import java.time.Instant;

@Component
public class ProtoMapper {

    // HubEventProto → HubEvent

    public HubEvent toDomain(HubEventProto proto) {
        return switch (proto.getPayloadCase()) {
            case DEVICE_ADDED      -> toDeviceAdded(proto);
            case DEVICE_REMOVED    -> toDeviceRemoved(proto);
            case SCENARIO_ADDED    -> toScenarioAdded(proto);
            case SCENARIO_REMOVED  -> toScenarioRemoved(proto);
            case PAYLOAD_NOT_SET   -> throw new IllegalArgumentException("Hub payload not set");
        };
    }

    private DeviceAddedEvent toDeviceAdded(HubEventProto proto) {
        DeviceAddedEventProto p = proto.getDeviceAdded();
        return DeviceAddedEvent.builder()
                .hubId(proto.getHubId())
                .timestamp(Instant.ofEpochSecond(proto.getTimestamp().getSeconds(), proto.getTimestamp().getNanos()))
                .id(p.getId())
                .deviceType(DeviceType.valueOf(p.getType().name()))
                .build();
    }

    private DeviceRemovedEvent toDeviceRemoved(HubEventProto proto) {
        DeviceRemovedEventProto p = proto.getDeviceRemoved();
        return DeviceRemovedEvent.builder()
                .hubId(proto.getHubId())
                .timestamp(Instant.ofEpochSecond(proto.getTimestamp().getSeconds(), proto.getTimestamp().getNanos()))
                .id(p.getId())
                .build();
    }

    private ScenarioAddedEvent toScenarioAdded(HubEventProto proto) {
        ScenarioAddedEventProto p = proto.getScenarioAdded();
        return ScenarioAddedEvent.builder()
                .hubId(proto.getHubId())
                .timestamp(Instant.ofEpochSecond(proto.getTimestamp().getSeconds(), proto.getTimestamp().getNanos()))
                .name(p.getName())
                .conditions(p.getConditionList().stream().map(this::toCondition).toList())
                .actions(p.getActionList().stream().map(this::toAction).toList())
                .build();
    }

    private ScenarioRemovedEvent toScenarioRemoved(HubEventProto proto) {
        ScenarioRemovedEventProto p = proto.getScenarioRemoved();
        return ScenarioRemovedEvent.builder()
                .hubId(proto.getHubId())
                .timestamp(Instant.ofEpochSecond(proto.getTimestamp().getSeconds(), proto.getTimestamp().getNanos()))
                .name(p.getName())
                .build();
    }

    private DeviceAction toAction(DeviceActionProto p) {
        return DeviceAction.builder()
                .sensorId(p.getSensorId())
                .type(ActionType.valueOf(p.getType().name()))
                .value(p.hasValue() ? p.getValue() : null)
                .build();
    }

    private ScenarioCondition toCondition(ScenarioConditionProto p) {
        int value = switch (p.getValueCase()) {
            case INT_VALUE   -> p.getIntValue();
            case BOOL_VALUE  -> p.getBoolValue() ? 1 : 0;
            case VALUE_NOT_SET -> 0;
        };

        return ScenarioCondition.builder()
                .sensorId(p.getSensorId())
                .conditionType(ConditionType.valueOf(p.getType().name()))
                .conditionOperation(ConditionOperation.valueOf(p.getOperation().name()))
                .value(value)
                .build();
    }

    // SensorEventProto -> SensorEvent

    public SensorEvent toDomain(SensorEventProto proto) {
        return switch (proto.getPayloadCase()) {
            case CLIMATE_SENSOR_EVENT     -> toClimate(proto);
            case LIGHT_SENSOR_EVENT       -> toLight(proto);
            case MOTION_SENSOR_EVENT      -> toMotion(proto);
            case SWITCH_SENSOR_EVENT      -> toSwitch(proto);
            case TEMPERATURE_SENSOR_EVENT -> toTemperature(proto);
            case PAYLOAD_NOT_SET          -> throw new IllegalArgumentException("Sensor payload not set");
        };
    }

    private ClimateSensorEvent toClimate(SensorEventProto proto) {
        ClimateSensorProto p = proto.getClimateSensorEvent();
        return ClimateSensorEvent.builder()
                .id(proto.getId())
                .hubId(proto.getHubId())
                .timestamp(Instant.ofEpochSecond(proto.getTimestamp().getSeconds(), proto.getTimestamp().getNanos()))
                .temperatureC(p.getTemperatureC())
                .humidity(p.getHumidity())
                .co2Level(p.getCo2Level())
                .build();
    }

    private LightSensorEvent toLight(SensorEventProto proto) {
        LightSensorProto p = proto.getLightSensorEvent();
        return LightSensorEvent.builder()
                .id(proto.getId())
                .hubId(proto.getHubId())
                .timestamp(Instant.ofEpochSecond(proto.getTimestamp().getSeconds(), proto.getTimestamp().getNanos()))
                .linkQuality(p.getLinkQuality())
                .luminosity(p.getLuminosity())
                .build();
    }

    private MotionSensorEvent toMotion(SensorEventProto proto) {
        MotionSensorProto p = proto.getMotionSensorEvent();
        return MotionSensorEvent.builder()
                .id(proto.getId())
                .hubId(proto.getHubId())
                .timestamp(Instant.ofEpochSecond(proto.getTimestamp().getSeconds(), proto.getTimestamp().getNanos()))
                .linkQuality(p.getLinkQuality())
                .motion(p.getMotion())
                .voltage(p.getVoltage())
                .build();
    }

    private SwitchSensorEvent toSwitch(SensorEventProto proto) {
        SwitchSensorProto p = proto.getSwitchSensorEvent();
        return SwitchSensorEvent.builder()
                .id(proto.getId())
                .hubId(proto.getHubId())
                .timestamp(Instant.ofEpochSecond(proto.getTimestamp().getSeconds(), proto.getTimestamp().getNanos()))
                .state(p.getState())
                .build();
    }

    private TemperatureSensorEvent toTemperature(SensorEventProto proto) {
        TemperatureSensorProto p = proto.getTemperatureSensorEvent();
        return TemperatureSensorEvent.builder()
                .id(proto.getId())
                .hubId(proto.getHubId())
                .timestamp(Instant.ofEpochSecond(proto.getTimestamp().getSeconds(), proto.getTimestamp().getNanos()))
                .temperatureC(p.getTemperatureC())
                .temperatureF(p.getTemperatureF())
                .build();
    }
}
