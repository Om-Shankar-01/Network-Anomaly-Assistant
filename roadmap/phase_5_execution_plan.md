# Phase 5: Causal Inference (The Brains) - Detailed Execution Plan

## 📌 Phase Overview
Phase 5 builds "The Brains" of the Network Anomaly Root-Cause Assistant. While Phase 4 detected anomaly spikes and signal gaps, Phase 5 elevates the system from simple correlation ("Device A and Device B both had errors at 14:00") to true causal reasoning ("Device A's interface drop caused Device B's CPU spike"). It combines DAG topology extraction, Granger Causality time-series tests, and Bayesian Belief Networks to rank probable root causes.

---

## ⏱️ Time Estimation & Work Schedule
* **Total Estimated Effort**: 15 Hours
* **Daily Commitment**: 5 Hours / Day
* **Total Duration**: 3 Days

```
       DAY 1 (5 hrs)                      DAY 2 (5 hrs)                      DAY 3 (5 hrs)
┌──────────────────────────┐       ┌──────────────────────────┐       ┌──────────────────────────┐
│  • Neo4j Topology to DAG │  ───► │  • Granger Causality     │  ───► │  • Bayesian Belief Net   │
│  • Dependency Pathing    │       │    Statistical Engine    │       │  • Posterior Probability │
│  • DAG Model Builder     │       │  • Python statsmodels    │       │  • Hypothesis Ranker     │
└──────────────────────────┘       └──────────────────────────┘       └──────────────────────────┘
```

---

## 📅 Day-Wise Schedule & Task Breakdown

### 🗓️ Day 1: Topology Graph to DAG Transformation (5 Hours)

#### Task 1.1: Dependency Flow & DAG Model (2.0 Hours)
- Create `DagGraph.java` and `DagNode.java` in Java backend.
- Transform Neo4j network topology graph into a Directed Acyclic Graph (DAG) by orienting edges along traffic flow and service dependency direction (Upstream Core $\rightarrow$ Distribution $\rightarrow$ Edge $\rightarrow$ Host).

#### Task 1.2: Ancestor & Downstream Impact Pathing (1.5 Hours)
- Implement `CausalGraphBuilderService.java` in Java to query Neo4j for all upstream ancestor candidate nodes for any anomalous device.

#### Task 1.3: DAG Extraction REST API (1.5 Hours)
- Expose REST API endpoint (`GET /api/v1/causal/dag/{incidentDeviceId}`) returning the localized DAG subgraph for an incident.

---

### 🗓️ Day 2: Granger Causality Testing Engine in Python (5 Hours)

#### Task 2.1: Python Granger Causality Engine (2.5 Hours)
- Implement `ml-service/app/services/granger_causality_engine.py` using `statsmodels.tsa.stattools.grangercausalitytests`.
- Evaluates whether metric time-series $X(t)$ (e.g., Core Router interface errors) statistically predicts metric time-series $Y(t)$ (e.g., Edge Switch latency) at lag $k$.
- Returns F-test statistics and $p$-values ($p < 0.05 \implies$ Causal direction supported).

#### Task 2.2: Statistical Validation & Multi-Lag Evaluation (1.5 Hours)
- Handle stationarity checking (ADF - Augmented Dickey-Fuller test) and differencing for time-series metrics.

#### Task 2.3: Inter-Service gRPC Integration (1.0 Hour)
- Implement gRPC endpoint handling `CausalGraphRequest` and returning Granger $p$-values between linked nodes.

---

### 🗓️ Day 3: Bayesian Belief Network & Root-Cause Hypothesis Ranker (5 Hours)

#### Task 3.1: Bayesian Inference Engine (2.5 Hours)
- Implement `ml-service/app/services/bayesian_engine.py`.
- Computes conditional posterior probabilities $P(\text{RootCause} = \text{Node}_i \mid \text{Observed Evidence})$ using:
  1. **Prior Probabilities**: Base failure rate of device types + config change events.
  2. **Likelihoods**: Granger causality $p$-values + Phase 4 anomaly scores + missing-evidence gaps.
  3. **Bayes' Theorem**: Updates posterior probability distribution over candidate root-cause nodes.

#### Task 3.2: Ranked Hypothesis Result Formatter (1.5 Hours)
- Sort candidate nodes by posterior probability ($0.0 \rightarrow 1.0$).
- Output structured root-cause hypotheses with supporting evidence nodes and confidence ratings.

#### Task 3.3: Phase 5 End-to-End Causal Inference Test (1.0 Hour)
- Run synthetic failure cascade (Fiber Cut $\rightarrow$ BGP Drop $\rightarrow$ CPU Spike).
- Verify that the system correctly distinguishes root cause (`router-core-01:GigE0/1`) from correlated downstream noise (`router-edge-02:CPU`).

---

## 🎯 Phase 5 Final Deliverables
1. **DAG Graph Extractor**: Converts Neo4j topology into localized dependency DAGs.
2. **Granger Causality Engine**: Statistical temporal prediction engine using `statsmodels`.
3. **Bayesian Belief Network Engine**: Calculates conditional posterior probabilities $P(\text{RootCause} \mid \text{Evidence})$.
4. **Ranked Hypothesis Generator**: Produces probability-ranked root-cause candidate lists.
