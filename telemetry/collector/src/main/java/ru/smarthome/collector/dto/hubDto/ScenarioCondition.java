package ru.smarthome.collector.dto.hubDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Getter
@Setter
@ToString(callSuper=true)
public class ScenarioCondition {

    @NotBlank
    String sensorId;

    @NotNull
    ConditionType conditionType;

    @NotNull
    ConditionOperation conditionOperation;

    @NotNull
    Integer value;
}
