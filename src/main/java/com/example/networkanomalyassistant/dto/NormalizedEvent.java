package com.example.networkanomalyassistant.dto;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class NormalizedEvent {

    private String eventId;
    private Instant timestampUtc;
    private String sourceDeviceId;
    private String sourceInterfaceId;
    private String eventType; // METRIC, LOG, ALERT, CONFIG_CHANGE
    private String severity;  // CRITICAL, WARNING, INFO
    private Map<String, Object> payload;

    public NormalizedEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.timestampUtc = Instant.now();
    }

    public NormalizedEvent(String sourceDeviceId, String sourceInterfaceId, String eventType, String severity, Map<String, Object> payload) {
        this.eventId = UUID.randomUUID().toString();
        this.timestampUtc = Instant.now();
        this.sourceDeviceId = sourceDeviceId;
        this.sourceInterfaceId = sourceInterfaceId;
        this.eventType = eventType;
        this.severity = severity;
        this.payload = payload;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
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

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }
}