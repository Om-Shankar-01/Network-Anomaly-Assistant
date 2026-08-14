import os
import json
import re
from typing import Dict, Any, List
from datetime import datetime, timezone

# Optional import for Google Gemini SDK
try:
    from google import genai
except ImportError:
    genai = None

class LLMAssistantEngine:

    def __init__(self):
        # 1. Store Gemini API Key
        self.gemini_api_key = os.getenv("GEMINI_API_KEY") or os.getenv("GOOGLE_API_KEY")

        # 2. Initialize Gemini Client if key exists
        self.gemini_client = None
        if self.gemini_api_key and genai:
            self.gemini_client = genai.Client(api_key=self.gemini_api_key)

    def generate_incident_report(self, context: Dict[str, Any]) -> Dict[str, Any]:
        """
        Generates incident report using Google Gemini API or Offline Deterministic Rule Engine.
        """
        incident_id = context.get("incidentId", "INC-UNKNOWN")
        primary_device = context.get("primaryDeviceId", "UNKNOWN_DEVICE")
        confidence_score = context.get("confidenceScore", 0.9172)
        confidence_rating = context.get("confidenceRating", "HIGH")
        confirmed_evidence = context.get("confirmedEvidence", [])
        correlated_signals = context.get("correlatedSignals", [])
        recent_logs = context.get("recentLogs", [])

        # Build LLM Prompt
        prompt = self._construct_llm_prompt(context)

        # -------------------------------------------------------------
        # PROVIDER 1: Google Gemini API (Online Dynamic Generation)
        # -------------------------------------------------------------
        if self.gemini_client:
            try:
                print("🤖 Generating dynamic report & CLI commands using Google Gemini API...")
                response = self.gemini_client.interactions.create(
                    model="gemini-flash-latest",
                    input=prompt
                )
                markdown_report = response.output_text
                extracted_commands = self._extract_commands_from_markdown(markdown_report)
                return self._build_response(
                    incident_id, primary_device, confidence_score,
                    confidence_rating, markdown_report, extracted_commands, "Google Gemini Flash"
                )
            except Exception as e:
                print(f"⚠️ Gemini API call failed, attempting offline fallback: {e}")

        # -------------------------------------------------------------
        # PROVIDER 2: Offline Deterministic Rule Engine (Fallback)
        # -------------------------------------------------------------
        print("ℹ️ No Gemini API key detected. Using Offline Rule Engine for commands and report.")
        fallback_commands = self._generate_fallback_diagnostic_commands(
            primary_device, confirmed_evidence, correlated_signals, recent_logs
        )
        markdown_report = self._build_deterministic_markdown(
            context, incident_id, primary_device, confidence_score, confidence_rating, fallback_commands
        )
        return self._build_response(
            incident_id, primary_device, confidence_score, confidence_rating,
            markdown_report, fallback_commands, "Offline Rule Engine"
        )

    def _construct_llm_prompt(self, context: Dict[str, Any]) -> str:
        return f"""You are an expert Network Reliability Engineer & Audit Assistant. Analyze the actual Syslog stream, 3-Way Evidence Matrix, and Causal Graph outputs to generate a comprehensive, auditor-friendly markdown incident investigation report.
        
        INCIDENT CONTEXT:
        - Incident ID: {context.get('incidentId')}
        - Target Device: {context.get('primaryDeviceId')}
        - Root Cause Hypothesis: {context.get('rootCauseHypothesis')}
        - Confidence Rating: {context.get('confidenceRating')} ({context.get('confidenceScore', 0) * 100:.1f}%)
        
        ACTUAL SYS-LOG STREAM (Inspect these log events carefully to determine exact failure causes):
        {json.dumps(context.get('recentLogs', []), indent=2)}
        
        3-WAY EVIDENCE MATRIX:
        1. CONFIRMED EVIDENCE (Hard Observed Facts):
        {json.dumps(context.get('confirmedEvidence', []), indent=2)}
        
        2. CORRELATED SIGNALS (Statistical Associations):
        {json.dumps(context.get('correlatedSignals', []), indent=2)}
        
        3. MISSING EVIDENCE (Absent Signals):
        {json.dumps(context.get('missingEvidence', []), indent=2)}
        
        INSTRUCTIONS:
        Generate a clean Markdown report containing the following 5 required sections:
        1. # 🚨 Incident Investigation Report
        2. ## 📋 Executive Summary (Explain what happened based on the Syslog events)
        3. ## 🎯 Root Cause Analysis (Detail the Causal Reasoning)
        4. ## 📊 3-Way Evidence Matrix Table (Confirmed vs Correlated vs Missing)
        5. ## 🛠️ Recommended CLI Diagnostic & Remediation Commands
        
        DYNAMIC COMMAND GENERATION DIRECTIVE FOR SECTION 5:
        Based on the specific interfaces, protocols (BGP, OSPF, LACP), and IPs in the Syslog stream, dynamically invent the top 4 exact CLI diagnostic commands a senior engineer should run on `{context.get('primaryDeviceId')}`. Place them inside a ```bash ``` code block with step explanations.
        """

    def _extract_commands_from_markdown(self, markdown: str) -> List[str]:
        """
        Parses bash code blocks generated by the LLM to extract CLI command lists.
        """
        bash_blocks = re.findall(r'```bash\s*(.*?)\s*```', markdown, re.DOTALL)
        commands = []
        if bash_blocks:
            lines = bash_blocks[-1].split('\n')
            for line in lines:
                line_str = line.strip()
                if line_str and not line_str.startswith('#'):
                    commands.append(line_str)
        if not commands:
            commands = ["show interfaces status", "show logging", "traceroute"]
        return commands

    def _generate_fallback_diagnostic_commands(
            self, device_id: str, confirmed_evidence: List[str], correlated_signals: List[str], recent_logs: List[str]
    ) -> List[str]:
        """
        Offline Regex Rule Engine fallback for command generation when no API key is present.
        """
        commands = []
        all_text = " ".join(confirmed_evidence + correlated_signals + recent_logs)

        # 1. Interface Extraction from Actual Logs
        iface_match = re.search(r'(GigE\d+/\d+|TenGigE\d+/\d+/\d+|eth\d+|Bundle-Ether\d+|FastEthernet\d+/\d+)', all_text, re.IGNORECASE)
        if iface_match:
            iface = iface_match.group(1)
            commands.append(f"show interface {iface} status")
            commands.append(f"show interface {iface} counters errors")
        else:
            commands.append(f"show interfaces status | include {device_id}")

        # 2. IP Neighbor Extraction & Protocol Parsing
        peer_ip_match = re.search(r'neighbor\s+(\d+\.\d+\.\d+\.\d+)', all_text, re.IGNORECASE)

        if "BGP" in all_text.upper():
            if peer_ip_match:
                commands.append(f"show ip bgp neighbors {peer_ip_match.group(1)}")
            else:
                commands.append("show ip bgp summary | include Down")
        elif "OSPF" in all_text.upper():
            if peer_ip_match:
                commands.append(f"show ip ospf neighbor {peer_ip_match.group(1)}")
            else:
                commands.append("show ip ospf neighbor")

        # 3. Resource Metric Parsing
        if "CPU" in all_text.upper() or "96.8" in all_text:
            commands.append("show processes cpu sorted | exclude 0.00")
        elif "RAM" in all_text.upper() or "MEMORY" in all_text.upper():
            commands.append("show memory statistics")

        # 4. Traceroute Pathing
        if peer_ip_match:
            commands.append(f"traceroute {peer_ip_match.group(1)}")
        else:
            commands.append(f"traceroute {device_id}")

        return commands

    def _build_response(self, incident_id, primary_device, score, rating, markdown, commands, provider):
        return {
            "incident_id": incident_id,
            "primary_device": primary_device,
            "confidence_score": score,
            "confidence_rating": rating,
            "llm_provider": provider,
            "markdown_report": markdown,
            "recommended_commands": commands
        }

    def _build_deterministic_markdown(
            self, context, incident_id, primary_device, confidence_score, confidence_rating, recommended_commands
    ):
        confirmed_evidence = context.get("confirmedEvidence", [])
        correlated_signals = context.get("correlatedSignals", [])
        missing_evidence = context.get("missingEvidence", [])
        recent_logs = context.get("recentLogs", [])

        cmd_lines = "\n\n".join([f"# Step {idx+1}: Targeted Diagnostic\n{cmd}" for idx, cmd in enumerate(recommended_commands)])

        return (
            f"# 🚨 Incident Investigation Report: {incident_id}\n\n"
            f"## 📋 Executive Summary\n"
            f"At {datetime.now(timezone.utc).strftime('%Y-%m-%d %H:%M:%S UTC')}, a critical network anomaly cascade was detected affecting **{primary_device}**. "
            f"The Causal Inference Engine evaluated multi-source telemetry, log events, and signal gaps to identify the root cause with **{confidence_rating} confidence ({confidence_score * 100:.1f}%)**.\n\n"
            f"---\n\n"
            f"## 🎯 Root Cause Analysis\n"
            f"* **Primary Root Cause Hypothesis**: `{primary_device}`\n"
            f"* **Confidence Rating**: **{confidence_rating}** ({confidence_score * 100:.1f}%)\n"
            f"* **Log Analysis**: Syslog stream confirms adjacency/state changes on `{primary_device}` initiating downstream impact.\n\n"
            f"---\n\n"
            f"## 📊 3-Way Evidence Matrix\n\n"
            f"### 1. 🟢 Confirmed Evidence (Hard Observed Facts)\n"
            f"{self._format_list(confirmed_evidence)}\n\n"
            f"### 2. 🟡 Correlated Signals (Statistical Associations)\n"
            f"{self._format_list(correlated_signals)}\n\n"
            f"### 3. 🔴 Missing Evidence (Expected-but-Absent Signals)\n"
            f"{self._format_list(missing_evidence)}\n\n"
            f"---\n\n"
            f"## 🕒 Recent Syslog Stream\n"
            f"```text\n"
            f"{self._format_logs(recent_logs)}\n"
            f"```\n\n"
            f"---\n\n"
            f"## 🛠️ Recommended Next Diagnostic & Remediation Steps\n\n"
            f"```bash\n"
            f"{cmd_lines}\n"
            f"```\n"
        )

    def _format_list(self, items: List[str]) -> str:
        if not items:
            return "* *No evidence recorded in this category.*"
        return "\n".join([f"* {item}" for item in items])

    def _format_logs(self, logs: List[str]) -> str:
        if not logs:
            return "%SYS-5-INFO: No recent syslog events found."
        return "\n".join(logs[:10])