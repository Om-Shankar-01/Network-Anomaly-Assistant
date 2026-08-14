package com.example.networkanomalyassistant.dto;

import java.time.Instant;

public class AuditRecordDto {

    private String auditId;
    private Instant timestampUtc;
    private String incidentId;
    private String deviceId;
    private String eventType;
    private String previousHypothesis;
    private String newHypothesis;
    private Double confidenceScore;
    private String triggerSource;
    private String details;

    public AuditRecordDto() {}

    public AuditRecordDto(String auditId, Instant timestampUtc, String incidentId, String deviceId, String eventType, String previousHypothesis, String newHypothesis, Double confidenceScore, String triggerSource, String details) {
        this.auditId = auditId;
        this.timestampUtc = timestampUtc;
        this.incidentId = incidentId;
        this.deviceId = deviceId;
        this.eventType = eventType;
        this.previousHypothesis = previousHypothesis;
        this.newHypothesis = newHypothesis;
        this.confidenceScore = confidenceScore;
        this.triggerSource = triggerSource;
        this.details = details;
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
