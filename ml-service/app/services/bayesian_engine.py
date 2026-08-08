from typing import List, Dict, Any

class BayesianCausalEngine:

    BASE_PRIORS = {
        "ROUTER": 0.05,
        "SWITCH": 0.03,
        "FIREWALL": 0.04,
        "SERVER": 0.02
    }

    def rank_root_causes(self, candidate_nodes: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
        """
        Calculates conditional posterior probabilities for candidate root cause nodes.

        Each candidate_node dict contains:
          - id: str
          - device_type: str (ROUTER, SWITCH, FIREWALL, SERVER)
          - anomaly_score: float [0.0 - 1.0]
          - has_recent_config_change: bool
          - granger_causal_p_value: float [0.0 - 1.0]
          - is_missing_evidence: bool
        """
        if not candidate_nodes:
            return []

        unnormalized_posteriors = []

        for node in candidate_nodes:
            device_type = node.get("device_type", "SWITCH").upper()

            # 1. Prior Probability P(N_i)
            prior = self.BASE_PRIORS.get(device_type, 0.03)
            if node.get("has_recent_config_change", False):
                prior += 0.40 # Major prior boost if config change occurred!

            # 2. Likelihood P(E | N_i)
            anomaly_score = float(node.get("anomaly_score", 0.0))
            granger_p = float(node.get("granger_causal_p_value", 1.0))
            is_missing = bool(node.get("is_missing_evidence", False))

            # Higher anomaly score & lower Granger p-value increase likelihood
            causal_weight = (1.0 - granger_p) if granger_p < 0.05 else 0.1
            missing_boost = 0.3 if is_missing else 0.0

            likelihood = (anomaly_score * 0.5) + (causal_weight * 0.3) + (missing_boost * 0.2) + 0.01

            # Unnormalized Posterior = Likelihood * Prior
            unnormalized_posterior = likelihood * prior
            unnormalized_posteriors.append((node, unnormalized_posterior))

        # 3. Normalize probabilities across all candidates (sum to 1.0)
        total_sum = sum(p for _, p in unnormalized_posteriors)
        if total_sum == 0:
            total_sum = 1.0

        ranked_hypotheses = []
        for node, unnorm_p in unnormalized_posteriors:
            confidence_score = float(unnorm_p / total_sum)

            # Assign confidence rating
            if confidence_score >= 0.60:
                confidence_rating = "HIGH"
            elif confidence_score >= 0.30:
                confidence_rating = "MEDIUM"
            else:
                confidence_rating = "LOW"

            ranked_hypotheses.append({
                "node_id": node.get("id"),
                "device_type": node.get("device_type"),
                "confidence_score": round(confidence_score, 4),
                "confidence_rating": confidence_rating,
                "evidence_summary": {
                    "anomaly_score": node.get("anomaly_score", 0.0),
                    "recent_config_change": node.get("has_recent_config_change", False),
                    "granger_causal_support": node.get("granger_causal_p_value", 1.0) < 0.05,
                    "is_missing_evidence": node.get("is_missing_evidence", False)
                }
            })

        # Sort by confidence score descending
        ranked_hypotheses.sort(key=lambda x: x["confidence_score"], reverse=True)
        return ranked_hypotheses