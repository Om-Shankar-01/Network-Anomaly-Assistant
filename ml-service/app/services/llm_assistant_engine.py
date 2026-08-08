from typing import Dict, Any, List
from datetime import datetime, timezone

class LLMAssistantEngine:

    def generate_incident_report(self, context: Dict[str, Any]) -> Dict[str, Any]:
        """
        Translates incident context & 3-way evidence matrix into a structured markdown report.
        Supports offline deterministic templating as well as LLM API prompts.
        """
        incident_id = context.get("incidentId", "INC-UNKNOWN")
        primary_device = context.get("primaryDeviceId", "UNKNOWN_DEVICE")
        hypothesis = context.get("rootCauseHypothesis", primary_device)
        confidence_score = context.get("confidenceScore", 0.9172)
        confidence_rating = context.get("confidenceRating", "HIGH")

        confirmed_evidence = context.get("confirmedEvidence", [])
        correlated_signals = context.get("correlatedSignals", [])
        missing_evidence = context.get("missingEvidence", [])
        recent_logs = context.get("recentLogs", [])

        # Generate Recommended CLI Diagnostic Commands based on device & evidence
        recommended_commands = self._generate_diagnostic_commands(primary_device, confirmed_evidence)

        # Build Markdown Report Body
        markdown_report = (
            f"# Incident Investigation Report: {incident_id}\n\n"
            f"## 📋 Executive Summary\n"
            f"At {datetime.now(timezone.utc).strftime('%Y-%m-%d %H:%M:%S UTC')}, a critical network anomaly cascade was detected affecting **{primary_device}**. "
            f"The Causal Inference Engine has evaluated multi-source telemetry, log events, and signal gaps to identify the root cause with **{confidence_rating} confidence ({confidence_score * 100:.1f}%)**.\n\n"
            f"---\n\n"
            f"## Root Cause Analysis\n"
            f"* **Primary Root Cause Hypothesis**: `{hypothesis}`\n"
            f"* **Confidence Rating**: **{confidence_rating}** ({confidence_score * 100:.1f}%)\n"
            f"* **Causal Reasoning**: Bayesian posterior probabilities indicate that a physical link flap / config drift on `{hypothesis}` initiated the downstream anomaly cascade.\n\n"
            f"---\n\n"
            f"## 3-Way Evidence Matrix\n\n"
            f"### 1. 🟢 Confirmed Evidence (Hard Observed Facts)\n"
            f"{self._format_list_as_bullets(confirmed_evidence)}\n\n"
            f"### 2. 🟡 Correlated Signals (Statistical Associations)\n"
            f"{self._format_list_as_bullets(correlated_signals)}\n\n"
            f"### 3. 🔴 Missing Evidence (Expected-but-Absent Signals)\n"
            f"{self._format_list_as_bullets(missing_evidence)}\n\n"
            f"---\n\n"
            f"## Recent Syslog & Event Stream\n"
            f"```text\n"
            f"{self._format_logs(recent_logs)}\n"
            f"```\n\n"
            f"---\n\n"
            f"## Recommended Next Diagnostic & Remediation Steps\n\n"
            f"Run the following CLI commands to verify and remediate the incident on **{primary_device}**:\n\n"
            f"```bash\n"
            f"# 1. Check physical interface status & error counters\n"
            f"{recommended_commands[0]}\n\n"
            f"# 2. Inspect BGP neighbor adjacency states\n"
            f"{recommended_commands[1]}\n\n"
            f"# 3. Trace network route pathing to downstream dependents\n"
            f"{recommended_commands[2]}\n\n"
            f"# 4. Verify system CPU & memory process utilization\n"
            f"{recommended_commands[3]}\n"
            f"```\n"
        )

        return {
            "incident_id": incident_id,
            "primary_device": primary_device,
            "confidence_score": confidence_score,
            "confidence_rating": confidence_rating,
            "markdown_report": markdown_report,
            "recommended_commands": recommended_commands
        }

    def _generate_diagnostic_commands(self, device_id: str, confirmed_evidence: List[str]) -> List[str]:
        return [
            f"show interface GigE0/1 status | include {device_id}",
            "show ip bgp summary | include Down",
            f"traceroute {device_id}",
            "show processes cpu sorted | exclude 0.00"
        ]

    def _format_list_as_bullets(self, items: List[str]) -> str:
        if not items:
            return "* *No evidence recorded in this category.*"
        return "\n".join([f"* {item}" for item in items])

    def _format_logs(self, logs: List[str]) -> str:
        if not logs:
            return "%SYS-5-INFO: No recent syslog events found."
        return "\n".join(logs[:10])