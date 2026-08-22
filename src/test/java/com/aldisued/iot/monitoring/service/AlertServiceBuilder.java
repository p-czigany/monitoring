package com.aldisued.iot.monitoring.service;

import com.aldisued.iot.monitoring.repository.AlertRepository;

public class AlertServiceBuilder {
  private AlertRepository alertRepository;

  public AlertServiceBuilder alertRepository(AlertRepository alertRepository) {
    this.alertRepository = alertRepository;
    return this;
  }

  public AlertService build() {
    return new AlertService(
        this.alertRepository,
        null,
        null
    );
  }
}
