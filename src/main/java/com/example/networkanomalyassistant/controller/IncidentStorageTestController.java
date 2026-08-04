package com.example.networkanomalyassistant.controller;

import com.example.networkanomalyassistant.common.ApiResponse;
import com.example.networkanomalyassistant.entity.IncidentRecord;
import com.example.networkanomalyassistant.entity.MetricRecord;
import com.example.networkanomalyassistant.repo.IncidentRecordRepository;
import com.example.networkanomalyassistant.repo.MetricRecordRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/storage/test")
@Tag(name = "Storage Test", description = "Endpoints for testing TimescaleDB / JPA metric & incident persistence")
public class IncidentStorageTestController {

    private final MetricRecordRepository metricRecordRepository;
    private final IncidentRecordRepository incidentRecordRepository;

    public IncidentStorageTestController(MetricRecordRepository metricRecordRepository, IncidentRecordRepository incidentRecordRepository) {
        this.metricRecordRepository = metricRecordRepository;
        this.incidentRecordRepository = incidentRecordRepository;
    }

    @PostMapping("/create-sample")
    @Operation(summary = "Persist sample metric and incident records into TimescaleDB")
    public ApiResponse<IncidentRecord> createSampleData() {
        // 1. Save sample metric record
        MetricRecord metric = new MetricRecord(
                Instant.now(),
                "router-core-01",
                "GigE0/1",
                "cpu_usage",
                98.5
        );
        metricRecordRepository.save(metric);

        // 2. Save sample incident record
        IncidentRecord incident = new IncidentRecord(
                "router-core-01",
                "CRITICAL",
                "High CPU saturation caused by BGP routing table flap"
        );
        IncidentRecord savedIncident = incidentRecordRepository.save(incident);

        return ApiResponse.ok(savedIncident, "Sample data persisted into TimescaleDB successfully!");
    }

    @GetMapping("/incidents")
    @Operation(summary = "Fetch all persisted incidents from TimescaleDB")
    public ApiResponse<List<IncidentRecord>> getAllIncidents() {
        List<IncidentRecord> incidents = incidentRecordRepository.findAll();
        return ApiResponse.ok(incidents);
    }
}