package com.example.networkanomalyassistant.controller;

import com.example.networkanomalyassistant.common.ApiResponse;
import com.example.networkanomalyassistant.service.ExplainableAssistantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/assistant")
@Tag(name = "Explainable Assistant", description = "Endpoints for generating auditor-friendly markdown incident reports with 3-way evidence matrix and CLI commands")
public class ExplainableAssistantController {

    private final ExplainableAssistantService assistantService;

    public ExplainableAssistantController(ExplainableAssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @PostMapping("/explain/{deviceId}")
    @Operation(summary = "Generate complete auditor-friendly incident investigation report (Markdown + 3-Way Evidence + CLI Commands)")
    public ApiResponse<Map<String, Object>> generateReport(@PathVariable String deviceId) {
        Map<String, Object> report = assistantService.generateAndPersistReport(deviceId);
        return ApiResponse.ok(report, "Incident investigation report generated and persisted successfully!");
    }
}