from fastapi.testclient import TestClient
from app.main import app

# Create a test client to simulate HTTP requests against our FastAPI app
client = TestClient(app)

def test_health_check():
    response = client.get("/health")

    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "UP"
    assert data["service"] == "ml-analytics-service"
    assert "timestamp" in data

def test_analyze_bayesian_endpoint():
    # Simulate the JSON payload that Spring Boot would send
    payload = {
        "incident_id": "INC-TEST-123",
        "candidates": [
            {
                "id": "switch-01",
                "device_type": "SWITCH",
                "anomaly_score": 0.95,
                "has_recent_config_change": True,
                "granger_causal_p_value": 0.02,
                "is_missing_evidence": False
            },
            {
                "id": "router-01",
                "device_type": "ROUTER",
                "anomaly_score": 0.5,
                "has_recent_config_change": False,
                "granger_causal_p_value": 0.8,
                "is_missing_evidence": False
            }
        ]
    }

    # POST the payload to the endpoint
    response = client.post("/analyze/bayesian", json=payload)

    assert response.status_code == 200
    data = response.json()

    assert data["incident_id"] == "INC-TEST-123"
    assert data["primary_root_cause"] == "switch-01"

    ranked = data["ranked_hypotheses"]
    assert len(ranked) == 2

    # Verify the Bayesian engine correctly ranked the switch first due to config change + high anomaly score
    assert ranked[0]["node_id"] == "switch-01"
    assert ranked[0]["confidence_rating"] in ["HIGH", "MEDIUM"]
    assert ranked[1]["node_id"] == "router-01"