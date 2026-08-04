from fastapi import FastAPI
from pydantic import BaseModel
from datetime import datetime, timezone

app = FastAPI(
    title="Network Assistant ML Microservice",
    version="1.0.0",
    description="Python service for anomaly detection, Granger Causality, and Bayesian Belief Networks."
)

class HealthResponse (BaseModel) :
    status: str
    service: str
    timestamp: str

@app.get("/health", response_model=HealthResponse)
def health_check():
    return HealthResponse(
        status="UP",
        service="ml-analytics-service",
        timestamp=datetime.now(timezone.utc).isoformat()
    )

if __name__ == "__main__" :
    import uvicorn
    uvicorn.run("app.main:app", host="0.0.0.0", port=8000, reload=True)