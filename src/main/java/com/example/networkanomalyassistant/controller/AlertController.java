package com.example.networkanomalyassistant.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.networkanomalyassistant.common.ApiResponse;
import com.example.networkanomalyassistant.dto.AlertEvent;
import com.example.networkanomalyassistant.service.AlertDeduplicationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/alerts")
@Tag(name = "Alert Ingestion & Deduplication", description = "Endpoints for ingesting raw alerts and inspecting active 30s deduplication windows")
public class AlertController {

    private final AlertDeduplicationService deduplicationService;

    public AlertController(AlertDeduplicationService deduplicationService) {
        this.deduplicationService = deduplicationService;
    }

    @PostMapping("/ingest")
    @Operation(summary = "Ingest an alert through the 30-second sliding-window deduplication engine")
    public ApiResponse<AlertEvent> ingestAlert(@RequestBody AlertEvent alert) {
        AlertEvent processed = deduplicationService.processAlert(alert);
        return ApiResponse.ok(processed, "Alert processed by deduplication engine");
    }
    
    @GetMapping("/active-window")
    @Operation(summary = "Get all active deduplicated alerts currently in the 30-second sliding window")
    public ApiResponse<List<AlertEvent>> getActiveWindowAlerts() {
        List<AlertEvent> activeAlerts = deduplicationService.getActiveWindowAlerts();
        return ApiResponse.ok(activeAlerts);
    }
}
