from fastapi import FastAPI
from pydantic import BaseModel
from typing import List, Dict, Any
from datetime import datetime, timezone
from app.services.isolation_forest_engine import IsolationForestEngine
from app.services.granger_causality_engine import GrangerCausalityEngine


app = FastAPI(
    title="Network Assistant ML Microservice",
    version="1.0.0",
    description="Python service for anomaly detection, Isolation Forests, and Bayesian Belief Networks."
)

iso_engine = IsolationForestEngine(contamination=0.15)
granger_engine = GrangerCausalityEngine(max_lag=3)

class HealthResponse (BaseModel) :
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

if __name__ == "__main__" :
    import uvicorn
    uvicorn.run("app.main:app", host="0.0.0.0", port=8000, reload=True)