# Phase 6: The Explainable Assistant (LLM Integration) - Detailed Execution Plan

## 📌 Phase Overview
Phase 6 bridges the gap between complex mathematical outputs (Granger $p$-values, Isolation Forest decision scores, Bayesian posterior probabilities) and human network engineers. It aggregates the causal graph, relevant logs, and missing evidence into a structured prompt, and uses an LLM (via OpenAI/Anthropic/Ollama or LangChain API) to generate human-readable, auditor-friendly incident reports with 3-way evidence classification.

---

## ⏱️ Time Estimation & Work Schedule
* **Total Estimated Effort**: 15 Hours
* **Daily Commitment**: 5 Hours / Day
* **Total Duration**: 3 Days

```
       DAY 1 (5 hrs)                      DAY 2 (5 hrs)                      DAY 3 (5 hrs)
┌──────────────────────────┐       ┌──────────────────────────┐       ┌──────────────────────────┐
│  • Context Aggregator    │  ───► │  • Structured Prompting  │  ───► │  • LLM Service Bridge    │
│  • 3-Way Evidence Matrix │       │    & Audit Timeline      │       │  • Incident Report API   │
│  • Context Builder       │       │  • Diagnostic Steps      │       │  • E2E Assistant Run     │
└──────────────────────────┘       └──────────────────────────┘       └──────────────────────────┘
```

---

## 📅 Day-Wise Schedule & Task Breakdown

### 🗓️ Day 1: Multi-Source Context Aggregator & 3-Way Evidence Matrix (5 Hours)

#### Task 1.1: Incident Context Aggregator (2.0 Hours)
- Create `IncidentContextAggregatorService.java` in Java backend.
- Collects all incident details in one place:
  - Top Ranked Root Cause Node & DAG Subgraph (Phase 5)
  - Historical Telemetry Metrics from TimescaleDB (Phase 3)
  - Filtered Syslog & Config Change logs from Elasticsearch (Phase 3)
  - Missing-Evidence Heartbeat Gaps (Phase 4)

#### Task 1.2: 3-Way Evidence Matrix Builder (1.5 Hours)
- Classify incident observations into 3 distinct categories:
  1. **Confirmed Evidence**: Hard facts (e.g., `LINK_DOWN` alerts, `CONFIG_CHANGE` logs).
  2. **Correlated Signals**: Statistically associated metrics (e.g., Granger $p$-value $< 0.05$ CPU spikes).
  3. **Missing Evidence**: Expected signals that were absent (e.g., `HEARTBEAT_GAP` timeouts).

#### Task 1.3: Aggregator DTOs & REST Endpoint (1.5 Hours)
- Create `IncidentContextDto.java` and expose `GET /api/v1/explainability/context/{incidentId}`.

---

### 🗓️ Day 2: Structured Prompt Engineering & Diagnostic Recommendation Engine (5 Hours)

#### Task 2.1: Python LLM Service Engine (2.5 Hours)
- Implement `ml-service/app/services/llm_assistant_engine.py` (supporting OpenAI, Ollama, or fallback template generator).
- Design system prompts that instruct the LLM to format incident reports without hallucination:
  - **Executive Summary**
  - **Root Cause Hypothesis & Confidence Score**
  - **Auditor-Friendly Chronological Timeline**
  - **3-Way Evidence Classification Matrix**
  - **Recommended Diagnostic & Remediation Commands** (e.g., `show ip bgp summary`, `traceroute`).

#### Task 2.2: Fallback Deterministic Report Engine (1.5 Hours)
- Build a robust fallback template generator so the system produces clean markdown reports even when an LLM API key is not provided.

#### Task 2.3: FastAPI Endpoint Integration (1.0 Hour)
- Expose `POST /generate/incident-report` in Python `app/main.py`.

---

### 🗓️ Day 3: Java LLM Bridge & End-to-End Assistant Verification (5 Hours)

#### Task 3.1: Java Assistant Service Integration (2.0 Hours)
- Create `ExplainableAssistantService.java` and `ExplainableAssistantController.java` (`POST /api/v1/assistant/explain/{incidentId}`).
- Automatically fetches aggregated context from Day 1 and requests structured report generation from Day 2.

#### Task 3.2: Report Storage & Audit Logging Integration (1.5 Hours)
- Update `IncidentRecord` in TimescaleDB to persist generated markdown reports and diagnostic recommendations.

#### Task 3.3: Phase 6 End-to-End Verification Run (1.5 Hours)
- Run synthetic failure cascade (Fiber Cut $\rightarrow$ BGP Drop $\rightarrow$ CPU Spike).
- Trigger LLM Explainable Assistant endpoint and verify the complete markdown report output containing executive summary, timeline, 3-way evidence split, and CLI commands!

---

## 🎯 Phase 6 Final Deliverables
1. **Incident Context Aggregator**: Java service assembling metrics, logs, alerts, DAG, and missing evidence.
2. **3-Way Evidence Matrix**: Explicit classification of Confirmed, Correlated, and Missing evidence.
3. **Structured Prompt & LLM Engine**: Python service producing auditor-friendly markdown incident reports.
4. **Diagnostic Command Generator**: Recommends actionable CLI diagnostic commands.
5. **Explainable Assistant API**: `POST /api/v1/assistant/explain/{incidentId}` endpoint.
