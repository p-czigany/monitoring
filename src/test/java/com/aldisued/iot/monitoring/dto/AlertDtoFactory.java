package com.aldisued.iot.monitoring.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

public class AlertDtoFactory {

  public AlertDto alertDto(UUID sensorId) {
    return new AlertDto(
        sensorId,
        "Temperature too high",
        LocalDateTime.now()
    );
  }

  public AlertDto alertDto(
      final UUID sensorId,
      final String message,
      final LocalDateTime timestamp
  ) {
    return new AlertDto(sensorId, message, timestamp);
  }
}
