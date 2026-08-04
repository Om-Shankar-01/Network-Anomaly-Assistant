import uuid
import time
import random
import json

from datetime import datetime, timezone
from kafka import KafkaProducer

KAFKA_SERVER = 'localhost:9092'

# Initialize Kafka Server
try:
    producer = KafkaProducer(
        bootstrap_servers=[KAFKA_SERVER],
        value_serializer=lambda v: json.dumps(v).encode('utf-8')
    )
    print(f"Connected to Kafka broker at {KAFKA_SERVER}")
except Exception as e:
    print(f"Error connecting to Kafka: {e}")
    producer = None

def generate_event (event_type, source_device, source_interface, severity, payload):
    return {
        "eventId" : str(uuid.uuid4()),
        "timestampUtc" : datetime.now(timezone.utc).isoformat(),
        "sourceDeviceId": source_device,
        "sourceInterfaceId" :source_interface,
        "eventType" : event_type,
        "severity" : severity,
        "payload" : payload
    }

def publish (topic, event):
    if producer:
        producer.send(topic, value=event)
        producer.flush()
    print(f"[{event['timestampUtc']}] Published to '{topic}': {event['eventType']} | {event['sourceDeviceId']} | {event['severity']}")


# ---------------------------------------------------------
# CASCADE 1: Physical Fiber Cut & BGP Session Loss
# ---------------------------------------------------------
def simulate_fiber_cut_cascade():
    print("\n⚡ Starting Cascade 1: Fiber Cut on router-core-01 (GigE0/1)...")

    # 1. Interface Link Down Alert
    alert = generate_event("ALERT", "router-core-01", "GigE0/1", "CRITICAL", {
        "alertName": "LINK_DOWN",
        "message": "Interface GigabitEthernet0/1 changed state to DOWN"
    })
    publish("ingest.alerts", alert)
    time.sleep(1)

    # 2. BGP Neighbor Session Lost Log
    log_event = generate_event("LOG", "router-core-01", "GigE0/1", "CRITICAL", {
        "logMessage": "%BGP-5-ADJCHANGE: neighbor 10.0.0.2 Down - Interface down"
    })
    publish("ingest.logs", log_event)
    time.sleep(1)

    # 3. CPU Spike Telemetry Metric
    metric = generate_event("METRIC", "router-core-01", "CPU", "WARNING", {
        "metricName": "cpu_usage",
        "value": 96.8
    })

    publish("telemetry.metrics", metric)
    print("✓ Cascade 1 complete.\n")


# ---------------------------------------------------------
# CASCADE 2: Memory Leak & Packet Drops
# ---------------------------------------------------------
def simulate_memory_leak_cascade():
    print("\n⚡ Starting Cascade 2: Memory Leak on switch-dist-01...")
    
    for ram in [65.0, 78.5, 89.2, 98.4]:
        metric = generate_event("METRIC", "switch-dist-01", "RAM", "WARNING" if ram < 90 else "CRITICAL", {
            "metricName": "memory_usage",
            "value": ram
        })
        publish("telemetry.metrics", metric)
        time.sleep(1)

    alert = generate_event("ALERT", "switch-dist-01", "RAM", "CRITICAL", {
        "alertName": "OUT_OF_MEMORY",
        "message": "System memory usage exceeded 98% threshold"
    })
    publish("ingest.alerts", alert)
    print("✓ Cascade 2 complete.\n")



# ---------------------------------------------------------
# CASCADE 3: Config Drift / Misconfigured ACL
# ---------------------------------------------------------
def simulate_config_drift_cascade():
    print("\n⚡ Starting Cascade 3: Config Drift on firewall-edge-01...")
    
    config_evt = generate_event("LOG", "firewall-edge-01", "MGMT", "INFO", {
        "logMessage": "%SYS-5-CONFIG_I: Configured from console by admin (ACL 101 updated)"
    })
    publish("ingest.config-changes", config_evt)
    time.sleep(1)

    alert = generate_event("ALERT", "firewall-edge-01", "eth0", "WARNING", {
        "alertName": "PACKET_DROP_SPIKE",
        "message": "Ingress packet drop rate spiked to 85%"
    })
    publish("ingest.alerts", alert)
    print("✓ Cascade 3 complete.\n")


if __name__ == '__main__':
    print("=== Network Anomaly Synthetic Incident Simulator ===")
    simulate_fiber_cut_cascade()
    time.sleep(2)
    simulate_memory_leak_cascade()
    time.sleep(2)
    simulate_config_drift_cascade()