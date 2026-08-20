package com.aldisued.iot.monitoring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aldisued.iot.monitoring.entity.Sensor;
import com.aldisued.iot.monitoring.entity.SensorReading;
import com.aldisued.iot.monitoring.entity.SensorType;
import com.aldisued.iot.monitoring.repository.SensorReadingRepository;
import com.aldisued.iot.monitoring.repository.SensorRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

public class ReceiveNewReadingITCase extends IntegrationTestBase {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private SensorRepository sensorRepository;

  @Autowired
  private SensorReadingRepository sensorReadingRepository;

  @BeforeEach
  void cleanDatabase() {
    sensorReadingRepository.deleteAll();
    sensorRepository.deleteAll();
  }

  @Test
  void postSensorReadingPersistsReading() throws Exception {
    Sensor sensor = sensorRepository.save(
        new Sensor("sensor-1", SensorType.TEMPERATURE)
    );

    mockMvc.perform(post("/sensor-readings")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "sensorId": "%s",
                  "value": 23.5,
                  "timestamp": "2026-08-20T10:00:00"
                }
                """.formatted(sensor.getId())))
        .andExpect(status().is2xxSuccessful());

    List<SensorReading> readings = sensorReadingRepository.findAll();

    assertThat(readings).hasSize(1);

    SensorReading reading = readings.getFirst();
    assertThat(reading.getValue()).isEqualTo(23.5);
    assertThat(reading.getTimestamp())
        .isEqualTo(LocalDateTime.parse("2026-08-20T10:00:00"));
    assertThat(reading.getSensor().getId()).isEqualTo(sensor.getId());
  }
}
