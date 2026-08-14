# Phase 7: Audit Trail & Immutable Incident History API - Detailed Execution Plan

## 📌 Phase Overview
Phase 7 creates an immutable audit trail and event-sourced history mechanism for compliance and post-mortem investigations. In high-stakes network operations (FINSERV, Telecom, Cloud Infrastructures), engineering leads need to audit **how an incident evolved over time**, why a specific root-cause hypothesis was selected at minute 0 vs minute 10, and who or what system triggered remediation actions.

---

## ⏱️ Time Estimation & Work Schedule
* **Total Estimated Effort**: 15 Hours
* **Daily Commitment**: 5 Hours / Day
* **Total Duration**: 3 Days

```
       DAY 1 (5 hrs)                      DAY 2 (5 hrs)                      DAY 3 (5 hrs)
┌──────────────────────────┐       ┌──────────────────────────┐       ┌──────────────────────────┐
│  • Audit Trail Entity    │  ───► │  • Event-Sourcing State   │  ───► │  • Audit History REST    │
│  • JPA & TimescaleDB     │       │    Transition Engine     │       │  • Compliance Post-Mortem│
│  • Audit Repository      │       │  • Hypothesis Evolution  │       │  • E2E Audit Verification│
└──────────────────────────┘       └──────────────────────────┘       └──────────────────────────┘
```

---

## 📅 Day-Wise Schedule & Task Breakdown

### 🗓️ Day 1: Audit Trail Entity & Database Schema (5 Hours)

#### Task 1.1: Audit Record JPA Entity (2.0 Hours)
- Create `AuditTrailRecord.java` in TimescaleDB (`entity` package).
- Attributes:
  - `auditId` (UUID)
  - `timestampUtc` (Instant)
  - `incidentId` (String)
  - `deviceId` (String)
  - `eventType` (e.g., `HYPOTHESIS_UPDATED`, `CONFIDENCE_BOOSTED`, `HEARTBEAT_TIMEOUT`, `REPORT_GENERATED`)
  - `previousHypothesis` (String)
  - `newHypothesis` (String)
  - `confidenceDelta` (Double)
  - `triggerSource` (e.g., `BAYESIAN_ENGINE`, `SIGNAL_MONITOR`, `LLM_ASSISTANT`, `OPERATOR`)

#### Task 1.2: Audit Trail Repository (1.5 Hours)
- Create `AuditTrailRecordRepository.java` interface extending `JpaRepository`.
- Add custom query methods: `findByIncidentIdOrderByTimestampUtcAsc(String incidentId)` and `findByDeviceIdOrderByTimestampUtcDesc(String deviceId)`.

#### Task 1.3: Audit DTOs & Event Listener (1.5 Hours)
- Create `AuditRecordDto.java`.

---

### 🗓️ Day 2: Incident State Evolution & Event-Sourcing Service (5 Hours)

#### Task 2.1: Incident State Transition Engine (2.5 Hours)
- Implement `IncidentAuditService.java`.
- Manages state transitions for network incidents:
  - `DETECTED`: Initial anomaly alert or metric Z-score spike.
  - `CORRELATED`: Multi-variate Isolation Forest or Granger causality link confirmed.
  - `ROOT_CAUSE_CONFIRMED`: Bayesian posterior probability exceeds threshold (> 80%).
  - `RESOLVED`: Heartbeats and telemetry normalize.

#### Task 2.2: Automated Audit Hook Bindings (1.5 Hours)
- Bind audit logging hooks to `KafkaEventConsumer`, `SignalPresenceMonitorService`, `BayesianTestController`, and `ExplainableAssistantService` so every system action automatically records an immutable audit log entry.

#### Task 2.3: Unit & Integration Testing of Audit Service (1.0 Hour)
- Verify state transitions and hypothesis history tracking.

---

### 🗓️ Day 3: History & Compliance Post-Mortem REST APIs (5 Hours)

#### Task 3.1: Audit Trail REST Controller (2.0 Hours)
- Create `AuditTrailController.java` (`/api/v1/audit`).
- Endpoints:
  - `GET /api/v1/audit/incidents/{incidentId}/history`: Returns complete chronological hypothesis evolution timeline.
  - `GET /api/v1/audit/devices/{deviceId}/trail`: Returns audit log for a specific device.
  - `GET /api/v1/audit/incidents/{incidentId}/export`: Generates compliance-ready Post-Mortem Audit Report.

#### Task 3.2: Exportable Post-Mortem Report Builder (1.5 Hours)
- Format compliance post-mortem document summarizing initial detection, hypothesis transitions, evidence matrix, and final resolution.

#### Task 3.3: Phase 7 End-to-End Verification (1.5 Hours)
- Run complete incident lifecycle (Anomaly $\rightarrow$ Granger $\rightarrow$ Bayesian $\rightarrow$ LLM Report).
- Query `/api/v1/audit/incidents/{incidentId}/history` and verify the complete immutable history log!

---

## 🎯 Phase 7 Final Deliverables
1. **Immutable Audit Entity**: `AuditTrailRecord.java` in TimescaleDB.
2. **Event-Sourcing Transition Engine**: `IncidentAuditService.java` tracking hypothesis evolution over time.
3. **Automated Audit Logging Hooks**: Hooks recording every system detection and analysis step.
4. **Audit History & Compliance APIs**: `/api/v1/audit/incidents/{incidentId}/history` and export endpoints.
