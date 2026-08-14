import pytest
from app.services.isolation_forest_engine import IsolationForestEngine
from app.services.bayesian_engine import BayesianCausalEngine

def test_isolation_forest_engine_empty_input():
    engine = IsolationForestEngine()
    result = engine.analyze_multivariate_matrix([])
    assert result["overall_detected"] is False
    assert len(result["anomalous_indices"]) == 0

def test_isolation_forest_engine_anomaly_detection():
    # Contamination=0.2 means we expect ~20% of the dataset to be anomalies
    engine = IsolationForestEngine(contamination=0.2, random_state=42)

    # Format: [CPU, RAM, PacketDropRate]
    # 4 normal data points, 1 massive outlier
    feature_matrix = [
        [20.0, 4.0, 0.01],
        [21.0, 4.1, 0.02],
        [19.0, 3.9, 0.01],
        [20.5, 4.0, 0.01],
        [99.0, 16.0, 50.5] # Massive Outlier at index 4
    ]

    result = engine.analyze_multivariate_matrix(feature_matrix)

    assert result["overall_detected"] is True
    # The outlier is the last element (index 4)
    assert 4 in result["anomalous_indices"]
    # Verify probabilities are mathematically normalized between 0 and 1
    assert result["max_score"] > 0.8

def test_bayesian_engine_prioritizes_config_changes():
    engine = BayesianCausalEngine()

    candidates = [
        {
            "id": "router-core",
            "device_type": "ROUTER",
            "anomaly_score": 0.9,
            "has_recent_config_change": False,
            "granger_causal_p_value": 0.01,
            "is_missing_evidence": False
        },
        {
            "id": "switch-edge",
            "device_type": "SWITCH",
            "anomaly_score": 0.6,
            # This config change should vastly boost the switch's Prior Probability
            "has_recent_config_change": True,
            "granger_causal_p_value": 0.03,
            "is_missing_evidence": False
        }
    ]

    ranked = engine.rank_root_causes(candidates)

    # switch-edge should be rank 1 due to the config change boost, despite router-core having a higher anomaly score
    assert ranked[0]["node_id"] == "switch-edge"
    assert ranked[0]["confidence_rating"] in ["HIGH", "MEDIUM"]
    assert ranked[1]["node_id"] == "router-core"

def test_bayesian_engine_missing_evidence_boost():
    engine = BayesianCausalEngine()

    candidates = [
        {
            "id": "firewall-01",
            "device_type": "FIREWALL",
            "anomaly_score": 0.0,
            "has_recent_config_change": False,
            "granger_causal_p_value": 1.0,
            "is_missing_evidence": True # Silent drop heart-beat timeout
        }
    ]

    ranked = engine.rank_root_causes(candidates)

    assert len(ranked) == 1
    assert ranked[0]["confidence_score"] == 1.0 # Only one candidate, normalized to 100%
    assert ranked[0]["evidence_summary"]["is_missing_evidence"] is True