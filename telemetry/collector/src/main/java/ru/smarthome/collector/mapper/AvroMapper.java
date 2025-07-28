package ru.smarthome.collector.mapper;


import org.apache.avro.generic.GenericRecord;
import org.springframework.stereotype.Component;
import ru.smarthome.collector.dto.hubDto.HubEvent;
import ru.smarthome.collector.dto.hubDto.DeviceAddedEvent;
import ru.smarthome.collector.dto.hubDto.DeviceRemovedEvent;
import ru.smarthome.collector.dto.hubDto.ScenarioAddedEvent;
import ru.smarthome.collector.dto.hubDto.ScenarioRemovedEvent;
import ru.smarthome.collector.dto.sensorDto.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.stream.Collectors;

@Component
public class AvroMapper {

    // HubEvent → Avro

    public GenericRecord toHubEvent(HubEvent dto) {
        GenericRecord payload = switch (dto.getType()) {
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

    private DeviceAddedEventAvro toDeviceAdded(DeviceAddedEvent dto) {
        return DeviceAddedEventAvro.newBuilder()
                .setId(dto.getId())
                .setType(DeviceTypeAvro.valueOf(dto.getDeviceType().name()))
                .build();
    }

    private DeviceRemovedEventAvro toDeviceRemoved(DeviceRemovedEvent dto) {
        return DeviceRemovedEventAvro.newBuilder()
                .setId(dto.getId())
                .build();
    }

    private ScenarioAddedEventAvro toScenarioAdded(ScenarioAddedEvent dto) {
        var avroConditions = dto.getConditions().stream()
                .map(c -> ScenarioConditionAvro.newBuilder()
                        .setSensorId(c.getSensorId())
                        .setType(ConditionTypeAvro.valueOf(c.getType().name()))
                        .setOperation(ConditionOperationAvro.valueOf(c.getOperation().name()))
                        .setValue(c.getValue())
                        .build())
                .collect(Collectors.toList());

        var avroActions = dto.getActions().stream()
                .map(a -> DeviceActionAvro.newBuilder()
                        .setSensorId(a.getSensorId())
                        .setType(ActionTypeAvro.valueOf(a.getType().name()))
                        .setValue(a.getValue() == null ? null : a.getValue())
                        .build())
                .collect(Collectors.toList());

        return ScenarioAddedEventAvro.newBuilder()
                .setName(dto.getName())
                .setConditions(avroConditions)
                .setActions(avroActions)
                .build();
    }

    private ScenarioRemovedEventAvro toScenarioRemoved(ScenarioRemovedEvent dto) {
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(dto.getName())
                .build();
    }


    // SensorEvent → Avro

    public GenericRecord toSensorEvent(SensorEvent dto) {
        GenericRecord payload = switch (dto.getType()) {
            case LIGHT_SENSOR_EVENT    -> toLightSensor((LightSensorEvent) dto);
            case SWITCH_SENSOR_EVENT   -> toSwitchSensor((SwitchSensorEvent) dto);
            case MOTION_SENSOR_EVENT   -> toMotionSensor((MotionSensorEvent) dto);
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

    private SwitchSensorAvro toSwitchSensor(SwitchSensorEvent e) {
        return SwitchSensorAvro.newBuilder()
                .setState(e.isState())
                .build();
    }

    private MotionSensorAvro toMotionSensor(MotionSensorEvent e) {
        return MotionSensorAvro.newBuilder()
                .setLinkQuality(e.getLinkQuality())
                .setMotion(e.isMotion())
                .setVoltage(e.getVoltage())
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
                .setTemperatureC(e.getTemperatureC())
                .setTemperatureF(e.getTemperatureF())
                .build();
    }
}