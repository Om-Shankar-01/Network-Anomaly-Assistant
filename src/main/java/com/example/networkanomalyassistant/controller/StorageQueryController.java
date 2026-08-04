package com.example.networkanomalyassistant.controller;

import com.example.networkanomalyassistant.common.ApiResponse;
import com.example.networkanomalyassistant.document.AlertDocument;
import com.example.networkanomalyassistant.document.LogDocument;
import com.example.networkanomalyassistant.entity.IncidentRecord;
import com.example.networkanomalyassistant.entity.MetricRecord;
import com.example.networkanomalyassistant.repo.AlertSearchRepository;
import com.example.networkanomalyassistant.repo.IncidentRecordRepository;
import com.example.networkanomalyassistant.repo.LogSearchRepository;
import com.example.networkanomalyassistant.repo.MetricRecordRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/api/v1/storage")
@Tag(name = "Storage Query API", description = "Unified query endpoints for querying metrics (TimescaleDB), logs/alerts (Elasticsearch), and incidents")
public class StorageQueryController {

    private final MetricRecordRepository metricRecordRepository;
    private final IncidentRecordRepository incidentRecordRepository;
    private final LogSearchRepository logSearchRepository;
    private final AlertSearchRepository alertSearchRepository;

    public StorageQueryController(MetricRecordRepository metricRecordRepository, IncidentRecordRepository incidentRecordRepository, LogSearchRepository logSearchRepository, AlertSearchRepository alertSearchRepository) {
        this.metricRecordRepository = metricRecordRepository;
        this.incidentRecordRepository = incidentRecordRepository;
        this.logSearchRepository = logSearchRepository;
        this.alertSearchRepository = alertSearchRepository;
    }

    @GetMapping("/metrics/{deviceId}")
    @Operation(summary = "Query historical metric time-series data for a device from TimescaleDB")
    public ApiResponse<List<MetricRecord>> getDeviceMetrics(
            @PathVariable String deviceId,
            @RequestParam(required = false) Integer minutesBack
    ) {
        int minutes = (minutesBack != null && minutesBack > 0) ? minutesBack : 60;
        Instant start = Instant.now().minus(minutes, ChronoUnit.MINUTES);
        Instant end = Instant.now();

        List<MetricRecord> metrics = metricRecordRepository
                .findBySourceDeviceIdAndTimestampUtcBetweenOrderByTimestampUtcAsc(deviceId, start, end);
        return ApiResponse.ok(metrics, "Fetched " + metrics.size() + " metrics records for device: " + deviceId);
    }

    @GetMapping("/logs/search")
    @Operation(summary = "Full-text search over indexed network logs in Elasticsearch")
    public ApiResponse<List<LogDocument>> searchLogs(@RequestParam String query) {
        List<LogDocument> logs = logSearchRepository.findByLogMessageContainingIgnoreCase(query);
        return ApiResponse.ok(logs, "Found " + logs.size() + " matching log entries");
    }

    @GetMapping("/alerts/{deviceId}")
    @Operation(summary = "Fetch indexed alert history for a device from Elasticsearch")
    public ApiResponse<List<AlertDocument>> getDeviceAlerts(@PathVariable String deviceId) {
        List<AlertDocument> alerts = alertSearchRepository.findBySourceDeviceId(deviceId);
        return ApiResponse.ok(alerts, "Fetched " + alerts.size() + " alert documents for device: " + deviceId);
    }

    @GetMapping("/incidents")
    @Operation(summary = "Fetch persistent root-cause incident records from TimescaleDB")
    public ApiResponse<List<IncidentRecord>> getAllIncidents() {
        List<IncidentRecord> incidents = incidentRecordRepository.findAll();
        return ApiResponse.ok(incidents, "Fetched " + incidents.size() + " total incidents");
    }
}
