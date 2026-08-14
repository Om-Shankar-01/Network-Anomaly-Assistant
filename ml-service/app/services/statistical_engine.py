import numpy as np
from typing import List, Dict, Any

class StatisticalAnomalyEngine:

    def __init__(self, z_threshold: float = 2.5):
        self.z_threshold = z_threshold

    def analyze_time_series(self, values: List[float]) -> Dict[str, Any]:
        """
        Calculates dynamic rolling Z-scores and classifies time-series anomaly signatures.
        """
        if not values or len(values) < 2:
            return {
                "mean": 0.0,
                "std": 1.0,
                "z_scores": [],
                "anomalies": [],
                "overall_detected": False,
                "max_score": 0.0
            }

        np_values = np.array(values, dtype=float)
        mean = float(np.mean(np_values))
        std = float(np.std(np_values))

        # Avoid division by zero for flat lines
        if std == 0.0:
            std = 1.0

        z_scores = (np_values - mean) / std
        anomalies = []
        overall_detected = False
        max_score = 0.0

        for idx, (val, z) in enumerate(zip(np_values, z_scores)):
            abs_z = abs(z)
            is_anomaly = bool(abs_z >= self.z_threshold)

            # Normalize anomaly score between 0.0 and 1.0
            score = min(1.0, float(abs_z / 4.0))

            if is_anomaly:
                overall_detected = True
                max_score = max(max_score, score)

            # Classify anomaly signature
            if z > self.z_threshold:
                anomaly_type = "SPIKE"
            elif z < -self.z_threshold:
                anomaly_type = "DROP"
            else:
                anomaly_type = "NORMAL"

            anomalies.append({
                "index": idx,
                "value": float(val),
                "z_score": float(z),
                "is_anomaly": is_anomaly,
                "anomaly_score": score,
                "anomaly_type": anomaly_type
            })

        return {
            "mean": mean,
            "std": std,
            "z_scores": [float(z) for z in z_scores],
            "anomalies": anomalies,
            "overall_detected": overall_detected,
            "max_score": float(max_score)
        }