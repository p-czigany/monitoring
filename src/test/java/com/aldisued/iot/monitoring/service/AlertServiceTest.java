package com.aldisued.iot.monitoring.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aldisued.iot.monitoring.dto.AlertDto;
import com.aldisued.iot.monitoring.entity.AlertFactory;
import com.aldisued.iot.monitoring.repository.AlertRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

  private @Mock AlertRepository alertRepository;

  private UUID sensorId;

  @BeforeEach
  void init() {
    sensorId = UUID.randomUUID();
  }

  @Test
  void usesSensorIdToFindLatestAlert() {
    when(alertRepository.findFirstBySensor_IdOrderByTimestampDesc(sensorId))
        .thenReturn(Optional.of(new AlertFactory().alert(sensorId)));

    new AlertServiceBuilder().alertRepository(alertRepository).build()
        .findLastAlertBySensorId(sensorId);

    verify(alertRepository).findFirstBySensor_IdOrderByTimestampDesc(sensorId);
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
    ).isEqualTo(new AlertDto(sensorId, message, timestamp));
  }

  @Test
  void throwsNotFoundWhenSensorHasNoAlerts() {
    when(alertRepository.findFirstBySensor_IdOrderByTimestampDesc(sensorId))
        .thenReturn(Optional.empty());

    assertThatThrownBy(
        () -> new AlertServiceBuilder().alertRepository(alertRepository).build()
            .findLastAlertBySensorId(sensorId)
    ).isInstanceOfSatisfying(
        ResponseStatusException.class,
        exception -> assertThat(exception.getStatusCode())
            .isEqualTo(HttpStatus.NOT_FOUND)
    );
  }
}
