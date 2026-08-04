package com.example.networkanomalyassistant.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "incident_records")
public class IncidentRecord {

    @Id
    @Column(name = "incident_id")
    private String incidentId;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @Column(name = "primary_device_id")
    private String primaryDeviceId;

    @Column(name = "status")
    private String status;   // OPEN, INVESTIGATING, RESOLVED

    @Column(name = "severity")
    private String severity; // CRITICAL, WARNING, INFO

    @Column(name = "root_cause_summary")
    private String rootCauseSummary;

    public IncidentRecord() {
        this.incidentId = "INC-" + UUID.randomUUID().toString().substring(0,8).toUpperCase();
        this.startTime = Instant.now();
        this.status = "OPEN";
    }

    public IncidentRecord(String primaryDeviceId, String severity, String rootCauseSummary) {
        this.incidentId = "INC-" + UUID.randomUUID().toString().substring(0,8).toUpperCase();
        this.startTime = Instant.now();
        this.status = "OPEN";
        this.primaryDeviceId = primaryDeviceId;
        this.severity = severity;
        this.rootCauseSummary = rootCauseSummary;
    }

    public String getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(String incidentId) {
        this.incidentId = incidentId;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public String getPrimaryDeviceId() {
        return primaryDeviceId;
    }

    public void setPrimaryDeviceId(String primaryDeviceId) {
        this.primaryDeviceId = primaryDeviceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getRootCauseSummary() {
        return rootCauseSummary;
    }

    public void setRootCauseSummary(String rootCauseSummary) {
        this.rootCauseSummary = rootCauseSummary;
    }
}
