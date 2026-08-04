# Network Anomaly Root-Cause Assistant: Complete Roadmap

## Phase 1: The Foundation (Tech Stack & Service Architecture)
* Set up a robust Java-based backend (e.g., Spring Boot) to handle the heavy lifting of concurrent data ingestion streams, temporal alignment, and API routing.
* Spin up a separate Python microservice dedicated purely to the machine learning algorithms.
* Deploy a graph database (like Neo4j) to map out and query your network topology efficiently.
* Define clear service communication contracts between the Java backend and the Python ML service using gRPC (preferred for low-latency inference) or REST. Specify which calls are synchronous (real-time anomaly scoring) and which are asynchronous (batch model retraining, bulk historical analysis) via a message queue like RabbitMQ or Kafka topics.
* Set up API contracts (OpenAPI/Protobuf schemas) for the frontend-to-backend interface, including authentication and authorization middleware (e.g., Spring Security with JWT).

## Phase 2: Data Ingestion & Normalization
* Stream your telemetry, logs, **alerts**, and configuration changes using a message broker like Apache Kafka. Dedicate separate Kafka topics per data type (e.g., `telemetry.metrics`, `ingest.logs`, `ingest.alerts`, `ingest.config-changes`).
* Standardize the incoming chaos. Parse logs, metrics, and alerts into a unified, structured format (like JSON) with strict, UTC-aligned timestamps to prevent temporal bleeding across your data streams.
* **Alert Ingestion Pipeline**: Ingest alerts from heterogeneous sources — SNMP traps, Syslog severity events, Prometheus AlertManager webhooks, PagerDuty/OpsGenie integrations, and custom threshold-based alerts. Normalize alert schemas to include: source, severity level (critical/warning/info), affected component, timestamp, and raw payload. Implement deduplication logic to suppress alert storms (e.g., 500 identical alerts from a flapping interface should collapse into one event with a count).
* Build a **synthetic data generator** that produces realistic network telemetry, failure cascades, configuration drift, and alert storms. This is essential for development, testing, and demo purposes when real production data is unavailable. Model common failure patterns: link flaps, BGP session drops, CPU saturation cascades, memory leaks, and misconfigured ACLs.

## Phase 3: Data Storage Layer
* Deploy a **time-series database** (e.g., TimescaleDB, InfluxDB, or VictoriaMetrics) for high-throughput metric storage with efficient range queries over sliding time windows.
* Set up **Elasticsearch** (or Grafana Loki) as the log and alert search engine, enabling full-text search, field-level filtering, and aggregation over parsed log/alert events.
* Use **PostgreSQL** (or another relational store) for structured data: incident records, configuration snapshots, hypothesis audit logs, user sessions, and system metadata.
* Neo4j (already provisioned in Phase 1) stores the live topology graph — nodes, edges, dependencies, and their real-time health states.
* Define data retention policies per store (e.g., raw metrics: 30 days, aggregated metrics: 1 year, audit logs: indefinite/immutable).

## Phase 4: Anomaly Detection (The Math)
* Deploy statistical models for time-series anomaly detection. For univariate metrics (like CPU spikes), utilize moving averages and Z-score thresholding ($Z = \frac{X - \mu}{\sigma}$).
* For high-dimensional, complex telemetry, implement Isolation Forests or Autoencoders. These will help identify non-linear outlier patterns within rolling temporal windows by measuring the reconstruction error of the data points.
* **Missing-Evidence Detection**: Implement heartbeat and signal-presence monitors that detect *expected-but-absent* data — missing heartbeats from a node, gaps in metric reporting windows, or silent log sources. The absence of a signal can be as diagnostic as its presence (e.g., no SNMP polls returned from a switch = likely device unreachability). Flag these as a distinct evidence category: `MISSING_EVIDENCE`.

## Phase 5: Causal Inference (The Brains)
* Model your system infrastructure as a Directed Acyclic Graph (DAG), where nodes are network components and edges represent flow or dependencies.
* To separate correlation from causation, apply Granger Causality tests to your time-series data to mathematically evaluate if one metric's temporal behavior significantly predicts another's.
* Implement a Bayesian Belief Network over your DAG to calculate the conditional probability of various root-cause nodes given the observed anomaly evidence and configuration changes.

