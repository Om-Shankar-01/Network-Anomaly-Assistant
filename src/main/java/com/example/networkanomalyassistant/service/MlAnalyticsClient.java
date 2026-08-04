package com.example.networkanomalyassistant.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.networkanomalyassistant.grpc.AnomalyDetectionRequest;
import com.example.networkanomalyassistant.grpc.AnomalyDetectionResponse;
import com.example.networkanomalyassistant.grpc.AnomalyDetectionServiceGrpc;
import com.example.networkanomalyassistant.grpc.MetricDataPoint;

import net.devh.boot.grpc.client.inject.GrpcClient;

@Service
public class MlAnalyticsClient {
    
    @GrpcClient("ml-service")
    private AnomalyDetectionServiceGrpc.AnomalyDetectionServiceBlockingStub anomalyStub;
    
    public AnomalyDetectionResponse detectAnomalies (String deviceId, List<MetricDataPoint> points, long startMs, long endMs) {
        AnomalyDetectionRequest request = AnomalyDetectionRequest.newBuilder()
                        .setDeviceId(deviceId)
                        .addAllDataPoints(points)
                        .setWindowStartMs(startMs)
                        .setWindowEndMs(endMs)
                        .build();
        
        return anomalyStub.detectAnomalies(request);
    }
}
