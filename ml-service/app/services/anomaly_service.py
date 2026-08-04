import sys
import os

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '../proto')))


import numpy as np
from app.proto import anomaly_detection_pb2, anomaly_detection_pb2_grpc

# gRPC servicer implementing a simple statistical anomaly detector.
# Uses Z-score (based on mean/std) to mark spikes as anomalies and
# returns protobuf messages describing per-point and overall results.


class AnomalyDetectionServicer(anomaly_detection_pb2_grpc.AnomalyDetectionServiceServicer):

    def DetectAnomalies(self, request, context):
        # Extract inputs from the incoming gRPC request
        device_id = request.device_id
        data_points = request.data_points

        # Initialize result containers and flags
        anomalies = []
        overall_detected = False
        max_score = 0.0

        # Early return when there are no datapoints to analyze
        if not data_points:
            return anomaly_detection_pb2.AnomalyDetectionResponse(
                device_id=device_id,
                anomalies=[],
                overall_anomaly_detected=False,
                max_anomaly_score=0.0
            )

        # Convert datapoint values to a NumPy array and compute
        # mean and standard deviation. If std is zero, fall back to 1.0
        # to avoid division-by-zero when computing Z-scores.
        values = np.array([dp.value for dp in data_points])
        mean = np.mean(values)
        std = np.std(values) if np.std(values) > 0 else 1.0

        # Iterate each datapoint, compute its Z-score and normalized
        # anomaly score, set anomaly flags, and append protobuf results.
        for dp in data_points:
            z_score = abs((dp.value - mean) / std)
            is_anomaly = z_score > 2.5  # heuristic threshold for spikes
            score = min(1.0, z_score / 5.0)  # normalize to [0.0, 1.0]

            if is_anomaly:
                overall_detected = True
                max_score = max(max_score, score)

            anomalies.append(anomaly_detection_pb2.AnomalyResult(
                timestamp_ms=dp.timestamp_ms,
                metric_name=dp.metric_name,
                value=dp.value,
                z_score=float(z_score),
                is_anomaly=is_anomaly,
                anomaly_score=float(score),
                anomaly_type="STATISTICAL_SPIKE" if is_anomaly else "NORMAL"
            ))

        # Construct and return the response protobuf with per-point
        # results and overall anomaly summary.
        return anomaly_detection_pb2.AnomalyDetectionResponse(
            device_id=device_id,
            anomalies=anomalies,
            overall_anomaly_detected=overall_detected,
            max_anomaly_score=float(max_score)
        )
    