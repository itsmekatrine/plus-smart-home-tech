package ru.smarthome.collector.dto.hubDto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

@SuperBuilder(toBuilder = true)
@Getter
@Setter
@ToString(callSuper=true)
public class DeviceAddedEvent extends HubEvent {
    private String id;
    private DeviceType deviceType;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }
}
