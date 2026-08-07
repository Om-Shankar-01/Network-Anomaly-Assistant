import numpy as np
import pandas as pd
from statsmodels.tsa.stattools import grangercausalitytests
from typing import List, Dict, Any

class GrangerCausalityEngine:

    def __init__(self, max_lag: int = 3, significance_level: float = 0.05):
        self.max_lag = max_lag
        self.significance_level = significance_level

    def test_causality(self, series_x: List[float], series_y: List[float]) -> Dict[str, Any]:
        """
        Tests if time-series X Granger-causes time-series Y.
        Input format for statsmodels: 2D array where column 0 = Y (dependent), column 1 = X (cause).
        """
        if not series_x or not series_y or len(series_x) != len(series_y) or len(series_x) < 10:
            return {
                "is_causal": False,
                "p_value": 1.0,
                "best_lag": 1,
                "explanation": "Insufficient data points for Granger Causality test (minimum 10 required)"
            }

        # Add small random noise to prevent singular matrix errors on constant flat lines
        x_clean = np.array(series_x, dtype=float) + np.random.normal(0, 1e-5, len(series_x))
        y_clean = np.array(series_y, dtype=float) + np.random.normal(0, 1e-5, len(series_y))

        # statsmodels expects 2D DataFrame: [Y, X]
        df = pd.DataFrame({"Y": y_clean, "X": x_clean})

        try:
            # Run Granger Causality test up to max_lag
            max_possible_lags = min(self.max_lag, (len(series_x) - 2) // 3)
            if max_possible_lags < 1:
                max_possible_lags = 1

            gc_results = grangercausalitytests(df[["Y", "X"]], maxlag=max_possible_lags, verbose=False)

            min_p_value = 1.0
            best_lag = 1

            for lag, result in gc_results.items():
                # Extract SSR F-test p-value
                p_val = result[0]['ssr_ftest'][1]
                if p_val < min_p_value:
                    min_p_value = p_val
                    best_lag = lag

            is_causal = min_p_value < self.significance_level

            return {
                "is_causal": is_causal,
                "p_value": float(min_p_value),
                "best_lag": int(best_lag),
                "explanation": f"X -> Y causality {'SUPPORTED' if is_causal else 'REJECTED'} (p-value: {min_p_value:.4f} at lag {best_lag})"
            }

        except Exception as e:
            return {
                "is_causal": False,
                "p_value": 1.0,
                "best_lag": 1,
                "explanation": f"Granger test calculation error: {str(e)}"
            }