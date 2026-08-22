package com.aldisued.iot.monitoring.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.aldisued.iot.monitoring.dto.SensorDto;
import com.aldisued.iot.monitoring.entity.SensorType;
import com.aldisued.iot.monitoring.exception.SensorNameAlreadyExistsException;
import com.aldisued.iot.monitoring.repository.SensorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SensorServiceTest {

  private @Mock SensorRepository sensorRepository;

  @Test
  void throwsSensorNameAlreadyExistsException() {
    when(sensorRepository.existsByName("Test Name")).thenReturn(Boolean.TRUE);

    assertThatThrownBy(() -> new SensorService(sensorRepository).saveSensor(
        new SensorDto("Test Name", SensorType.TEMPERATURE)
    )).isInstanceOf(SensorNameAlreadyExistsException.class);
  }
}
