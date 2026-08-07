package com.example.networkanomalyassistant.controller;

import com.example.networkanomalyassistant.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api/v1/ml")
@Tag(name = "ML Service Integration", description = "Endpoints for testing Granger Causality temporal prediction")
public class GrangerTestController {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String PYTHON_GRANGER_URL = "http://localhost:8000/analyze/granger";

    @PostMapping("/test-granger")
    @Operation(summary = "Test Granger Causality (Series X causes Series Y with 1-step lag)")
    public ApiResponse<Map<String, Object>> testGrangerCausality() {

        // Construct time-series X (Leading Cause: Core Router Packet Loss Spike)
        List<Double> seriesX = List.of(
                1.0, 1.2, 1.1, 95.0, 98.0, 92.0, 1.3, 1.1, 1.0, 1.2, 1.1, 1.0, 1.1, 1.2, 1.0
        );

        // Construct time-series Y (Lagged Effect: Edge Switch Latency Spike, delayed by 1 time step!)
        List<Double> seriesY = List.of(
                10.0, 10.2, 10.1, 10.3, 450.0, 480.0, 460.0, 10.2, 10.1, 10.0, 10.2, 10.1, 10.0, 10.1, 10.0
        );

        Map<String, Object> requestBody = Map.of(
                "source_device_id", "router-core-01",
                "target_device_id", "switch-edge-02",
                "series_x", seriesX,
                "series_y", seriesY
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> pythonResponse = restTemplate.postForObject(PYTHON_GRANGER_URL, requestBody, Map.class);

        return ApiResponse.ok(pythonResponse, "Granger Causality analysis complete!");
    }
}