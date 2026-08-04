package com.example.networkanomalyassistant.document;


import jakarta.persistence.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.util.UUID;

@Document(indexName = "network_logs")
public class LogDocument {

    @Id
    private String id;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Instant timestamp;

    @Field(type = FieldType.Keyword)
    private String sourceDeviceId;

    @Field(type = FieldType.Keyword)
    private String logLevel;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String logMessage;

    public LogDocument() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
    }

    public LogDocument(String sourceDeviceId, String logLevel, String logMessage) {
        this.id = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
        this.sourceDeviceId = sourceDeviceId;
        this.logLevel = logLevel;
        this.logMessage = logMessage;
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

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }
}
