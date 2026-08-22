package com.aldisued.iot.monitoring.exception;

public class SensorNameAlreadyExistsException extends RuntimeException {
  public SensorNameAlreadyExistsException(String name) {
    super("Sensor with name '%s' already exists".formatted(name));
  }
}
