package com.example.networkanomalyassistant.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.driver.Driver;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.networkanomalyassistant.common.ApiResponse;
import com.example.networkanomalyassistant.grpc.AnomalyDetectionRequest;
import com.example.networkanomalyassistant.grpc.AnomalyDetectionResponse;
import com.example.networkanomalyassistant.grpc.AnomalyDetectionServiceGrpc;
import com.example.networkanomalyassistant.repo.DeviceRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.devh.boot.grpc.client.inject.GrpcClient;

@RestController
@RequestMapping("/api/v1/system")
@Tag(name = "System Health", description = "Aggregated health checks for Java, Neo4j, and Python ML services")
public class SystemHealthController {
    
    private final Driver neo4jDriver;
    private final DeviceRepository deviceRepository;

    @GrpcClient("ml-service")
    private AnomalyDetectionServiceGrpc.AnomalyDetectionServiceBlockingStub anomalyStub;

    public SystemHealthController (Driver neo4jDriver, DeviceRepository deviceRepository) {
        this.neo4jDriver = neo4jDriver;
        this.deviceRepository = deviceRepository;
    }

    @GetMapping("/health")
    @Operation(summary = "Aggregated system health check covering Java, Neo4j Graph DB, and Python ML Service")
    public ApiResponse<Map<String, Object>> getSystemHealth() {
        Map<String, Object> healthReport = new HashMap<>();
        Map<String, String> components = new HashMap<>();

        // 1. Check Java Backend
        components.put("javaBackend", "UP");

        // 2. Check Neo4j Connectivity & Node Count
        try {
            neo4jDriver.verifyConnectivity();
            long count = deviceRepository.count();
            components.put("neo4jDatabase", "UP (Device Count: " + count + ")");
        } catch (Exception e) {
            components.put("neo4jDatabase", "DOWN (" + e.getMessage() + ")");
        }

        // 3. Check Python ML Service via gRPC Ping
        try {
            long now = System.currentTimeMillis();
            AnomalyDetectionRequest pingRequest = AnomalyDetectionRequest.newBuilder()
                    .setDeviceId("health-ping")
                    .addAllDataPoints(Collections.emptyList())
                    .setWindowStartMs(now)
                    .setWindowEndMs(now)
                    .build();
                    
            AnomalyDetectionResponse response = anomalyStub.detectAnomalies(pingRequest);
            if (response != null && "health-ping".equals(response.getDeviceId())) {
                components.put("pythonMlService", "UP");
            } else {
                components.put("pythonMlService", "DEGRADED");
            }
        } catch (Exception e) {
            components.put("pythonMlService", "DOWN (" + e.getMessage() + ")");
        }

        // Calculate overall system status
        boolean allUp = components.values().stream().allMatch(status -> status.startsWith("UP"));
        healthReport.put("status", allUp ? "UP" : "DEGRADED");
        healthReport.put("components", components);

        return ApiResponse.ok(healthReport, "System health report generated successfully");
    }
}
