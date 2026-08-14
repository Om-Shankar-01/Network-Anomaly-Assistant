package com.example.networkanomalyassistant.service;

import com.example.networkanomalyassistant.document.AlertDocument;
import com.example.networkanomalyassistant.document.LogDocument;
import com.example.networkanomalyassistant.entity.MetricRecord;
import com.example.networkanomalyassistant.repo.AlertSearchRepository;
import com.example.networkanomalyassistant.repo.LogSearchRepository;
import com.example.networkanomalyassistant.repo.MetricRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

import com.example.networkanomalyassistant.dto.NormalizedEvent;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class KafkaEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventConsumer.class);

    private final MetricRecordRepository metricRecordRepository;
    private final LogSearchRepository logSearchRepository;
    private final AlertSearchRepository alertSearchRepository;
    private final SignalPresenceMonitorService signalPresenceMonitorService;
    private final IncidentAuditService incidentAuditService;

    public KafkaEventConsumer(MetricRecordRepository metricRecordRepository,
                              LogSearchRepository logSearchRepository,
                              AlertSearchRepository alertSearchRepository,
                              SignalPresenceMonitorService signalPresenceMonitorService,
                              IncidentAuditService incidentAuditService) {

        this.metricRecordRepository = metricRecordRepository;
        this.logSearchRepository = logSearchRepository;
        this.alertSearchRepository = alertSearchRepository;
        this.signalPresenceMonitorService = signalPresenceMonitorService;
        this.incidentAuditService = incidentAuditService;
    }

    /**
     * Consumes telemetry metrics from Kafka and persists them to TimescaleDB
     */
    @KafkaListener(topics = "${app.kafka.topics.telemetry}", groupId = "network-assistant-group-v2")
    public void consumeTelemetry(NormalizedEvent event) {
        try {
            // Refresh heartbeat timestamp for device
            signalPresenceMonitorService.recordDeviceHeartbeat(event.getSourceDeviceId());

            Map<String, Object> payload = event.getPayload();
            String metricName = payload != null && payload.get("metricName") != null ? payload.get("metricName").toString() : "cpu_usage";
            Double metricValue = payload != null && payload.get("metricValue") != null ? Double.valueOf(payload.get("metricValue").toString()) : 0.0;

            MetricRecord metricRecord = new MetricRecord(
                    event.getTimestampUtc(),
                    event.getSourceDeviceId(),
                    event.getSourceInterfaceId(),
                    metricName,
                    metricValue
            );

            metricRecordRepository.save(metricRecord);
            log.info("Persisted TELEMETRY to TimescaleDB: Device={} | Metric={} | Value={}",
                    event.getSourceDeviceId(), metricName, metricValue);
        } catch (Exception e) {
            log.error("Failed to persist telemetry metric: {}", e.getMessage());
        }
    }


    /**
     * Consumes alert events from Kafka and indexes them in Elasticsearch
     */
    @KafkaListener(topics = "${app.kafka.topics.alerts}", groupId = "network-assistant-group-v2")
    public void consumeAlerts(NormalizedEvent event) {
        try {

            signalPresenceMonitorService.recordDeviceHeartbeat(event.getSourceDeviceId());

            Map<String, Object> payload = event.getPayload();
            String alertName = payload != null && payload.get("alertName") != null ? payload.get("alertName").toString() : "GENERIC_ALERT";
            int occurrenceCount = payload != null && payload.get("occurrenceCount") != null ? Integer.parseInt(payload.get("occurrenceCount").toString()) : 1;

            AlertDocument alertDocument = new AlertDocument(
                    event.getSourceDeviceId(),
                    event.getSourceInterfaceId(),
                    alertName,
                    event.getSeverity(),
                    occurrenceCount
            );

            alertSearchRepository.save(alertDocument);

            // Inside consumeAlerts(NormalizedEvent event):
            if ("CRITICAL".equalsIgnoreCase(event.getSeverity())) {
                incidentAuditService.recordStateTransition(
                        "INC-" + event.getSourceDeviceId(),
                        event.getSourceDeviceId(),
                        "HEALTHY",
                        IncidentAuditService.STATE_DETECTED,
                        0.50,
                        "KAFKA_CONSUMER",
                        "Critical alert ingested from Kafka: " + event.getPayload().get("alertName")
                );
            }

            log.warn("Indexed ALERT in Elasticsearch: Device={} | Alert={} | Severity={}",
                    event.getSourceDeviceId(), alertName, event.getSeverity());

        } catch (Exception e) {
            log.error("Failed to index alert event: {}", e.getMessage());
        }
    }

    /**
     * Consumes logs and config changes from Kafka and indexes them in Elasticsearch
     */
    @KafkaListener(topics = "${app.kafka.topics.logs}", groupId = "network-assistant-group-v2")
    public void consumeLogs(NormalizedEvent event) {
        try {

            signalPresenceMonitorService.recordDeviceHeartbeat(event.getSourceDeviceId());

            Map<String, Object> payload = event.getPayload();
            String logMsg = payload != null && payload.get("logMessage") != null ? payload.get("logMessage").toString() : "System log event";
            if (payload != null && payload.containsKey("message")) {
                logMsg = payload.get("message").toString();
            }

            LogDocument logDoc = new LogDocument(
                    event.getSourceDeviceId(),
                    event.getSeverity(),
                    logMsg
            );

            logSearchRepository.save(logDoc);
            log.info("Indexed LOG in Elasticsearch: Device={} | Message={}",
                    event.getSourceDeviceId(), logMsg);

        } catch (Exception e) {
            log.error("Failed to index log event: {}", e.getMessage());
        }
    }

}
