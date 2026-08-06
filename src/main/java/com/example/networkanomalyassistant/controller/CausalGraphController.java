package com.example.networkanomalyassistant.controller;

import com.example.networkanomalyassistant.common.ApiResponse;
import com.example.networkanomalyassistant.dto.DagGraph;
import com.example.networkanomalyassistant.service.CausalGraphBuilderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/causal")
@Tag(name = "Causal Inference", description = "Endpoints for DAG topology extraction, Granger Causality, and Bayesian root-cause ranking")
public class CausalGraphController {

    private final CausalGraphBuilderService causalGraphBuilderService;

    public CausalGraphController(CausalGraphBuilderService causalGraphBuilderService) {
        this.causalGraphBuilderService = causalGraphBuilderService;
    }

    @GetMapping("/dag/{deviceId}")
    @Operation(summary = "Extract localized Directed Acyclic Graph (DAG) for an incident device")
    public ApiResponse<DagGraph> getCausalDag(@PathVariable String deviceId) {
        DagGraph dag = causalGraphBuilderService.buildCausalDag(deviceId);
        return ApiResponse.ok(dag, "Causal DAG extracted successfully for device: " + deviceId);
    }
}