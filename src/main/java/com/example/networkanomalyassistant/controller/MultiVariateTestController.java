package com.example.networkanomalyassistant.controller;

import com.example.networkanomalyassistant.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api/v1/ml")
@Tag(name = "ML Service Integration", description = "Endpoints for testing Isolation Forest multi-variate anomaly detection")
public class MultiVariateTestController {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String PYTHON_ML_URL = "http://localhost:8000/analyze/multivariate";

    @PostMapping("/test-multivariate/{deviceId}")
    @Operation(summary = "Test Isolation Forest multi-variate anomaly detection (CPU + RAM + Packet Drops)")
    public ApiResponse<Map<String, Object>> testMultiVariateAnomaly(@PathVariable String deviceId) {

        List<String> featureNames = List.of("cpu_usage", "ram_usage", "packet_drop_rate");
        List<List<Double>> matrix = new ArrayList<>();

        // 9 Normal time ticks (CPU ~25%, RAM ~40%, Drops ~0.1%)
        for (int i = 0; i < 9; i++) {
            matrix.add(List.of(
                    24.0 + (Math.random() * 2.0),
                    39.0 + (Math.random() * 2.0),
                    0.05 + (Math.random() * 0.1)
            ));
        }

        // 1 Anomalous Multi-Variate Tick (All 3 metrics elevated simultaneously!)
        matrix.add(List.of(78.5, 82.0, 5.4));

        Map<String, Object> requestBody = Map.of(
                "device_id", deviceId,
                "feature_names", featureNames,
                "matrix", matrix
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> pythonResponse = restTemplate.postForObject(PYTHON_ML_URL, requestBody, Map.class);

        return ApiResponse.ok(pythonResponse, "Multi-variate Isolation Forest analysis complete!");
    }
}