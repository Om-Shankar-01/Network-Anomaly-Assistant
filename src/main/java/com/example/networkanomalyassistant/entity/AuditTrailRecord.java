package com.example.networkanomalyassistant.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_trail_records")
public class AuditTrailRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "audit_id", nullable = false, unique = true)
    private String auditId;

    @Column(name = "timestamp_utc", nullable = false)
    private Instant timestampUtc;

    @Column(name = "incident_id", nullable = false)
    private String incidentId;

    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Column(name = "event_type", nullable = false)
    private String eventType; // e.g., ANOMALY_DETECTED, HYPOTHESIS_UPDATED, CONFIDENCE_BOOSTED, REPORT_GENERATED

    @Column(name = "previous_hypothesis")
    private String previousHypothesis;

    @Column(name = "new_hypothesis")
    private String newHypothesis;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "trigger_source")
    private String triggerSource; // e.g., BAYESIAN_ENGINE, SIGNAL_MONITOR, LLM_ASSISTANT

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    public AuditTrailRecord() {
        this.auditId = UUID.randomUUID().toString();
        this.timestampUtc = Instant.now();
    }

    public AuditTrailRecord(String incidentId, String deviceId, String eventType, String previousHypothesis, String newHypothesis, Double confidenceScore, String triggerSource, String details) {
        this.auditId = UUID.randomUUID().toString();
        this.timestampUtc = Instant.now();
        this.incidentId = incidentId;
        this.deviceId = deviceId;
        this.eventType = eventType;
        this.previousHypothesis = previousHypothesis;
        this.newHypothesis = newHypothesis;
        this.confidenceScore = confidenceScore;
        this.triggerSource = triggerSource;
        this.details = details;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuditId() {
        return auditId;
    }

    public void setAuditId(String auditId) {
        this.auditId = auditId;
    }

    public Instant getTimestampUtc() {
        return timestampUtc;
    }

    public void setTimestampUtc(Instant timestampUtc) {
        this.timestampUtc = timestampUtc;
    }

    public String getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(String incidentId) {
        this.incidentId = incidentId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getPreviousHypothesis() {
        return previousHypothesis;
    }

    public void setPreviousHypothesis(String previousHypothesis) {
        this.previousHypothesis = previousHypothesis;
    }

    public String getNewHypothesis() {
        return newHypothesis;
    }

    public void setNewHypothesis(String newHypothesis) {
        this.newHypothesis = newHypothesis;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getTriggerSource() {
        return triggerSource;
    }

    public void setTriggerSource(String triggerSource) {
        this.triggerSource = triggerSource;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}