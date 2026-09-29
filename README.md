# Network Anomaly Assistant

Network Anomaly Assistant is an intelligent Network Reliability Engineering (NRE) platform that combines real-time telemetry ingestion, machine learning anomaly detection, causal inference analysis, and AI-powered incident investigation to help network operations teams identify, diagnose, and resolve network anomalies.

## Core Capabilities
- **Real-Time Anomaly Detection**: Statistical Z-score analysis on streaming metric data.
- **Root-Cause Analysis**: Bayesian posterior probability computation and Granger causality testing.
- **AI Copilot Reports**: Google Gemini-powered investigation reports with actionable CLI diagnostics.
- **Network Topology Visualization**: Interactive blast-radius simulation and mapping.
- **Full-Text Log Search**: Elasticsearch-powered search across all logs and alerts.

## Technology Stack
- **Frontend**: React, Vite, React Router, ForceGraph2D
- **Backend**: Java 17, Spring Boot, Spring Kafka, Spring Security (JWT)
- **ML Service**: Python 3.11, FastAPI, gRPC, scikit-learn, google-genai
- **Databases & Infrastructure**: TimescaleDB (PostgreSQL), Neo4j, Apache Kafka, Elasticsearch

## Quick Start

1. **Start the Infrastructure**
   ```bash
   docker compose up -d neo4j kafka timescale elasticsearch
   ```

2. **Start the ML Service**
   ```bash
   cd ml-service
   pip install -r requirements.txt
   python -m app.main
   ```

3. **Start the Backend**
   ```bash
   ./gradlew bootRun
   ```

4. **Start the Frontend**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

For a comprehensive guide, architecture details, and complete setup instructions, see the [Operations Guide](guide.md).
