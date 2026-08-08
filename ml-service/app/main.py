from fastapi import FastAPI
from pydantic import BaseModel
from typing import List, Dict, Any
from datetime import datetime, timezone
from app.services.isolation_forest_engine import IsolationForestEngine
from app.services.granger_causality_engine import GrangerCausalityEngine
from app.services.bayesian_engine import BayesianCausalEngine
from app.services.llm_assistant_engine import LLMAssistantEngine

app = FastAPI(
    title="Network Anomaly ML Microservice",
    version="1.0.0",
    description="Python service for statistical anomaly detection, Isolation Forests, Granger Causality, Bayesian Belief Networks, and LLM Report Generation."
)

iso_engine = IsolationForestEngine(contamination=0.15)
granger_engine = GrangerCausalityEngine(max_lag=3)
bayesian_engine = BayesianCausalEngine()
llm_engine = LLMAssistantEngine()

class HealthResponse(BaseModel):
    status: str
    service: str
    timestamp: str

class MultiVariateRequest(BaseModel):
    device_id: str
    feature_names: List[str] # e.g., ["cpu", "ram", "packet_drops"]
    matrix: List[List[float]] # Rows = time ticks, Cols = features

class MultiVariateResponse(BaseModel):
    device_id: str
    overall_detected: bool
    anomalous_indices: List[int]
    scores: List[float]
    max_score: float

class GrangerRequest(BaseModel):
    source_device_id: str
    target_device_id: str
    series_x: List[float] # Candidate Cause (Leading metric)
    series_y: List[float] # Candidate Effect (Lagged metric)

class GrangerResponse(BaseModel):
    is_causal: bool
    p_value: float
    best_lag: int
    explanation: str

class BayesianCandidate(BaseModel):
    id: str
    device_type: str
    anomaly_score: float
    has_recent_config_change: bool
    granger_causal_p_value: float
    is_missing_evidence: bool

class BayesianRequest(BaseModel):
    incident_id: str
    candidates: List[BayesianCandidate]

@app.get("/health", response_model=HealthResponse)
def health_check():
    return HealthResponse(
        status="UP",
        service="ml-analytics-service",
        timestamp=datetime.now(timezone.utc).isoformat()
    )

@app.post("/analyze/multivariate", response_model=MultiVariateResponse)
def analyze_multivariate(req: MultiVariateRequest):
    result = iso_engine.analyze_multivariate_matrix(req.matrix)
    return MultiVariateResponse(
        device_id=req.device_id,
        overall_detected=result["overall_detected"],
        anomalous_indices=result["anomalous_indices"],
        scores=result["scores"],
        max_score=result["max_score"]
    )

@app.post("/analyze/granger", response_model=GrangerResponse)
def analyze_granger(req: GrangerRequest):
    result = granger_engine.test_causality(req.series_x, req.series_y)
    return GrangerResponse(
        is_causal=result["is_causal"],
        p_value=result["p_value"],
        best_lag=result["best_lag"],
        explanation=result["explanation"]
    )

@app.post("/analyze/bayesian")
def analyze_bayesian(req: BayesianRequest):
    candidates_dict = [c.dict() for c in req.candidates]
    ranked = bayesian_engine.rank_root_causes(candidates_dict)
    return {
        "incident_id": req.incident_id,
        "ranked_hypotheses": ranked,
        "primary_root_cause": ranked[0]["node_id"] if ranked else None
    }

@app.post("/generate/incident-report")
def generate_report(context: Dict[str, Any]):
    return llm_engine.generate_incident_report(context)

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("app.main:app", host="0.0.0.0", port=8000, reload=True)