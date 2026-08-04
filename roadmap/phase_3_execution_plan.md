# Phase 3: Data Storage Layer - Detailed Execution Plan

## 📌 Phase Overview
Phase 3 establishes the persistent data storage foundation for the Network Anomaly Root-Cause Assistant. While Phase 1 provided the Graph DB (Neo4j) for network topology and Phase 2 built the Kafka event streaming pipeline, Phase 3 provisions multi-model data storage required for historical time-series metrics, searchable logs/alerts, and immutable incident audit logs.

---

## ⏱️ Time Estimation & Work Schedule
* **Total Estimated Effort**: 15 Hours
* **Daily Commitment**: 5 Hours / Day
* **Total Duration**: 3 Days

```
       DAY 1 (5 hrs)                      DAY 2 (5 hrs)                      DAY 3 (5 hrs)
┌──────────────────────────┐       ┌──────────────────────────┐       ┌──────────────────────────┐
│  • TimescaleDB Setup     │  ───► │  • Elasticsearch Setup   │  ───► │  • Storage Pipeline      │
│  • Metric Hypertables    │       │  • Log/Alert Indexing    │       │    Integration           │
│  • JPA Repositories      │       │  • Search Repositories   │       │  • E2E Storage Test      │
└──────────────────────────┘       └──────────────────────────┘       └──────────────────────────┘
```

---

## 📅 Day-Wise Schedule & Task Breakdown

### 🗓️ Day 1: Time-Series & Relational Storage with TimescaleDB (5 Hours)

#### Task 1.1: TimescaleDB Container in Docker (1.5 Hours)
- Update `docker-compose.yml` to add TimescaleDB (PostgreSQL 16 + Timescale extension) on port `5432`.
- Note: TimescaleDB handles both **time-series hypertables** (for metrics) AND **standard relational tables** (for incidents, audit logs, and config snapshots) in a single container.

#### Task 1.2: Spring Data JPA Configuration (2.0 Hours)
- Add `org.springframework.boot:spring-boot-starter-data-jpa` and `org.postgresql:postgresql` dependencies to `build.gradle`.
- Configure PostgreSQL connection parameters in `application.yaml`.
- Create JPA entities:
  - `MetricRecord.java`: (`id`, `timestampUtc`, `deviceId`, `interfaceId`, `metricName`, `value`)
  - `IncidentRecord.java`: (`incidentId`, `startTime`, `endTime`, `primaryDevice`, `status`, `severity`, `rootCauseSummary`)

#### Task 1.3: Hypertables & Metric Persistence Repositories (1.5 Hours)
- Create `MetricRecordRepository` and `IncidentRecordRepository`.
- Write SQL initialization script (`schema.sql`) to convert `metric_records` into a TimescaleDB hypertable for optimized time-bucket queries ($O(\log N)$ sliding window queries).

---

### 🗓️ Day 2: Elasticsearch Log & Alert Search Engine (5 Hours)

#### Task 2.1: Elasticsearch Container in Docker (1.5 Hours)
- Update `docker-compose.yml` to add Elasticsearch 8.x container on port `9200`.
- Disable security for local development (`xpack.security.enabled=false`).

#### Task 2.2: Spring Data Elasticsearch Integration (2.0 Hours)
- Add `org.springframework.boot:spring-boot-starter-data-elasticsearch` dependency to `build.gradle`.
- Configure Elasticsearch connection properties in `application.yaml` (`localhost:9200`).
- Create Elasticsearch Document entities:
  - `LogDocument.java`: (`id`, `timestamp`, `deviceId`, `logLevel`, `message`, `rawPayload`)
  - `AlertDocument.java`: (`id`, `timestamp`, `deviceId`, `interfaceId`, `alertName`, `severity`, `occurrenceCount`)

#### Task 2.3: Log & Alert Search Repositories (1.5 Hours)
- Create `LogSearchRepository` and `AlertSearchRepository`.
- Implement full-text search methods and time-range filter queries (`findByDeviceIdAndTimestampBetween`).

---

### 🗓️ Day 3: End-to-End Storage Pipeline & Verification (5 Hours)

#### Task 3.1: Automated Persistence Listeners (2.0 Hours)
- Connect Kafka event consumer to automatically write incoming Kafka streams to their respective data stores:
  - Metrics $\rightarrow$ TimescaleDB `MetricRecord`
  - Logs $\rightarrow$ Elasticsearch `LogDocument`
  - Alerts $\rightarrow$ Elasticsearch `AlertDocument` + TimescaleDB
- Implement batch insertion logic to optimize database write throughput.

#### Task 3.2: Storage Query REST Controllers (1.5 Hours)
- Create `StorageQueryController.java` with REST endpoints:
  - `GET /api/v1/storage/metrics/{deviceId}` (Range query for metric history)
  - `GET /api/v1/storage/logs/search` (Full-text log search)
  - `GET /api/v1/storage/incidents` (Incident history list)

#### Task 3.3: Phase 3 E2E Storage Verification (1.5 Hours)
- Run synthetic data generator from Phase 2.
- Verify that streaming failure cascades are successfully persisted across TimescaleDB, Elasticsearch, and Neo4j, and queryable via REST APIs.

---

## 🎯 Phase 3 Final Deliverables
1. **TimescaleDB Container**: PostgreSQL + TimescaleDB for metrics & relational incidents.
2. **Elasticsearch Container**: Search engine for full-text log and alert indexing.
3. **Spring Data JPA & Elasticsearch Repositories**: Complete Java persistence layer.
4. **Automated Stream-to-Store Pipeline**: Kafka consumer persisting streaming events.
5. **Storage Query APIs**: REST controllers for metric range queries, log search, and incident history.
