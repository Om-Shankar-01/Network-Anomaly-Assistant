# Phase 2: Data Ingestion & Normalization - Detailed Execution Plan

## 📌 Phase Overview
Phase 2 builds the real-time data streaming and normalization engine of the Network Anomaly Root-Cause Assistant. It handles incoming heterogeneous data streams (metrics, logs, alerts, configuration changes), standardizes them into a unified temporal schema, applies alert deduplication/suppression logic, and provides a synthetic data generator to simulate network failure cascades.

---

## ⏱️ Time Estimation & Work Schedule
* **Total Estimated Effort**: 15 Hours
* **Daily Commitment**: 5 Hours / Day
* **Total Duration**: 3 Days

```
       DAY 1 (5 hrs)                      DAY 2 (5 hrs)                      DAY 3 (5 hrs)
┌──────────────────────────┐       ┌──────────────────────────┐       ┌──────────────────────────┐
│  • Apache Kafka Setup    │  ───► │  • Alert Ingestion &     │  ───► │  • Synthetic Data        │
│  • Spring Kafka Config   │       │    Deduplication Engine  │       │    Generator & Simulator │
│  • Unified Event Schema  │       │  • Sliding Window Buffer │       │  • End-to-End Test Run   │
└──────────────────────────┘       └──────────────────────────┘       └──────────────────────────┘
```

---

## 📅 Day-Wise Schedule & Task Breakdown

### 🗓️ Day 1: Streaming Infrastructure & Unified Normalization Schema (5 Hours)

#### Task 1.1: Kafka Infrastructure in Docker (1.5 Hours)
- Update `docker-compose.yml` to add Apache Kafka (KRaft mode, no Zookeeper dependency) listening on port `9092`.
- Configure Kafka topics automatically upon startup:
  - `telemetry.metrics` (time-series CPU, memory, bandwidth metrics)
  - `ingest.logs` (syslog, application log events)
  - `ingest.alerts` (SNMP traps, Prometheus alerts)
  - `ingest.config-changes` (ACL updates, interface config commits)

#### Task 1.2: Spring Kafka Integration in Java (2.0 Hours)
- Add `spring-kafka` dependency to `build.gradle`.
- Configure Kafka producers and consumers in `application.yaml` (`bootstrap-servers: localhost:9092`).
- Build Java DTOs for the **Unified Ingestion Schema**:
  - `NormalizedEvent`: (`eventId`, `timestampUtc`, `sourceDeviceId`, `sourceInterfaceId`, `eventType`, `severity`, `rawData`).

#### Task 1.3: Data Normalization Consumers & Pipeline (1.5 Hours)
- Create `TelemetryNormalizerService` to parse raw incoming payloads into ISO-8601 UTC aligned timestamps.
- Expose REST API endpoint for HTTP-based data ingestion as a backup to Kafka streaming.

---

### 🗓️ Day 2: Alert Ingestion & Sliding-Window Deduplication Engine (5 Hours)

#### Task 2.1: Multi-Source Alert Ingestion Pipeline (2.0 Hours)
- Support alert normalization for:
  - **SNMP Traps**: LinkUp/LinkDown, Enterprise Traps.
  - **Syslog Events**: Severity levels 0-4 (Emergency to Warning).
  - **Prometheus AlertManager Webhooks**: High CPU, memory saturation.
- Implement `AlertNormalizationService` to classify alert severity (`CRITICAL`, `WARNING`, `INFO`).

#### Task 2.2: Alert Storm Deduplication Engine (2.0 Hours)
- Implement a sliding time-window buffer (30-second window) to handle alert storms.
- Deduplication Logic:
  - Suppress duplicate alerts from the same device/interface within the time window.
  - Collapse 500 identical flapping alerts into 1 aggregated alert event with an `occurrenceCount` and `firstSeen` / `lastSeen` timestamps.

#### Task 2.3: Verification of Alert Suppression (1.0 Hour)
- Write unit tests verifying that 100 rapid duplicate alerts produce only 1 deduplicated event output.

---

### 🗓️ Day 3: Synthetic Data Generator & Pipeline Verification (5 Hours)

#### Task 3.1: Python Synthetic Network Failure Simulator (2.5 Hours)
- Build `ml-service/synthetic_generator.py` to simulate 3 realistic network incident cascades:
  1. **Cascade 1: Fiber Cut / Link Flap**: Port `GigE0/1` drops $\rightarrow$ BGP neighbor down $\rightarrow$ interface error storm $\rightarrow$ CPU spike.
  2. **Cascade 2: Memory Leak / CPU Saturation**: Gradual memory leak $\rightarrow$ packet drops $\rightarrow$ service timeout.
  3. **Cascade 3: Misconfigured ACL / Config Drift**: Config change applied $\rightarrow$ dropped packet count spikes $\rightarrow$ alert triggered.

#### Task 3.2: Streaming Pipeline Integration (1.5 Hours)
- Connect the Python generator to publish synthetic failure cascades directly to Kafka topics (`telemetry.metrics`, `ingest.alerts`, `ingest.logs`).
- Configure Java Kafka listeners to consume and log normalized events in real time.

#### Task 3.3: Phase 2 E2E Verification & Audit Log (1.0 Hour)
- Run synthetic failure generator and verify that Java backend receives, normalizes, deduplicates, and logs the incident stream correctly.

---

## 🎯 Phase 2 Final Deliverables
1. **Kafka Cluster**: Running Apache Kafka in KRaft mode via `docker-compose.yml`.
2. **Unified Data Schema**: Standardized JSON model for metrics, logs, alerts, and config changes.
3. **Alert Deduplication Engine**: 30-second sliding window deduplicator suppressing alert storms.
4. **Synthetic Data Generator**: Python simulator capable of generating 3 realistic network failure cascades.
5. **Ingestion Endpoints**: REST + Kafka consumer pipelines for streaming data.
