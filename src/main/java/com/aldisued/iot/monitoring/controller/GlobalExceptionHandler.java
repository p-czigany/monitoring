package com.aldisued.iot.monitoring.controller;

import com.aldisued.iot.monitoring.exception.SensorNameAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(SensorNameAlreadyExistsException.class)
  public ResponseEntity<String> handleSensorNameAlreadyExists(
      final SensorNameAlreadyExistsException ex
  ) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }
}
