import sys
import os

# Fix Python path for gRPC stubs
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '../proto')))

from app.proto import anomaly_detection_pb2, anomaly_detection_pb2_grpc
from app.services.statistical_engine import StatisticalAnomalyEngine

class AnomalyDetectionServicer(anomaly_detection_pb2_grpc.AnomalyDetectionServiceServicer):

    def __init__(self):
        self.stat_engine = StatisticalAnomalyEngine(z_threshold=2.5)

    def DetectAnomalies(self, request, context):
        device_id = request.device_id
        data_points = request.data_points

        if not data_points:
            return anomaly_detection_pb2.AnomalyDetectionResponse(
                device_id=device_id,
                anomalies=[],
                overall_anomaly_detected=False,
                max_anomaly_score=0.0
            )

        # Extract numerical values and analyze via Statistical Engine
        values = [dp.value for dp in data_points]
        analysis_result = self.stat_engine.analyze_time_series(values)

        anomalies_pb = []
        for dp, item in zip(data_points, analysis_result["anomalies"]):
            anomalies_pb.append(anomaly_detection_pb2.AnomalyResult(
                timestamp_ms=dp.timestamp_ms,
                metric_name=dp.metric_name,
                value=dp.value,
                z_score=item["z_score"],
                is_anomaly=item["is_anomaly"],
                anomaly_score=item["anomaly_score"],
                anomaly_type=item["anomaly_type"]
            ))

        return anomaly_detection_pb2.AnomalyDetectionResponse(
            device_id=device_id,
            anomalies=anomalies_pb,
            overall_anomaly_detected=analysis_result["overall_detected"],
            max_anomaly_score=analysis_result["max_score"]
        )