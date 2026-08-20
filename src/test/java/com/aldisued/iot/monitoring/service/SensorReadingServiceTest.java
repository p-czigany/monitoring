package com.aldisued.iot.monitoring.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aldisued.iot.monitoring.dto.SensorReadingDto;
import com.aldisued.iot.monitoring.entity.Sensor;
import com.aldisued.iot.monitoring.entity.SensorReading;
import com.aldisued.iot.monitoring.repository.SensorReadingRepository;
import com.aldisued.iot.monitoring.repository.SensorRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SensorReadingServiceTest {

  private final static Double SENSOR_VALUE = 2.5;

  private @Mock SensorRepository sensorRepository;
  private @Mock SensorReadingRepository sensorReadingRepository;
  private @Mock SensorReading persistedReading;

  private UUID sensorId;
  private SensorReadingDto sensorReadingDto;

  @BeforeEach
  void init() {
    sensorId = UUID.randomUUID();
    sensorReadingDto =
        new SensorReadingDto(sensorId, SENSOR_VALUE, LocalDateTime.now());
  }

  @Test
  void usesSensorIdFromDto() {
    when(sensorRepository.getReferenceById(sensorId))
        .thenReturn(new Sensor(sensorId));

    new SensorReadingService(sensorReadingRepository, sensorRepository)
        .saveSensorReading(sensorReadingDto);

    verify(sensorRepository).getReferenceById(sensorId);
  }

  @Test
  void obtainsSensorReferenceBeforeSaving() {
    when(sensorRepository.getReferenceById(sensorId))
        .thenReturn(new Sensor(sensorId));

    new SensorReadingService(sensorReadingRepository, sensorRepository)
        .saveSensorReading(sensorReadingDto);

    InOrder inOrder = inOrder(sensorRepository, sensorReadingRepository);
    inOrder.verify(sensorRepository).getReferenceById(sensorId);
    inOrder.verify(sensorReadingRepository).save(any(SensorReading.class));
  }

  @Test
  void constructsSensorReadingWithCorrectValueAndTimestamp() {
    LocalDateTime timestamp = LocalDateTime.of(2026, 8, 20, 12, 30);
    when(sensorRepository.getReferenceById(sensorId))
        .thenReturn(new Sensor(sensorId));

    new SensorReadingService(sensorReadingRepository, sensorRepository)
        .saveSensorReading(
            new SensorReadingDto(sensorId, SENSOR_VALUE, timestamp)
        );

    ArgumentCaptor<SensorReading> captor =
        ArgumentCaptor.forClass(SensorReading.class);
    verify(sensorReadingRepository).save(captor.capture());
    SensorReading savedReading = captor.getValue();
    assertThat(savedReading.getValue()).isEqualTo(SENSOR_VALUE);
    assertThat(savedReading.getTimestamp()).isEqualTo(timestamp);
  }

  @Test
  void assignsSensorReferenceToSensorReading() {
    Sensor sensor = new Sensor(sensorId);
    when(sensorRepository.getReferenceById(sensorId)).thenReturn(sensor);

    new SensorReadingService(sensorReadingRepository, sensorRepository)
        .saveSensorReading(sensorReadingDto);

    ArgumentCaptor<SensorReading> captor =
        ArgumentCaptor.forClass(SensorReading.class);
    verify(sensorReadingRepository).save(captor.capture());
    assertThat(captor.getValue().getSensor()).isSameAs(sensor);
  }

  @Test
  void callsSensorReadingRepositorySave() {
    when(sensorRepository.getReferenceById(sensorId))
        .thenReturn(new Sensor(sensorId));

    new SensorReadingService(sensorReadingRepository, sensorRepository)
        .saveSensorReading(sensorReadingDto);

    verify(sensorReadingRepository).save(any(SensorReading.class));
  }

  @Test
  void returnsEntityReturnedByRepository() {
    when(sensorRepository.getReferenceById(sensorId))
        .thenReturn(new Sensor(sensorId));
    when(sensorReadingRepository.save(any(SensorReading.class)))
        .thenReturn(persistedReading);

    assertThat(
        new SensorReadingService(sensorReadingRepository, sensorRepository)
            .saveSensorReading(sensorReadingDto)
    ).isSameAs(persistedReading);
  }
}
