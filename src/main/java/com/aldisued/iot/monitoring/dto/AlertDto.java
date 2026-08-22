package com.aldisued.iot.monitoring.dto;

import com.aldisued.iot.monitoring.entity.Alert;
import java.time.LocalDateTime;
import java.util.UUID;

public record AlertDto(
    UUID sensorId,
    String message,
    LocalDateTime timestamp
) {
  public AlertDto(Alert alert) {
    this(
        alert.getSensor().getId(),
        alert.getMessage(),
        alert.getTimestamp()
    );
  }
}
