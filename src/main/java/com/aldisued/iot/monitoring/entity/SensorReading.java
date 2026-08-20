package com.aldisued.iot.monitoring.entity;

import com.aldisued.iot.monitoring.dto.SensorReadingDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Table(name = "sensor_readings")
@Entity
public class SensorReading {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private Double value;

  @Column(nullable = false)
  private LocalDateTime timestamp;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "sensor_id", nullable = false)
  private Sensor sensor;

  public SensorReading() {
    this(null, null, null, null);
  }

  public SensorReading(
      SensorReadingDto sensorReadingDto,
      Sensor sensor
  ) {
    this(
        null,
        valueFromDto(sensorReadingDto),
        timestampFromDto(sensorReadingDto),
        sensor
    );
  }

  public SensorReading(
      Double value,
      LocalDateTime timestamp,
      Sensor sensor
  ) {
    this(null, value, timestamp, sensor);
  }

  public SensorReading(
      Long id,
      Double value,
      LocalDateTime timestamp,
      Sensor sensor
  ) {
    this.id = id;
    this.value = value;
    this.timestamp = timestamp;
    this.sensor = sensor;
  }

  private static Double valueFromDto(SensorReadingDto sensorReadingDto) {
    return sensorReadingDto.value();
  }

  private static LocalDateTime timestampFromDto(
      SensorReadingDto sensorReadingDto
  ) {
    return sensorReadingDto.timestamp();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Double getValue() {
    return value;
  }

  public void setValue(Double value) {
    this.value = value;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public Sensor getSensor() {
    return sensor;
  }

  public void setSensor(Sensor sensor) {
    this.sensor = sensor;
  }
}
