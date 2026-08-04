import sys
import os

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), 'proto')))


import grpc
from concurrent import futures
import logging
from app.proto import anomaly_detection_pb2_grpc
from app.services.anomaly_service import AnomalyDetectionServicer

logging.basicConfig(level=logging.INFO)

def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    
    # Register the AnomalyDetectionServicer
    anomaly_detection_pb2_grpc.add_AnomalyDetectionServiceServicer_to_server(
        AnomalyDetectionServicer(), server
    )
    
    server.add_insecure_port('[::]:50051')
    logging.info("Starting Python gRPC Server on port 50051...")
    server.start()
    server.wait_for_termination()

if __name__ == '__main__':
    serve()