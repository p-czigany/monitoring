package com.aldisued.iot.monitoring.entity;

import java.util.UUID;

public class SensorFactory {
  public Sensor testSensor(final UUID sensorId) {
    return new Sensor(
        sensorId,
        "temperature-sensor",
        SensorType.TEMPERATURE,
        null,
        null
    );
  }
}
