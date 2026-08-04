# Phase 4: Anomaly Detection Engine (The Math) - Detailed Execution Plan

## 📌 Phase Overview
Phase 4 builds the core mathematical and statistical anomaly detection algorithms of the Network Anomaly Root-Cause Assistant. Located primarily in the Python ML microservice (and integrated with the Java backend via gRPC), Phase 4 moves beyond basic thresholding to implement multi-variate Isolation Forests, rolling Z-score statistical monitors, and missing-evidence (heartbeat gap) detectors.

---

## ⏱️ Time Estimation & Work Schedule
* **Total Estimated Effort**: 15 Hours
* **Daily Commitment**: 5 Hours / Day
* **Total Duration**: 3 Days

```
       DAY 1 (5 hrs)                      DAY 2 (5 hrs)                      DAY 3 (5 hrs)
┌──────────────────────────┐       ┌──────────────────────────┐       ┌──────────────────────────┐
│  • Rolling Window Math   │  ───► │  • Isolation Forest      │  ───► │  • Missing-Evidence &    │
│  • Dynamic Z-Score Engine│       │    Multi-Variate Engine  │       │    Heartbeat Detection   │
│  • Statistical Service   │       │  • Autoencoder Outliers  │       │  • End-to-End Pipeline   │
└──────────────────────────┘       └──────────────────────────┘       └──────────────────────────┘
```

---

## 📅 Day-Wise Schedule & Task Breakdown

### 🗓️ Day 1: Statistical Time-Series Anomaly Engine (Rolling Z-Scores) (5 Hours)

#### Task 1.1: Rolling Window Statistical Analyzer (2.0 Hours)
- Implement `ml-service/app/services/statistical_engine.py`.
- Calculate dynamic rolling mean ($\mu$) and rolling standard deviation ($\sigma$) over sliding time windows (e.g., 5-minute rolling windows) to adapt to baseline network shifts (e.g., day vs. night traffic volume).
- Compute dynamic Z-Scores ($Z = \frac{X - \mu}{\sigma}$) for univariate metrics (CPU, Memory, Packet Drops).

#### Task 1.2: Advanced Anomaly Classification (1.5 Hours)
- Classify anomaly types: `SPIKE` ($Z > 3.0$), `DROP` ($Z < -3.0$), `FLAPPING` (rapid directional changes), and `LEVEL_SHIFT` (sustained step change).
- Return normalized anomaly confidence scores ($0.0 \rightarrow 1.0$).

#### Task 1.3: Integration with gRPC Service (1.5 Hours)
- Update `anomaly_service.py` to route incoming telemetry metric batches through the dynamic rolling Z-score engine.

---

### 🗓️ Day 2: Multi-Variate Anomaly Detection (Isolation Forests) (5 Hours)

#### Task 2.1: Isolation Forest Model Implementation (2.0 Hours)
- Implement `ml-service/app/services/isolation_forest_engine.py` using `scikit-learn`'s `IsolationForest`.
- Process high-dimensional telemetry vectors (e.g., combining CPU + RAM + Ingress Bandwidth + Egress Errors simultaneously) to detect non-linear multi-variate anomalies that univariate Z-scores miss.

#### Task 2.2: Model Preprocessing & Normalization (1.5 Hours)
- Implement standard scaling (`StandardScaler`) for feature vector normalization.
- Compute decision function anomaly scores and map isolation depth to anomaly probability ($0.0 \rightarrow 1.0$).

#### Task 2.3: Protobuf & gRPC Extension (1.5 Hours)
- Update `anomaly_detection.proto` to support multi-variate feature vectors.
- Re-compile gRPC stubs in Java and Python.

---

### 🗓️ Day 3: Missing-Evidence Detection & Heartbeat Gap Engine (5 Hours)

#### Task 3.1: Heartbeat & Signal-Presence Monitor in Java (2.0 Hours)
- Implement `SignalPresenceMonitorService.java` in Java backend.
- Maintain a registry of expected device heartbeats and metric windows.
- Detect expected-but-absent signals (e.g., no SNMP polls received from `router-core-01` for > 30 seconds).

#### Task 3.2: Missing Evidence Event Classification (1.5 Hours)
- Flag signal gaps as a distinct diagnostic category: `MISSING_EVIDENCE`.
- Note: In root-cause analysis, the absence of a signal (e.g., silent device) is as diagnostic as a high CPU alert.

#### Task 3.3: Phase 4 End-to-End Anomaly Verification (1.5 Hours)
- Run synthetic generator with multi-variate metric spikes and simulated signal drops.
- Verify that Z-scores, Isolation Forests, and Missing-Evidence events are detected and reported via gRPC & REST APIs.

---

## 🎯 Phase 4 Final Deliverables
1. **Dynamic Z-Score Engine**: Rolling window statistical time-series anomaly detector.
2. **Isolation Forest Model**: Multi-variate outlier detector for multi-dimensional telemetry vectors.
3. **Missing-Evidence Engine**: Heartbeat and signal-presence gap detector flagging `MISSING_EVIDENCE`.
4. **Enhanced gRPC Service**: Python ML anomaly detection service supporting statistical and ML models.
