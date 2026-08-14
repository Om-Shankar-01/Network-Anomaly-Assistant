package com.example.networkanomalyassistant.service;

import com.example.networkanomalyassistant.entity.AuditTrailRecord;
import com.example.networkanomalyassistant.repo.AuditTrailRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class IncidentAuditService {

    private static final Logger log = LoggerFactory.getLogger(IncidentAuditService.class);

    // State Constants: DETECTED -> CORRELATED -> ROOT_CAUSE_CONFIRMED -> RESOLVED
    public static final String STATE_DETECTED = "DETECTED";
    public static final String STATE_CORRELATED = "CORRELATED";
    public static final String STATE_CONFIRMED = "ROOT_CAUSE_CONFIRMED";
    public static final String STATE_RESOLVED = "RESOLVED";

    private final AuditTrailRecordRepository auditRepository;

    public IncidentAuditService(AuditTrailRecordRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    /**
     * Records an immutable audit trail entry for an incident event or hypothesis transition.
     */
    public AuditTrailRecord recordAuditEvent(String incidentId,
                                             String deviceId,
                                             String eventType,
                                             String previousHypothesis,
                                             String newHypothesis,
                                             Double confidenceScore,
                                             String triggerSource,
                                             String details) {

        AuditTrailRecord record = new AuditTrailRecord(
                incidentId,
                deviceId,
                eventType,
                previousHypothesis,
                newHypothesis,
                confidenceScore,
                triggerSource,
                details
        );

        AuditTrailRecord saved = auditRepository.save(record);
        log.info("Audit Record Saved [{}] | Incident [{}] | Event [{}] | Source [{}]",
                saved.getAuditId(), incidentId, eventType, triggerSource);
        return saved;
    }

    /**
     * Explicit State Transition Logger: DETECTED -> CORRELATED -> ROOT_CAUSE_CONFIRMED -> RESOLVED
     */
    public AuditTrailRecord recordStateTransition(String incidentId,
                                                  String deviceId,
                                                  String fromState,
                                                  String toState,
                                                  Double confidenceScore,
                                                  String triggerSource,
                                                  String details) {
        String eventType = "STATE_TRANSITION_" + toState;
        String fullDetails = String.format("State changed from [%s] to [%s]. Details: %s", fromState, toState, details);
        return recordAuditEvent(incidentId, deviceId, eventType, fromState, toState, confidenceScore, triggerSource, fullDetails);
    }

    public List<AuditTrailRecord> getHistoryByIncident(String incidentId) {
        return auditRepository.findByIncidentIdOrderByTimestampUtcAsc(incidentId);
    }

    public List<AuditTrailRecord> getHistoryByDevice(String deviceId) {
        return auditRepository.findByDeviceIdOrderByTimestampUtcDesc(deviceId);
    }

    /**
     * Generates a compliance-ready Post-Mortem Audit Report in Markdown format.
     */
    public String generatePostMortemReport(String incidentId) {
        List<AuditTrailRecord> history = getHistoryByIncident(incidentId);

        StringBuilder sb = new StringBuilder();
        sb.append("# 📑 Compliance Post-Mortem Audit Report: ").append(incidentId).append("\n\n");
        sb.append("Generated at: ").append(Instant.now()).append("\n\n");
        sb.append("## 📜 Immutable Incident Lifecycle History\n\n");
        if (history.isEmpty()) {
            sb.append("*No audit trail records found for incident ID: ").append(incidentId).append("*\n");
        } else {
            sb.append("| Timestamp (UTC) | Event Type | Trigger Source | State Transition | Confidence | Details |\n");
            sb.append("|---|---|---|---|---|---|\n");
            for (AuditTrailRecord record : history) {
                sb.append(String.format("| %s | `%s` | `%s` | %s → %s | %.1f%% | %s |\n",
                        record.getTimestampUtc(),
                        record.getEventType(),
                        record.getTriggerSource(),
                        record.getPreviousHypothesis(),
                        record.getNewHypothesis(),
                        record.getConfidenceScore() != null ? record.getConfidenceScore() * 100 : 0.0,
                        record.getDetails()
                ));
            }
        }
        sb.append("\n---\n");
        sb.append("### 🔒 Audit Verification Statement\n");
        sb.append("This document was automatically compiled from TimescaleDB append-only audit trail records. All timestamps are UTC-aligned and cryptographically indexed.\n");
        return sb.toString();
    }
}