## Phase 6: The Explainable Assistant
* Aggregate the outputs of your causal graph (the highest-probability nodes, relevant log snippets, and time-deltas) and pass them as context to a Large Language Model.
* Prompt the LLM to translate these mathematical relationships into an auditor-friendly incident timeline, clearly delineating between three distinct categories:
    1. **Confirmed Evidence** — hard facts observed in telemetry, logs, or alerts (e.g., "Interface GigE0/1 went DOWN at 14:03:12 UTC").
    2. **Correlated Signals** — statistically associated events that may or may not be causal (e.g., "CPU on Router-B spiked to 94% within 30s of the link failure, Granger p-value = 0.03").
    3. **Missing Evidence** — expected signals that were absent, flagged by Phase 4's missing-evidence detection (e.g., "No OSPF hello packets received from Router-C after 14:03:00 — expected every 10s").
* Generate **recommended next diagnostic steps**: suggest specific commands to run (`show ip bgp summary`, `traceroute`), logs to inspect, or metrics to monitor, prioritized by expected diagnostic value.

## Phase 7: Audit Trail & Incident Persistence
* Implement an **event-sourced, append-only audit log** that records every step of the investigation lifecycle:
    - Initial anomaly detection trigger (timestamp, source, severity)
    - Each hypothesis generated, with its probability score and supporting evidence snapshot
    - Ranking changes as new evidence arrives
    - Diagnostic recommendations issued
    - Analyst actions (acknowledged, dismissed, escalated)
    - Final resolution and root-cause determination
* Store audit records in an immutable, write-ahead log (e.g., PostgreSQL with append-only constraints, or a dedicated event store like EventStoreDB). Every record must include a timestamp, actor (system or analyst), action type, and a serialized evidence payload.
* Provide an **incident history API** that supports querying past incidents by time range, affected components, root-cause category, and resolution status — enabling post-mortem review and trend analysis.
* Maintain versioned hypothesis snapshots so reviewers can see how the system's understanding of an incident evolved over time.

## Phase 8: The Interface
* Construct a clean frontend dashboard. Visualize the topology graph, highlighting the "blast radius" of the incident, and display the ranked hypotheses neatly on the side.
* Include an interactive incident timeline view showing events, anomalies, and causal links on a time axis.
* Surface the three-way evidence classification (confirmed / correlated / missing) with distinct visual treatments (e.g., green checkmarks, amber links, red question marks).
* Provide an audit trail viewer — a chronological feed of every system action and analyst interaction for the current incident.

## Phase 9: Testing & Validation
* **Unit Tests**: Cover all core logic — anomaly scoring algorithms, Granger Causality computation, Bayesian posterior updates, alert deduplication, and evidence classification.
* **Integration Tests**: Validate the end-to-end pipeline: Kafka ingestion → normalization → anomaly detection → causal inference → LLM report generation → audit trail persistence.
* **Scenario Tests**: Use the synthetic data generator (Phase 2) to simulate known failure patterns (e.g., cascading link failure, BGP misconfiguration, config drift causing packet loss). Assert that the system correctly identifies the root cause and ranks it highest.
* **Regression Tests**: Ensure that model updates or code changes don't degrade hypothesis accuracy on a curated test suite of historical incidents.

## Phase 10: Deployment & Infrastructure
* **Containerize** every service: Java backend, Python ML service, Neo4j, TimescaleDB, Elasticsearch, Kafka — each in its own Docker image with multi-stage builds for lean production images.
* Provide a **Docker Compose** configuration for local development, enabling the full stack to spin up with a single command.
* Author **Kubernetes manifests** (or Helm charts) for production deployment: define resource limits, health checks, horizontal pod autoscaling for the ingestion layer, and persistent volume claims for databases.
* Set up a **CI/CD pipeline** (GitHub Actions / GitLab CI / Jenkins): lint → test → build → container push → staged deployment (dev → staging → production).
* Define environment-specific configuration (dev/staging/prod) using Spring Profiles and environment variables, with secrets managed via Kubernetes Secrets or HashiCorp Vault.

---
**TL;DR**
Use a Java backend for data ingestion (telemetry, logs, **alerts**, config changes) and a Python service for ML, communicating over gRPC. Store metrics in TimescaleDB, logs/alerts in Elasticsearch, topology in Neo4j, and incidents/audit logs in PostgreSQL. Detect outliers using Z-scores, Isolation Forests, and missing-signal monitors. Trace root causes probabilistically using DAGs, Granger Causality, and Bayesian Networks. Use an LLM to generate human-readable incident reports that clearly separate confirmed evidence, correlated signals, and missing evidence. Persist every investigative step in an immutable audit trail. Deploy via Docker Compose (dev) and Kubernetes (prod) with full CI/CD.
