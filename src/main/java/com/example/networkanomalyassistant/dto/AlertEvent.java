package com.example.networkanomalyassistant.dto;

import java.time.Instant;
import java.util.UUID;

public class AlertEvent {
    
    private String alertId;
    private String sourceDeviceId;
    private String sourceInterfaceId;
    private String alertName;       // e.g., "LINK_DOWN", "HIGH_CPU", "BGP_NEIGHBOR_LOSS"
    private String alertSource;     // SNMP_TRAP, SYSLOG, PROMETHEUS
    private String severity;        // CRITICAL, WARNING, INFO
    private Instant firstSeen;
    private Instant lastSeen;
    private int occurrenceCount;
    private String rawPayload;

    public AlertEvent() {
        this.alertId = UUID.randomUUID().toString();
        this.firstSeen = Instant.now();
        this.lastSeen = Instant.now();
        this.occurrenceCount = 1;
    }

    public AlertEvent(String sourceDeviceId, String sourceInterfaceId, String alertName, String alertSource,
            String severity, String rawPayload) {

        this.alertId = UUID.randomUUID().toString();
        this.sourceDeviceId = sourceDeviceId;
        this.sourceInterfaceId = sourceInterfaceId;
        this.alertName = alertName;
        this.alertSource = alertSource;
        this.severity = severity;
        this.firstSeen = Instant.now();
        this.lastSeen = Instant.now();
        this.occurrenceCount = 1;
        this.rawPayload = rawPayload;
        
    }

    public String getFingerprintKey() {
        return String.format("%s::%s::%s", 
            sourceDeviceId != null ? sourceDeviceId : "UNKNOWN",
            sourceInterfaceId != null ? sourceInterfaceId : "GLOBAL",
            alertName != null ? alertName : "GENERIC_ALERT"
        );
    }

    public void incrementOccurrence() {
        this.occurrenceCount++;
        this.lastSeen = Instant.now();
    }

    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
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

    public String getAlertName() {
        return alertName;
    }

    public void setAlertName(String alertName) {
        this.alertName = alertName;
    }

    public String getAlertSource() {
        return alertSource;
    }

    public void setAlertSource(String alertSource) {
        this.alertSource = alertSource;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public Instant getFirstSeen() {
        return firstSeen;
    }

    public void setFirstSeen(Instant firstSeen) {
        this.firstSeen = firstSeen;
    }

    public Instant getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Instant lastSeen) {
        this.lastSeen = lastSeen;
    }

    public int getOccurrenceCount() {
        return occurrenceCount;
    }

    public void setOccurrenceCount(int occurrenceCount) {
        this.occurrenceCount = occurrenceCount;
    }

    public String getRawPayload() {
        return rawPayload;
    }

    public void setRawPayload(String rawPayload) {
        this.rawPayload = rawPayload;
    }

}
