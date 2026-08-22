package com.aldisued.iot.monitoring.dto;

import com.aldisued.iot.monitoring.entity.SensorType;
import jakarta.validation.constraints.NotNull;

public record SensorDto(
    @NotNull String name,
    SensorType type
) {
}
