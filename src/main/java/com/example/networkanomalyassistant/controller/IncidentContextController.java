package com.example.networkanomalyassistant.controller;

import com.example.networkanomalyassistant.common.ApiResponse;
import com.example.networkanomalyassistant.dto.IncidentContextDto;
import com.example.networkanomalyassistant.service.IncidentContextAggregatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/explainability")
@Tag(name = "Explainable Assistant", description = "Endpoints for context aggregation, 3-way evidence matrix, and LLM incident report generation")
public class IncidentContextController {

    private final IncidentContextAggregatorService aggregatorService;

    public IncidentContextController(IncidentContextAggregatorService aggregatorService) {
        this.aggregatorService = aggregatorService;
    }

    @GetMapping("/context/{deviceId}")
    @Operation(summary = "Aggregate multi-source evidence and construct 3-Way Evidence Matrix (Confirmed, Correlated, Missing)")
    public ApiResponse<IncidentContextDto> getIncidentContext(@PathVariable String deviceId) {

        IncidentContextDto context = aggregatorService.aggregateContext(deviceId);

        return ApiResponse.ok(context, "Incident context and 3-way evidence matrix generated for device: " + deviceId);
    }
}