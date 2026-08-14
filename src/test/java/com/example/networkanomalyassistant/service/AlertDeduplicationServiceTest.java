package com.example.networkanomalyassistant.service;

import com.example.networkanomalyassistant.dto.AlertEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AlertDeduplicationServiceTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private AlertDeduplicationService alertDeduplicationService;

    @BeforeEach
    void setUp() {
        // Inject the Kafka topic name which is normally populated by @Value
        ReflectionTestUtils.setField(alertDeduplicationService, "alertsTopic", "test-alerts-topic");
    }

    @Test
    void testProcessAlert_NewAlert_PublishesToKafka() {
        AlertEvent alert = new AlertEvent();
        alert.setAlertId("ALT-001");
        alert.setSourceDeviceId("router-core-01");
        alert.setAlertName("BGP Neighbor Down");

        AlertEvent result = alertDeduplicationService.processAlert(alert);

        assertEquals(1, result.getOccurrenceCount());
        verify(kafkaTemplate, times(1)).send(eq("test-alerts-topic"), eq("router-core-01"), any(AlertEvent.class));
    }

    @Test
    void testProcessAlert_DuplicateAlertWithinWindow_Deduplicates() {
        AlertEvent alert1 = new AlertEvent();
        alert1.setAlertId("ALT-002");
        alert1.setSourceDeviceId("switch-edge-04");
        alert1.setAlertName("CPU Spike");

        AlertEvent alert2 = new AlertEvent();
        alert2.setAlertId("ALT-003");
        alert2.setSourceDeviceId("switch-edge-04");
        alert2.setAlertName("CPU Spike"); // Identical fingerprint

        // Process first alert
        alertDeduplicationService.processAlert(alert1);

        // Process second identical alert immediately
        AlertEvent result = alertDeduplicationService.processAlert(alert2);

        // Assertions
        assertEquals(2, result.getOccurrenceCount(), "Occurrence count should increment to 2");

        // Verify Kafka was only called ONCE for the initial alert, suppressing the duplicate
        verify(kafkaTemplate, times(1)).send(eq("test-alerts-topic"), eq("switch-edge-04"), any(AlertEvent.class));
    }
}