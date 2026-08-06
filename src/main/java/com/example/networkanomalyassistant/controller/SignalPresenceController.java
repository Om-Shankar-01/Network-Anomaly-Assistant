package com.example.networkanomalyassistant.controller;

import com.example.networkanomalyassistant.common.ApiResponse;
import com.example.networkanomalyassistant.dto.MissingEvidenceEvent;
import com.example.networkanomalyassistant.service.SignalPresenceMonitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/monitoring")
@Tag(name = "Signal Presence & Missing Evidence", description = "Endpoints for checking device heartbeats and auditing missing-evidence signal gaps")
public class SignalPresenceController {

    private final SignalPresenceMonitorService signalPresenceMonitorService;

    public SignalPresenceController(SignalPresenceMonitorService signalPresenceMonitorService) {
        this.signalPresenceMonitorService = signalPresenceMonitorService;
    }

    @PostMapping("/heartbeat/{deviceId}")
    @Operation(summary = "Register or refresh device heartbeat timestamp")
    public ApiResponse<String> registerHeartbeat(@PathVariable String deviceId) {
        signalPresenceMonitorService.recordDeviceHeartbeat(deviceId);
        return ApiResponse.ok("Heartbeat recorded for device: " + deviceId);
    }

    @GetMapping("/missing-evidence")
    @Operation(summary = "Audit all devices exceeding the 30-second heartbeat timeout and flag MISSING_EVIDENCE events")
    public ApiResponse<List<MissingEvidenceEvent>> getMissingEvidenceEvents() {
        List<MissingEvidenceEvent> missingEvents = signalPresenceMonitorService.auditSignalGaps();
        return ApiResponse.ok(missingEvents, "Audited signal gaps. Total MISSING_EVIDENCE events: " + missingEvents.size());
    }

    @GetMapping("/registry")
    @Operation(summary = "Get current heartbeat timestamps for all registered network devices")
    public ApiResponse<Map<String, Instant>> getDeviceRegistry() {
        Map<String, Instant> registry = signalPresenceMonitorService.getDeviceRegistry();
        return ApiResponse.ok(registry);
    }
}