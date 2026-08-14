# Phase 9: Testing & Validation Suite - Detailed Execution Plan

## 📌 Phase Overview
Phase 9 establishes a production-grade automated testing and validation suite. It verifies unit-level accuracy of mathematical algorithms (Z-scores, Isolation Forests, Granger Causality, Bayesian Belief Networks), integration testing of multi-service pipelines (Java $\leftrightarrow$ Python over gRPC & REST), and end-to-end failure cascade scenario testing.

---

## ⏱️ Time Estimation & Work Schedule
* **Total Estimated Effort**: 15 Hours
* **Daily Commitment**: 5 Hours / Day
* **Total Duration**: 3 Days

```
       DAY 1 (5 hrs)                      DAY 2 (5 hrs)                      DAY 3 (5 hrs)
┌──────────────────────────┐       ┌──────────────────────────┐       ┌──────────────────────────┐
│  • Java Unit Tests       │  ───► │  • Python ML Test Suite  │  ───► │  • End-to-End Pipeline  │
│  • Alert Deduplication   │       │  • Pytest IsolationForest│       │    Scenario Testing      │
│  • Causal DAG & Context  │       │  • Pytest Granger/Bayes  │       │  • Failure Cascades Assert│
└──────────────────────────┘       └──────────────────────────┘       └──────────────────────────┘
```

---

## 📅 Day-Wise Schedule & Task Breakdown

### 🗓️ Day 1: Java Unit & Service Integration Tests (5 Hours)

#### Task 1.1: Alert Deduplication & Ingestion Unit Tests (2.0 Hours)
- Create `src/test/java/com/example/networkanomalyassistant/service/AlertDeduplicationServiceTest.java`.
- Test 30-second sliding-window alert deduplication (500 identical interface flap alerts collapse into 1 event with `occurrenceCount = 500`).

#### Task 1.2: Causal DAG & Context Aggregator Tests (1.5 Hours)
- Create `CausalGraphBuilderServiceTest.java` and `IncidentContextAggregatorServiceTest.java`.
- Verify parent-child DAG edge orientation and 3-Way Evidence Matrix categorization (Confirmed, Correlated, Missing).

#### Task 1.3: Audit Trail Service Tests (1.5 Hours)
- Create `IncidentAuditServiceTest.java` asserting state transition history (`DETECTED` $\rightarrow$ `CORRELATED` $\rightarrow$ `ROOT_CAUSE_CONFIRMED` $\rightarrow$ `RESOLVED`).

---

### 🗓️ Day 2: Python ML Engine Test Suite (pytest) (5 Hours)

#### Task 2.1: Statistical & Isolation Forest Unit Tests (2.0 Hours)
- Create `ml-service/tests/test_statistical_engine.py` and `ml-service/tests/test_isolation_forest.py`.
- Assert univariate Z-score spike/drop classification and multi-variate Isolation Forest outlier probability scores.

#### Task 2.2: Granger Causality & Bayesian Engine Tests (2.0 Hours)
- Create `ml-service/tests/test_granger_causality.py` and `ml-service/tests/test_bayesian_engine.py`.
- Assert Granger $p$-value calculation ($X \rightarrow Y$ leading lag detection) and Bayesian posterior probability normalization ($\sum P = 1.0$).

#### Task 2.3: FastAPI & LLM Engine Tests (1.0 Hour)
- Create `ml-service/tests/test_llm_assistant_engine.py` verifying prompt generation and dynamic CLI command parsing.

---

### 🗓️ Day 3: End-to-End Failure Cascade Scenario Testing (5 Hours)

#### Task 3.1: Synthetic Failure Cascade Scenario Test (2.5 Hours)
- Create `src/test/java/com/example/networkanomalyassistant/scenario/FailureCascadeScenarioTest.java`.
- Simulate 3 classic network failure patterns:
  1. **Fiber Cut Cascade**: Core Router link down $\rightarrow$ BGP session drop $\rightarrow$ downstream Edge Switch latency spike.
  2. **Memory Leak Cascade**: Edge Switch memory leak $\rightarrow$ process crash $\rightarrow$ silent heartbeat gap (`MISSING_EVIDENCE`).
  3. **Config Drift Cascade**: Router ACL misconfiguration $\rightarrow$ packet drop spike $\rightarrow$ Granger causality trigger.

#### Task 3.2: Automated Regression Assertion Suite (1.5 Hours)
- Assert that the primary root-cause device is correctly identified and ranked #1 with `HIGH` confidence (>80%) across all scenarios.

#### Task 3.3: Phase 9 Test Suite Execution & Verification (1.0 Hour)
- Run `./gradlew test` and `pytest` verifying 100% test pass rate!

---

## 🎯 Phase 9 Final Deliverables
1. **Java Unit & Service Tests**: JUnit 5 + Mockito tests covering deduplication, DAG building, and audit trails.
2. **Python Pytest Suite**: Pytest coverage for Z-scores, Isolation Forests, Granger Causality, and Bayesian engines.
3. **End-to-End Scenario Suite**: Automated scenario testing simulating Fiber Cuts, BGP Drops, and Config Drift.
