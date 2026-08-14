package com.example.networkanomalyassistant.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.networkanomalyassistant.entity.AuditTrailRecord;
import com.example.networkanomalyassistant.repo.AuditTrailRecordRepository;

@ExtendWith(MockitoExtension.class)
class IncidentAuditServiceTest {

    @Mock
    private AuditTrailRecordRepository auditRepository;

    @InjectMocks
    private IncidentAuditService auditService;

    @Test
    void testRecordStateTransition_CreatesCorrectAuditRecord() {
        String incidentId = "INC-TEST1";

        // Mock save return
        AuditTrailRecord mockSaved = new AuditTrailRecord();
        mockSaved.setAuditId("AUDIT-123");
        when(auditRepository.save(any(AuditTrailRecord.class))).thenReturn(mockSaved);

        AuditTrailRecord result = auditService.recordStateTransition(
                incidentId,
                "router-01",
                IncidentAuditService.STATE_DETECTED,
                IncidentAuditService.STATE_CORRELATED,
                0.85,
                "SYSTEM_ENGINE",
                "Correlated 3 metric anomalies"
        );

        assertNotNull(result);

        // Capture what was actually passed to save()
        ArgumentCaptor<AuditTrailRecord> captor = ArgumentCaptor.forClass(AuditTrailRecord.class);
        verify(auditRepository).save(captor.capture());

        AuditTrailRecord captured = captor.getValue();
        assertEquals(incidentId, captured.getIncidentId());
        assertEquals("router-01", captured.getDeviceId());
        assertEquals("STATE_TRANSITION_CORRELATED", captured.getEventType());
        assertEquals(IncidentAuditService.STATE_DETECTED, captured.getPreviousHypothesis());
        assertEquals(IncidentAuditService.STATE_CORRELATED, captured.getNewHypothesis());
        assertTrue(captured.getDetails().contains("Correlated 3 metric anomalies"));
    }

    @Test
    void testGeneratePostMortemReport_RendersMarkdown() {
        String incidentId = "INC-TEST2";

        AuditTrailRecord record = new AuditTrailRecord(
                incidentId, "switch-02", "STATE_TRANSITION_RESOLVED",
                "ROOT_CAUSE_CONFIRMED", "RESOLVED", 0.99, "ADMIN_UI", "Manual override"
        );
        record.setTimestampUtc(Instant.parse("2026-08-14T10:00:00Z"));

        when(auditRepository.findByIncidentIdOrderByTimestampUtcAsc(incidentId))
                .thenReturn(List.of(record));

        String markdown = auditService.generatePostMortemReport(incidentId);

        assertNotNull(markdown);
        assertTrue(markdown.contains("Compliance Post-Mortem Audit Report"));
        assertTrue(markdown.contains("ROOT_CAUSE_CONFIRMED → RESOLVED"));
        assertTrue(markdown.contains("99.0%")); // Verifies the 0.99 confidence was multiplied by 100 for display
    }
}
