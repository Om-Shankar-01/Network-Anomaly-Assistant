package com.example.networkanomalyassistant.controller;

import com.example.networkanomalyassistant.common.ApiResponse;
import com.example.networkanomalyassistant.entity.AuditTrailRecord;
import com.example.networkanomalyassistant.service.IncidentAuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/audit")
@Tag(name = "Audit Trail & Compliance", description = "Endpoints for querying immutable incident history, hypothesis state transitions, and compliance post-mortems")
public class AuditTrailController {

    private final IncidentAuditService incidentAuditService;

    public AuditTrailController(IncidentAuditService incidentAuditService) {
        this.incidentAuditService = incidentAuditService;
    }

    @GetMapping("/incidents/{incidentId}/history")
    @Operation(summary = "Get complete chronological audit history and hypothesis evolution for an incident")
    public ApiResponse<List<AuditTrailRecord>> getIncidentHistory(@PathVariable String incidentId) {
        List<AuditTrailRecord> history = incidentAuditService.getHistoryByIncident(incidentId);
        return ApiResponse.ok(history, "Retrieved " + history.size() + " audit records for incident: " + incidentId);
    }

    @GetMapping("/devices/{deviceId}/trail")
    @Operation(summary = "Get audit trail entries for a specific network device")
    public ApiResponse<List<AuditTrailRecord>> getDeviceAuditTrail(@PathVariable String deviceId) {
        List<AuditTrailRecord> trail = incidentAuditService.getHistoryByDevice(deviceId);
        return ApiResponse.ok(trail, "Retrieved " + trail.size() + " audit records for device: " + deviceId);
    }

    @GetMapping("/incidents/{incidentId}/export")
    @Operation(summary = "Generate and export a compliance-ready Post-Mortem Audit Report")
    public ApiResponse<Map<String, String>> exportPostMortemReport(@PathVariable String incidentId) {
        String postMortem = incidentAuditService.generatePostMortemReport(incidentId);
        return ApiResponse.ok(Map.of(
                "incidentId", incidentId,
                "postMortemReport", postMortem
        ), "Post-Mortem audit report compiled successfully!");
    }
}