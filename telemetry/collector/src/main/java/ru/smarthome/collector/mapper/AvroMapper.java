package ru.smarthome.collector.mapper;

import org.springframework.stereotype.Component;
import ru.smarthome.collector.dto.hubDto.HubEvent;
import ru.smarthome.collector.dto.hubDto.DeviceAddedEvent;
import ru.smarthome.collector.dto.hubDto.DeviceRemovedEvent;
import ru.smarthome.collector.dto.hubDto.ScenarioAddedEvent;
import ru.smarthome.collector.dto.hubDto.ScenarioRemovedEvent;
import ru.smarthome.collector.dto.sensorDto.*;
import ru.yandex.practicum.kafka.telemetry.event.*;


@Component
public class AvroMapper {

    // HubEvent → Avro

    public HubEventAvro toAvro(HubEvent dto) {
        Object payload = switch (dto.getType()) {
            case DEVICE_ADDED    -> toDeviceAdded((DeviceAddedEvent) dto);
            case DEVICE_REMOVED  -> toDeviceRemoved((DeviceRemovedEvent) dto);
            case SCENARIO_ADDED  -> toScenarioAdded((ScenarioAddedEvent) dto);
            case SCENARIO_REMOVED-> toScenarioRemoved((ScenarioRemovedEvent) dto);
        };

        return HubEventAvro.newBuilder()
                .setHubId(dto.getHubId())
                .setTimestamp(dto.getTimestamp())
                .setPayload(payload)
                .build();
    }

    private DeviceAddedEventAvro toDeviceAdded(DeviceAddedEvent e) {
        return DeviceAddedEventAvro.newBuilder()
                .setId(e.getId())
                .setType(DeviceTypeAvro.valueOf(e.getDeviceType().name()))
                .build();
    }

    private DeviceRemovedEventAvro toDeviceRemoved(DeviceRemovedEvent e) {
        return DeviceRemovedEventAvro.newBuilder()
                .setId(e.getId())
                .build();
    }

    private ScenarioAddedEventAvro toScenarioAdded(ScenarioAddedEvent dto) {
        var conditions = dto.getConditions().stream()
                .map(c -> ScenarioConditionAvro.newBuilder()
                        .setSensorId(c.getSensorId())
                        .setType(ConditionTypeAvro.valueOf(c.getType().name()))
                        .setOperation(ConditionOperationAvro.valueOf(c.getOperation().name()))
                        .setValue(c.getValue())
                        .build())
                .toList();

        var actions = dto.getActions().stream()
                .map(a -> DeviceActionAvro.newBuilder()
                        .setSensorId(a.getSensorId())
                        .setType(ActionTypeAvro.valueOf(a.getType().name()))
                        .setValue(a.getValue())
                        .build())
                .toList();

        return ScenarioAddedEventAvro.newBuilder()
                .setName(dto.getName())
                .setConditions(conditions)
                .setActions(actions)
                .build();
    }

    private ScenarioRemovedEventAvro toScenarioRemoved(ScenarioRemovedEvent e) {
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(e.getName())
                .build();
    }


    // SensorEvent → Avro

    public SensorEventAvro toAvro(SensorEvent dto) {
        Object payload = switch (dto.getType()) {
            case LIGHT_SENSOR_EVENT    -> toLightSensor((LightSensorEvent) dto);
            case MOTION_SENSOR_EVENT   -> toMotionSensor((MotionSensorEvent) dto);
            case SWITCH_SENSOR_EVENT   -> toSwitchSensor((SwitchSensorEvent) dto);
            case CLIMATE_SENSOR_EVENT  -> toClimateSensor((ClimateSensorEvent) dto);
            case TEMPERATURE_SENSOR_EVENT -> toTemperatureSensor((TemperatureSensorEvent) dto);
        };

        return SensorEventAvro.newBuilder()
                .setId(dto.getId())
                .setHubId(dto.getHubId())
                .setTimestamp(dto.getTimestamp())
                .setPayload(payload)
                .build();
    }


    private LightSensorAvro toLightSensor(LightSensorEvent e) {
        return LightSensorAvro.newBuilder()
                .setLinkQuality(e.getLinkQuality())
                .setLuminosity(e.getLuminosity())
                .build();
    }

    private MotionSensorAvro toMotionSensor(MotionSensorEvent e) {
        return MotionSensorAvro.newBuilder()
                .setLinkQuality(e.getLinkQuality())
                .setMotion(e.isMotion())
                .setVoltage(e.getVoltage())
                .build();
    }

    private SwitchSensorAvro toSwitchSensor(SwitchSensorEvent e) {
        return SwitchSensorAvro.newBuilder()
                .setState(e.isState())
                .build();
    }

    private ClimateSensorAvro toClimateSensor(ClimateSensorEvent e) {
        return ClimateSensorAvro.newBuilder()
                .setTemperatureC(e.getTemperatureC())
                .setHumidity(e.getHumidity())
                .setCo2Level(e.getCo2Level())
                .build();
    }

    private TemperatureSensorAvro toTemperatureSensor(TemperatureSensorEvent e) {
        return TemperatureSensorAvro.newBuilder()
                .setId(e.getId())
                .setHubId(e.getHubId())
                .setTimestamp(e.getTimestamp())
                .setTemperatureC(e.getTemperatureC())
                .setTemperatureF(e.getTemperatureF())
                .build();
    }
}