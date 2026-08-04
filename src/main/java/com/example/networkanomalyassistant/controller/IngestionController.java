package com.example.networkanomalyassistant.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.networkanomalyassistant.common.ApiResponse;
import com.example.networkanomalyassistant.dto.NormalizedEvent;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/ingest")    
@Tag(name = "Data Ingestion", description = "Endpoints for streaming metrics, logs, alerts, and config changes into Kafka")
public class IngestionController {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.telemetry}")
    private String telemetryTopic;

    @Value("${app.kafka.topics.alerts}")
    private String alertsTopic;

    @Value("${app.kafka.topics.logs}")
    private String logsTopic;

    public IngestionController(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/event")
    @Operation(summary = "Ingest a single normalized event into Kafka topic stream")
    public ApiResponse<String> ingestEvent (@RequestBody NormalizedEvent event) {
        String targetTopic = determineTopic(event.getEventType());
        kafkaTemplate.send(targetTopic, event.getSourceDeviceId(), event);

        return ApiResponse.ok(
            "Event " + event.getEventId() + " published to Kafka Topic: " + targetTopic,
            "Event ingested successfully"
        );
    }

    private String determineTopic(String eventType) {
        if (eventType == null) return logsTopic;
        return switch(eventType.toUpperCase()) {
            case "METRIC" -> telemetryTopic;
            case "ALERT" -> alertsTopic;
            default -> logsTopic;
        };
    }
}
