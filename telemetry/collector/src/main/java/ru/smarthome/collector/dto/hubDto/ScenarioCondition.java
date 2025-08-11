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
    private String sensorId;

    @NotNull
    private ConditionType conditionType;

    @NotNull
    private ConditionOperation conditionOperation;

    private Integer value;
}
