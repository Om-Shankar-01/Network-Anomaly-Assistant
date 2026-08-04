package com.example.networkanomalyassistant.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.networkanomalyassistant.common.ApiResponse;
import com.example.networkanomalyassistant.grpc.AnomalyDetectionResponse;
import com.example.networkanomalyassistant.grpc.MetricDataPoint;
import com.example.networkanomalyassistant.service.MlAnalyticsClient;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/ml")
@Tag(name = "ML Service Integration", description = "Endpoints for testing inter-service communication with Python ML microservice")
public class MlTestController {

    private final MlAnalyticsClient mlAnalyticsClient;

    public MlTestController(MlAnalyticsClient mlAnalyticsClient) {
        this.mlAnalyticsClient = mlAnalyticsClient;
    }

    @PostMapping("/test-anomaly/{deviceId}")
    @Operation(summary = "Test gRPC call from Java to Python ML service for anomaly detection")
    public ApiResponse<String> testAnomalyDetection(@PathVariable String deviceId) {
        long now = System.currentTimeMillis();
        List<MetricDataPoint> points = new ArrayList<>();

        // 9 Normal baseline points (~25.0 CPU)
        for (int i = 9; i >= 1; i--) {
            double normalVal = 24.0 + (Math.random() * 2.0); // 24.0 to 26.0
            points.add(MetricDataPoint.newBuilder()
                    .setTimestampMs(now - (i * 1000))
                    .setMetricName("cpu_usage")
                    .setValue(normalVal)
                    .build());
        }

        // 1 Massive Anomaly Spike
        points.add(MetricDataPoint.newBuilder()
                .setTimestampMs(now)
                .setMetricName("cpu_usage")
                .setValue(98.5)
                .build());

        AnomalyDetectionResponse response = mlAnalyticsClient.detectAnomalies(deviceId, points, now - 10000, now);

        String summary = String.format(
                "Device: %s | Anomaly Detected: %b | Max Score: %.2f | Anomaly Count: %d",
                response.getDeviceId(),
                response.getOverallAnomalyDetected(),
                response.getMaxAnomalyScore(),
                response.getAnomaliesCount()
        );

        return ApiResponse.ok(summary, "gRPC call to Python ML service successful!");
    }
}
