package com.example.networkanomalyassistant.document;

import jakarta.persistence.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.util.UUID;

@Document(indexName = "network_alerts")
public class AlertDocument {

    @Id
    private String id;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Instant timestamp;

    @Field(type = FieldType.Keyword)
    private String sourceDeviceId;

    @Field(type = FieldType.Keyword)
    private String sourceInterfaceId;

    @Field(type = FieldType.Keyword)
    private String alertName;

    @Field(type = FieldType.Keyword)
    private String severity;

    @Field(type = FieldType.Integer)
    private int occurrenceCount;

    public AlertDocument() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
    }

    public AlertDocument(String sourceDeviceId, String sourceInterfaceId, String alertName, String severity, int occurrenceCount) {
        this.id = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
        this.sourceDeviceId = sourceDeviceId;
        this.sourceInterfaceId = sourceInterfaceId;
        this.alertName = alertName;
        this.severity = severity;
        this.occurrenceCount = occurrenceCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
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

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public int getOccurrenceCount() {
        return occurrenceCount;
    }

    public void setOccurrenceCount(int occurrenceCount) {
        this.occurrenceCount = occurrenceCount;
    }
}
