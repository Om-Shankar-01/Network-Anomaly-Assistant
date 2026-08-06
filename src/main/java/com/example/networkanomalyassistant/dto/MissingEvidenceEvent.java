package com.example.networkanomalyassistant.dto;

import java.time.Instant;
import java.util.UUID;

public class MissingEvidenceEvent {

    private String eventId;
    private String sourceDeviceId;
    private String sourceInterfaceId;
    private String missingSignalType; // e.g., "HEARTBEAT_GAP", "METRIC_WINDOW_MISSING", "SNMP_POLL_TIMEOUT"
    private Instant lastSeenTimestamp;
    private long secondsOverdue;
    private String evidenceCategory;  // ALWAYS "MISSING_EVIDENCE"
    private String severity;          // CRITICAL, WARNING

    public MissingEvidenceEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.evidenceCategory = "MISSING_EVIDENCE";
    }

    public MissingEvidenceEvent(String sourceDeviceId, String sourceInterfaceId, String missingSignalType, Instant lastSeenTimestamp, long secondsOverdue, String severity) {
        this.eventId = UUID.randomUUID().toString();
        this.sourceDeviceId = sourceDeviceId;
        this.sourceInterfaceId = sourceInterfaceId;
        this.missingSignalType = missingSignalType;
        this.lastSeenTimestamp = lastSeenTimestamp;
        this.secondsOverdue = secondsOverdue;
        this.evidenceCategory = "MISSING_EVIDENCE";
        this.severity = severity;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
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

    public String getMissingSignalType() {
        return missingSignalType;
    }

    public void setMissingSignalType(String missingSignalType) {
        this.missingSignalType = missingSignalType;
    }

    public Instant getLastSeenTimestamp() {
        return lastSeenTimestamp;
    }

    public void setLastSeenTimestamp(Instant lastSeenTimestamp) {
        this.lastSeenTimestamp = lastSeenTimestamp;
    }

    public long getSecondsOverdue() {
        return secondsOverdue;
    }

    public void setSecondsOverdue(long secondsOverdue) {
        this.secondsOverdue = secondsOverdue;
    }

    public String getEvidenceCategory() {
        return evidenceCategory;
    }

    public void setEvidenceCategory(String evidenceCategory) {
        this.evidenceCategory = evidenceCategory;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }
}