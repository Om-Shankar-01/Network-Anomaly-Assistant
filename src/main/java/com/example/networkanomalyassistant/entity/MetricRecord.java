package com.example.networkanomalyassistant.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "metric_records", indexes = {
        @Index(name = "idx_metric_device_time", columnList = "device_id, timestamp_utc")
})
public class MetricRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "timestamp_utc", nullable = false)
    private Instant timestampUtc;

    @Column(name = "device_id", nullable = false)
    private String sourceDeviceId;

    @Column(name = "interface_id")
    private String sourceInterfaceId;

    @Column(name = "metric_name", nullable = false)
    private String metricName;

    @Column(name = "metric_value", nullable = false)
    private Double metricValue;

    public MetricRecord() {}

    public MetricRecord(Instant timestampUtc, String sourceDeviceId, String sourceInterfaceId, String metricName, Double metricValue) {
        this.timestampUtc = timestampUtc;
        this.sourceDeviceId = sourceDeviceId;
        this.sourceInterfaceId = sourceInterfaceId;
        this.metricName = metricName;
        this.metricValue = metricValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getTimestampUtc() {
        return timestampUtc;
    }

    public void setTimestampUtc(Instant timestampUtc) {
        this.timestampUtc = timestampUtc;
    }

    public String getSourceDeviceId() {
        return sourceDeviceId;
    }

    public void setSourceDeviceId(String sourceDeviceId) {
        this.sourceDeviceId = sourceDeviceId;
    }

    public String getSourceInterfaceId() {
        return sourceInterfaceId;
    }

    public void setSourceInterfaceId(String sourceInterfaceId) {
        this.sourceInterfaceId = sourceInterfaceId;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public Double getMetricValue() {
        return metricValue;
    }

    public void setMetricValue(Double metricValue) {
        this.metricValue = metricValue;
    }
}
