package com.aldisued.iot.monitoring.service;

import com.aldisued.iot.monitoring.dto.AlertDto;
import com.aldisued.iot.monitoring.entity.Alert;
import com.aldisued.iot.monitoring.repository.AlertRepository;
import com.aldisued.iot.monitoring.repository.SensorRepository;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AlertService {

  private final AlertRepository alertRepository;
  private final SensorRepository sensorRepository;
  private final KafkaTemplate<String, AlertDto> kafkaTemplate;
  public AlertService(AlertRepository alertRepository,
      SensorRepository sensorRepository,
      KafkaTemplate<String, AlertDto> kafkaTemplate) {
    this.alertRepository = alertRepository;
    this.sensorRepository = sensorRepository;
    this.kafkaTemplate = kafkaTemplate;
  }

  public Alert saveAlert(AlertDto alertDto) {
    final Alert alert = alertRepository.save(
        new Alert(
            alertDto.message(),
            alertDto.timestamp(),
            sensorRepository.getReferenceById(alertDto.sensorId())
        )
    );
    kafkaTemplate.send("alerts", alertDto);
    return alert;
  }

  public AlertDto findLastAlertBySensorId(UUID sensorId) {
    return alertRepository.findFirstBySensor_IdOrderByTimestampDesc(sensorId)
        .map(AlertDto::new)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }
}
