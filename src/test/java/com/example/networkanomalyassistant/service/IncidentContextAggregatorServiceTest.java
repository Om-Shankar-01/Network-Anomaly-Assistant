package com.example.networkanomalyassistant.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.networkanomalyassistant.document.AlertDocument;
import com.example.networkanomalyassistant.dto.IncidentContextDto;
import com.example.networkanomalyassistant.repo.AlertSearchRepository;
import com.example.networkanomalyassistant.repo.LogSearchRepository;
import com.example.networkanomalyassistant.repo.MetricRecordRepository;

@ExtendWith(MockitoExtension.class)
class IncidentContextAggregatorServiceTest {

    @Mock
    private MetricRecordRepository metricRecordRepository;

    @Mock
    private LogSearchRepository logSearchRepository;

    @Mock
    private AlertSearchRepository alertSearchRepository;

    @Mock
    private SignalPresenceMonitorService signalPresenceMonitorService;

    @InjectMocks
    private IncidentContextAggregatorService aggregatorService;

    @Test
    void testAggregateContext_Builds3WayMatrix() {
        String deviceId = "router-core-01";

        // Mock Confirmed Evidence (Alerts fetching from Elasticsearch)
        AlertDocument alert = new AlertDocument();
        alert.setSourceDeviceId(deviceId);
        alert.setSeverity("CRITICAL");
        alert.setAlertName("BGP Neighbor Timeout");
        alert.setOccurrenceCount(5);
        when(alertSearchRepository.findBySourceDeviceId(deviceId)).thenReturn(List.of(alert));

        // Mock empty for the rest to test the fallback mechanisms
        when(logSearchRepository.findBySourceDeviceIdOrderByTimestampDesc(deviceId)).thenReturn(List.of());
        when(metricRecordRepository.findBySourceDeviceIdAndTimestampUtcBetweenOrderByTimestampUtcAsc(eq(deviceId), any(), any())).thenReturn(List.of());
        when(signalPresenceMonitorService.auditSignalGaps()).thenReturn(List.of());

        IncidentContextDto result = aggregatorService.aggregateContext(deviceId);

        // Assert Matrix Categorization logic works
        assertTrue(result.getConfirmedEvidence().stream().anyMatch(e -> e.contains("BGP Neighbor Timeout")));

        // Assert Fallbacks trigger when true arrays are empty
        assertEquals(1, result.getCorrelatedSignals().size());
        assertEquals(1, result.getMissingEvidence().size());

        assertEquals(0.9172, result.getConfidenceScore(), 0.0001);
    }
}