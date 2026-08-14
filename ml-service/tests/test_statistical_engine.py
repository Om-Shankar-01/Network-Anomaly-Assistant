import pytest
from app.services.statistical_engine import StatisticalAnomalyEngine

def test_statistical_engine_empty_input():
    engine = StatisticalAnomalyEngine()
    result = engine.analyze_time_series([])

    assert result["overall_detected"] is False
    assert result["max_score"] == 0.0
    assert len(result["anomalies"]) == 0

def test_statistical_engine_no_anomalies():
    engine = StatisticalAnomalyEngine(z_threshold=2.5)
    # A perfectly stable network link (e.g. 50% CPU)
    values = [50.0, 51.0, 49.0, 50.5, 49.5, 50.0]

    result = engine.analyze_time_series(values)

    assert result["overall_detected"] is False
    assert result["max_score"] < 1.0

    # Every element should be NORMAL
    for item in result["anomalies"]:
        assert item["is_anomaly"] is False
        assert item["anomaly_type"] == "NORMAL"

def test_statistical_engine_spike_detected():
    engine = StatisticalAnomalyEngine(z_threshold=2.0)
    # A stable CPU that suddenly spikes to 99%
    values = [20.0, 21.0, 19.0, 20.5, 19.5, 99.0]

    result = engine.analyze_time_series(values)

    assert result["overall_detected"] is True
    assert result["max_score"] > 0.0

    # The last element should be flagged as a SPIKE
    last_item = result["anomalies"][-1]
    assert last_item["is_anomaly"] is True
    assert last_item["anomaly_type"] == "SPIKE"

def test_statistical_engine_drop_detected():
    engine = StatisticalAnomalyEngine(z_threshold=2.0)
    # A stable link bandwidth that suddenly drops to 0 (link failure)
    values = [1000.0, 990.0, 1010.0, 1000.0, 995.0, 0.0]

    result = engine.analyze_time_series(values)

    assert result["overall_detected"] is True

    # The last element should be flagged as a DROP
    last_item = result["anomalies"][-1]
    assert last_item["is_anomaly"] is True
    assert last_item["anomaly_type"] == "DROP"

def test_statistical_engine_flatline_division_by_zero_prevention():
    engine = StatisticalAnomalyEngine()
    # A flatline where standard deviation = 0
    values = [50.0, 50.0, 50.0, 50.0]

    result = engine.analyze_time_series(values)

    # Our code overrides std=0 to std=1.0 to prevent division by zero exceptions
    assert result["std"] == 1.0
    assert result["overall_detected"] is False