# Phase 1: Tech Stack & Service Architecture - Detailed Execution Plan

## 📌 Phase Overview
Phase 1 lays the architectural foundation of the Network Anomaly Root-Cause Assistant. It ensures that the Java Spring Boot backend, Python ML microservice, Neo4j Graph Database, inter-service contracts (gRPC/REST), and security baseline are fully established before ingestion and analytics pipelines are constructed.

---

## ⏱️ Time Estimation & Work Schedule
* **Total Estimated Effort**: 15 Hours
* **Daily Commitment**: 5 Hours / Day
* **Total Duration**: 3 Days

```
       DAY 1 (5 hrs)                      DAY 2 (5 hrs)                      DAY 3 (5 hrs)
┌──────────────────────────┐       ┌──────────────────────────┐       ┌──────────────────────────┐
│  • Docker & Neo4j Setup  │  ───► │  • Python ML Service     │  ───► │  • Spring Security & JWT │
│  • SDN Entities & Repos  │       │  • Protobuf / Contracts  │       │  • Base Topology APIs    │
│  • OpenAPI & Base Java   │       │  • Inter-Service Client  │       │  • E2E Integration Test  │
└──────────────────────────┘       └──────────────────────────┘       └──────────────────────────┘
```

---

## 📅 Day-Wise Schedule & Task Breakdown

### 🗓️ Day 1: Infrastructure, Neo4j Integration & Core Spring Boot (5 Hours)

#### Task 1.1: Containerized Infrastructure Setup (1.5 Hours)
- Create `docker-compose.yml` in the project root.
- Configure Neo4j Community Edition container:
  - Bolt Protocol Port: `7687`
  - HTTP Browser Port: `7474`
  - Set default authentication (`NEO4J_AUTH=neo4j/password`).
  - Configure local data volume persistence.
- Validate local container execution and Cypher shell accessibility.

#### Task 1.2: Spring Data Neo4j (SDN) Configuration & Domain Entities (2.0 Hours)
- Configure connection parameters in `src/main/resources/application.yml`.
- Implement core domain entity models in `com.example.networkanomalyassistant.model`:
  - `@Node("Device")`: Represents routers, switches, and servers (`id`, `hostname`, `ipAddress`, `deviceType`).
  - `@Node("Interface")`: Represents physical/logical interfaces (`interfaceName`, `macAddress`, `bandwidth`).
  - `@RelationshipProperties`: Defines directional relationships (`CONNECTED_TO`, `DEPENDS_ON`).
- Create SDN repository interfaces (`DeviceRepository`, `TopologyRepository`) for graph queries.
- Write unit/integration tests to verify node creation and graph traversal.

#### Task 1.3: Base REST & OpenAPI Configuration (1.5 Hours)
- Add `springdoc-openapi-starter-webmvc-ui` dependency to `build.gradle`.
- Define standardized REST API wrappers (`ApiResponse<T>`) and error response schemas (`ErrorDetail`).
- Implement global exception handling using `@ControllerAdvice`.

---

### 🗓️ Day 2: Python ML Microservice & Inter-Service Communication (5 Hours)

#### Task 2.1: Python ML Microservice Initialization (2.0 Hours)
- Initialize `ml-service/` subproject directory.
- Setup Python virtual environment (`venv`) and `requirements.txt` with key libraries:
  - `fastapi`, `uvicorn`, `grpcio`, `grpcio-tools`, `pydantic`, `numpy`, `scikit-learn`, `torch`.
- Establish modular code organization:
  - `ml-service/app/main.py` (Service entrypoint)
  - `ml-service/app/api/` (API router modules)
  - `ml-service/app/services/` (Model logic wrappers)
- Implement health endpoint (`GET /health`) and lifecycle management hooks.

#### Task 2.2: Inter-Service Contract & Protobuf Definitions (1.0 Hour)
- Create shared `proto/` directory for gRPC service contracts:
  - `anomaly_detection.proto`: Message definitions for batch metric anomaly evaluation.
  - `causal_inference.proto`: Message definitions for topology graph + anomaly inputs yielding root-cause likelihoods.
- Configure Gradle Protobuf plugin for Java stub generation.
- Execute `grpcio-tools` code generation for Python server stubs.

#### Task 2.3: Java Inter-Service Client Integration (1.0 Hour)
- Configure gRPC ManagedChannel / Client Bean in Spring Boot pointing to Python service (`localhost:50051`).
- Add Resilience4j circuit breaker / retry logic to handle ML service availability gracefully.

---

### 🗓️ Day 3: Authentication, Base Endpoints & Phase 1 E2E Verification (5 Hours)

#### Task 3.1: Spring Security & JWT Setup (2.0 Hours)
- Add `spring-boot-starter-security` and `jjwt` dependencies.
- Implement security components:
  - `JwtTokenProvider`: Token issuance, validation, and claim extraction.
  - `JwtAuthenticationFilter`: Request filter for validating Bearer tokens.
  - `SecurityConfig`: Session management (stateless), CORS configuration, and endpoint authorization rules.
  - `AuthController`: Endpoint (`/api/v1/auth/login`) for authentication requests.

#### Task 3.2: System Health & Topology Base Controller (1.0 Hour)
- Implement `SystemHealthController` (`/api/v1/system/health`) to aggregate status of:
  - Java Backend Service
  - Neo4j Database Connectivity
  - Python ML Service Ping
- Implement `TopologyController` (`/api/v1/topology`) for querying current graph nodes and relationships.

#### Task 3.3: Phase 1 End-to-End Integration Verification (1.0 Hour)
- Boot all components concurrently (Docker Neo4j, Python ML Service, Java Application).
- Execute verification workflow:
  1. Authenticate via JWT API.
  2. Query system health endpoint (ensure all 3 components report `UP`).
  3. Trigger test ML ping via Java gRPC client and receive structured output.
  4. Verify Swagger UI accessibility at `http://localhost:8080/swagger-ui.html`.

---

## 🎯 Phase 1 Final Deliverables
1. **Containerized Infrastructure**: Functional `docker-compose.yml` with Neo4j.
2. **Spring Boot Core**: Configured backend with SDN graph domain models and repositories.
3. **Python ML Service**: Running `ml-service/` base application.
4. **Data Contracts**: Compiled gRPC Protobuf schemas shared between Java and Python.
5. **Security & Docs**: Working JWT authentication and interactive Swagger UI.
6. **Aggregated Health Endpoint**: `/api/v1/system/health` checking all service dependencies.
