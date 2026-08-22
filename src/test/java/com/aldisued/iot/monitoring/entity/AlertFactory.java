package com.aldisued.iot.monitoring.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class AlertFactory {

  public Alert alert(final UUID sensorId) {
    return new Alert(
        1L,
        "Temperature too high",
        LocalDateTime.now(),
        new SensorFactory().testSensor(sensorId)
    );
  }

  public Alert alert(final UUID sensorId, final LocalDateTime timestamp) {
    return new Alert(
        1L,
        "Temperature too high",
        timestamp,
        new SensorFactory().testSensor(sensorId)
    );
  }

  public Alert alert(
      final UUID sensorId,
      final LocalDateTime timestamp,
      final String message
  ) {
    return new Alert(
        1L,
        message,
        timestamp,
        new SensorFactory().testSensor(sensorId)
    );
  }

  public Alert alert(
      final String message,
      final LocalDateTime timestamp,
      final Sensor sensor
  ) {
    return new Alert(42L, message, timestamp, sensor);
  }
}
