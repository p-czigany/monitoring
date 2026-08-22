package com.aldisued.iot.monitoring.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aldisued.iot.monitoring.dto.AlertDto;
import com.aldisued.iot.monitoring.dto.AlertDtoFactory;
import com.aldisued.iot.monitoring.entity.Alert;
import com.aldisued.iot.monitoring.entity.AlertFactory;
import com.aldisued.iot.monitoring.entity.Sensor;
import com.aldisued.iot.monitoring.entity.SensorFactory;
import com.aldisued.iot.monitoring.repository.AlertRepository;
import com.aldisued.iot.monitoring.repository.SensorRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

  private @Mock AlertRepository alertRepository;
  private @Mock SensorRepository sensorRepository;
  private @Mock KafkaTemplate<String, AlertDto> kafkaTemplate;

  private UUID sensorId;

  @BeforeEach
  void init() {
    sensorId = UUID.randomUUID();
  }

  @Nested
  class FindLastAlertBySensorId {

    @Test
    void usesSensorIdToFindLatestAlert() {
      when(alertRepository.findFirstBySensor_IdOrderByTimestampDesc(sensorId))
          .thenReturn(Optional.of(new AlertFactory().alert(sensorId)));

      new AlertServiceBuilder().alertRepository(alertRepository).build()
          .findLastAlertBySensorId(sensorId);

      verify(alertRepository)
          .findFirstBySensor_IdOrderByTimestampDesc(sensorId);
    }

    @Test
    void mapsLatestAlertToDto() {
      final LocalDateTime timestamp = LocalDateTime.of(2026, 8, 22, 14, 30);
      final String message = "Temperature too high";
      when(alertRepository.findFirstBySensor_IdOrderByTimestampDesc(sensorId))
          .thenReturn(Optional.of(
              new AlertFactory().alert(sensorId, timestamp, message)
          ));

      assertThat(
          new AlertServiceBuilder().alertRepository(alertRepository).build()
              .findLastAlertBySensorId(sensorId)
      ).isEqualTo(new AlertDtoFactory().alertDto(sensorId, message, timestamp));
    }

    @Test
    void throwsNotFoundWhenSensorHasNoAlerts() {
      when(alertRepository.findFirstBySensor_IdOrderByTimestampDesc(sensorId))
          .thenReturn(Optional.empty());

      assertThatThrownBy(
          () -> new AlertServiceBuilder().alertRepository(alertRepository)
              .build().findLastAlertBySensorId(sensorId)
      ).isInstanceOfSatisfying(
          ResponseStatusException.class,
          exception -> assertThat(exception.getStatusCode())
              .isEqualTo(HttpStatus.NOT_FOUND)
      );
    }
  }

  @Nested
  class SaveAlert {

    @Test
    void usesSensorIdFromDto() {
      final AlertDto dto = new AlertDtoFactory().alertDto(sensorId);
      when(sensorRepository.getReferenceById(sensorId))
          .thenReturn(new SensorFactory().testSensor(sensorId));
      when(kafkaTemplate.send("alerts", dto))
          .thenReturn(CompletableFuture.completedFuture(null));

      new AlertService(alertRepository, sensorRepository, kafkaTemplate)
          .saveAlert(dto);

      verify(sensorRepository).getReferenceById(sensorId);
    }

    @Test
    void savesAlertWithCorrectValues() {
      final Sensor sensor = new SensorFactory().testSensor(sensorId);
      final LocalDateTime timestamp = LocalDateTime.of(2026, 8, 22, 15, 30);
      final String message = "Temperature too high";
      final AlertDto dto =
          new AlertDtoFactory().alertDto(sensorId, message, timestamp);
      when(sensorRepository.getReferenceById(sensorId)).thenReturn(sensor);
      when(kafkaTemplate.send("alerts", dto))
          .thenReturn(CompletableFuture.completedFuture(null));

      new AlertService(alertRepository, sensorRepository, kafkaTemplate)
          .saveAlert(dto);

      final ArgumentCaptor<Alert> captor = ArgumentCaptor.forClass(Alert.class);
      verify(alertRepository).save(captor.capture());
      assertThat(captor.getValue().getMessage()).isEqualTo(message);
      assertThat(captor.getValue().getSensor()).isEqualTo(sensor);
      assertThat(captor.getValue().getTimestamp()).isEqualTo(timestamp);
    }

    @Test
    void publishesAlertDtoToAlertsTopic() {
      final AlertDto dto = new AlertDtoFactory().alertDto(sensorId);
      when(sensorRepository.getReferenceById(sensorId))
          .thenReturn(new SensorFactory().testSensor(sensorId));
      when(kafkaTemplate.send("alerts", dto))
          .thenReturn(CompletableFuture.completedFuture(null));

      new AlertService(alertRepository, sensorRepository, kafkaTemplate)
          .saveAlert(dto);

      verify(kafkaTemplate).send("alerts", dto);
    }

    @Test
    void returnsSavedAlert() {
      final Sensor sensor = new SensorFactory().testSensor(sensorId);
      final AlertDto dto = new AlertDtoFactory().alertDto(sensorId);
      final Alert persistedAlert = new AlertFactory().alert(
          dto.message(),
          dto.timestamp(),
          sensor
      );
      when(sensorRepository.getReferenceById(sensorId)).thenReturn(sensor);
      when(alertRepository.save(any(Alert.class))).thenReturn(persistedAlert);
      when(kafkaTemplate.send("alerts", dto))
          .thenReturn(CompletableFuture.completedFuture(null));

      assertThat(
          new AlertService(alertRepository, sensorRepository, kafkaTemplate)
              .saveAlert(dto)
      ).isSameAs(persistedAlert);
    }

    @Test
    void doesNotPublishIfPersistenceFails() {
      when(sensorRepository.getReferenceById(sensorId))
          .thenReturn(new SensorFactory().testSensor(sensorId));
      when(alertRepository.save(any(Alert.class)))
          .thenThrow(new RuntimeException());

      assertThatThrownBy(
          () -> new AlertService(
              alertRepository,
              sensorRepository,
              kafkaTemplate
          ).saveAlert(new AlertDtoFactory().alertDto(sensorId))
      ).isInstanceOf(RuntimeException.class);

      verify(kafkaTemplate, never()).send(anyString(), any(AlertDto.class));
    }
  }
}
