import numpy as np
from sklearn.ensemble import IsolationForest
from sklearn.preprocessing import StandardScaler
from typing import List, Dict, Any


class IsolationForestEngine:

    def __init__ (self, contamination: float=0.1, random_state:int = 42):
        self.contamination = contamination
        self.random_state = random_state

    def analyze_multivariate_matrix(self, feature_matrix: List[List[float]]) -> Dict[str, Any]:
        """"
        Analyzes N-dimensional telemetry feature vectors (e.g., [CPU, RAM, Bandwidth, DropRate]).
        Returns anomaly flags and normalized anomaly probability scores.
        """
        if not feature_matrix or len(feature_matrix) < 3:
            return {
                "overall_detected": False,
                "anomalous_indices": [],
                "scores": []
            }

        X = np.array(feature_matrix, dtype=float)


        # 1. Standardize features (Mean=0, Std=1)
        scaler = StandardScaler()
        X_scaled = scaler.fit_transform(X)

        # 2. Fit Isolation Forest
        model = IsolationForest(
            contamination=self.contamination,
            random_state=self.random_state,
            n_estimators=100
        )
        model.fit(X_scaled)

        # Predict: -1 for anomalies, 1 for inliers
        predictions = model.predict(X_scaled)

        # Raw decision scores (lower/more negative = more anomalous)
        raw_scores = model.decision_function(X_scaled)

        # Convert decision scores into normalized anomaly probabilities [0.0 - 1.0]
        # Invert scores so high value = high anomaly probability
        min_score = np.min(raw_scores)
        max_score = np.max(raw_scores)

        if max_score == min_score:
            normalized_scores = [0.0] * len(predictions)
        else:
            normalized_scores = [
                float(1.0 - (score - min_score) / (max_score - min_score + 1e-6))
                for score in raw_scores
            ]
        anomalous_indices = [idx for idx, pred in enumerate(predictions) if pred == -1]
        overall_detected = len(anomalous_indices) > 0
        return {
            "overall_detected": overall_detected,
            "anomalous_indices": anomalous_indices,
            "predictions": [int(p) for p in predictions],
            "scores": normalized_scores,
            "max_score": float(np.max(normalized_scores)) if normalized_scores else 0.0
        }

