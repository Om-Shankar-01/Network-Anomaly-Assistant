import { useState, useEffect, useRef } from 'react';
import { Activity, ShieldAlert, WifiOff, AlertTriangle } from 'lucide-react';
import EvidenceMatrix from '../components/EvidenceMatrix';
import CausalTopology from '../components/CausalTopology';
import BayesianLeaderboard from '../components/BayesianLeaderboard';
import CopilotReport from '../components/CopilotReport';
import api from '../services/api';
import './Dashboard.css';

export default function Dashboard() {
  const [incidents, setIncidents] = useState([]);
  const [selectedIncident, setSelectedIncident] = useState(null);
  const [missingEvidence, setMissingEvidence] = useState([]);
  const [alerts, setAlerts] = useState([]);
  
  // New state for WebSocket telemetry and heatmap
  const [telemetryNodes, setTelemetryNodes] = useState({});
  const [wsStatus, setWsStatus] = useState('Connecting...');
  
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  // Persist the WebSocket connection across re-renders
  const ws = useRef(null);

  useEffect(() => {
    // 1. Initial REST Fetch for state
    const fetchDashboardData = async () => {
      try {
        setError(null);
        const [incRes, missingRes, alertsRes] = await Promise.all([
          api.get('/storage/incidents').catch(() => ({ data: { data: [] } })),
          api.get('/monitoring/missing-evidence').catch(() => ({ data: { data: [] } })),
          api.get('/alerts/active-window').catch(() => ({ data: { data: [] } }))
        ]);
        
        setIncidents(incRes.data?.data || []);
        setMissingEvidence(missingRes.data?.data || []);
        setAlerts(alertsRes.data?.data || []);
      } catch (err) {
        console.error('Failed to load dashboard data:', err);
        setError('Failed to fetch dashboard data.');
      } finally {
        setLoading(false);
      }
    };
    
    fetchDashboardData();

    // 2. Initialize WebSocket Connection for Real-Time Z-Scores
    try {
      ws.current = new WebSocket('ws://localhost:8080/ws/telemetry');
      
      ws.current.onopen = () => setWsStatus('Connected');
      ws.current.onclose = () => setWsStatus('Disconnected');
      ws.current.onerror = () => setWsStatus('Error');
      
      ws.current.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data);
          // Expecting data payload: { deviceId: 'router-01', zScore: 3.5 }
          setTelemetryNodes(prev => ({
            ...prev,
            [data.deviceId]: {
               ...data,
               // Calculate heat color based on Z-Score severity
               color: data.zScore >= 3.0 ? 'var(--status-critical)' : data.zScore >= 2.0 ? 'var(--status-warning)' : 'var(--status-healthy)'
            }
          }));
        } catch (e) {
          console.error("Invalid WS message", e);
        }
      };
    } catch (e) {
      console.error("WebSocket init failed", e);
      setWsStatus('Error');
    }

    return () => {
      if (ws.current) ws.current.close();
    };
  }, []);

  if (loading) {
    return (
      <div className="dashboard-container fade-in" style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '50vh' }}>
        <p style={{ color: 'var(--text-muted)' }}>Loading dashboard metrics...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="dashboard-container fade-in" style={{ padding: '2rem' }}>
        <div className="terminal-panel" style={{ padding: '2rem', borderLeft: '4px solid var(--status-critical)', background: 'rgba(239, 68, 68, 0.1)' }}>
          <h3 style={{ color: 'var(--status-critical)', marginBottom: '0.5rem' }}>Connection Error</h3>
          <p style={{ color: 'var(--text-secondary)' }}>{error}</p>
          <button className="btn-primary" style={{ marginTop: '1rem' }} onClick={() => window.location.reload()}>Retry Connection</button>
        </div>
      </div>
    );
  }

  return (
    <div className="dashboard-container fade-in">
      <div className="metrics-grid">
        <div className="metric-card terminal-panel">
          <div className="metric-icon" style={{ background: 'rgba(239, 68, 68, 0.2)', color: 'var(--status-critical)' }}>
            <ShieldAlert size={24} />
          </div>
          <div className="metric-info">
            <p>Active Incidents</p>
            <h3>{incidents.length} Total</h3>
          </div>
        </div>
        
        {/* Silent Drop Alert Center (Pulses if there is missing evidence) */}
        <div className="metric-card terminal-panel" style={missingEvidence.length > 0 ? { borderLeft: '4px solid var(--status-critical)', animation: 'pulse 2s infinite' } : {}}>
          <div className="metric-icon" style={{ background: 'rgba(245, 158, 11, 0.2)', color: 'var(--status-warning)' }}>
            <WifiOff size={24} />
          </div>
          <div className="metric-info">
            <p>Silent Drops</p>
            <h3>{missingEvidence.length} Missing</h3>
          </div>
        </div>
      </div>

      {/* Global Health Heatmap */}
      <div className="terminal-panel panel-large" style={{ marginBottom: '1.5rem' }}>
        <div className="panel-header">
          <h3 style={{ display: 'flex', alignItems: 'center' }}>
            <Activity size={18} style={{ marginRight: '8px' }}/> Global Health Heatmap
          </h3>
          <span className="text-muted" style={{ fontSize: '0.8rem' }}>WebSocket: {wsStatus}</span>
        </div>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(140px, 1fr))', gap: '1rem', padding: '1rem' }}>
          {Object.keys(telemetryNodes).length === 0 ? (
            <p className="text-muted" style={{ fontStyle: 'italic' }}>Listening for live telemetry streams...</p>
          ) : (
            Object.values(telemetryNodes).map(node => (
              <div key={node.deviceId} style={{ 
                background: node.color, 
                padding: '1rem', 
                borderRadius: '0px', /* Sharp terminal edges */
                border: '1px solid var(--border-light)',
                color: '#fff', 
                textAlign: 'center', 
                transition: 'background-color 0.4s ease' 
              }}>
                <h4 style={{ margin: 0, fontSize: '0.9rem', color: 'rgba(255,255,255,0.9)' }}>{node.deviceId}</h4>
                <p style={{ margin: '0.5rem 0 0', fontSize: '1.4rem', fontWeight: 'bold' }}>Z: {node.zScore?.toFixed(2)}</p>
              </div>
            ))
          )}
        </div>
      </div>

      <div className="dashboard-panels">
        {/* Active Incidents */}
        <div className="terminal-panel panel-large" style={{ display: 'flex', flexDirection: 'column' }}>
          <div className="panel-header">
            <h3>Active Incidents</h3>
            <button className="btn-secondary">View All</button>
          </div>
          <div className="incident-list">
            {incidents.map(inc => (
              <div 
                key={inc.incidentId} 
                className={`incident-row ${inc.severity?.toLowerCase()}`}
                onClick={() => setSelectedIncident(selectedIncident?.incidentId === inc.incidentId ? null : inc)}
                style={{ cursor: 'pointer', border: selectedIncident?.incidentId === inc.incidentId ? '1px solid var(--accent-indigo)' : '' }}
              >
                <div className="inc-status-indicator"></div>
                <div className="inc-details">
                  <h4>{inc.incidentId} - {inc.primaryDeviceId}</h4>
                  <p>Status: {inc.status}</p>
                </div>
                <div className="inc-meta">
                  <span className="badge">{inc.severity}</span>
                  <span className="text-muted">{new Date(inc.startTime).toLocaleTimeString()}</span>
                </div>
              </div>
            ))}
            {incidents.length === 0 && <p className="text-muted">No active incidents.</p>}
          </div>
          
          {/* Render Evidence Matrix and Causal Engine output if an incident is selected */}
          {selectedIncident && (
            <div style={{ marginTop: 'auto', padding: '1rem', borderTop: '1px solid var(--border-light)', display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
              <EvidenceMatrix evidence={selectedIncident.evidence || {}} />
              
              <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '1.5rem' }}>
                <CausalTopology 
                  nodes={selectedIncident.topology?.nodes || []} 
                  links={selectedIncident.topology?.links || []} 
                />
                <BayesianLeaderboard 
                  candidates={selectedIncident.bayesian_candidates || []} 
                />
              </div>
              <CopilotReport report={selectedIncident.copilot_report || {
                summary: "The ML Engine has detected an anomalous BGP flap associated with recent configuration changes on the core router.",
                analysis: "Bayesian analysis indicates a 94.2% probability that the root cause is a malformed route map pushed during the last maintenance window. This caused an unexpected prefix drop which led to the silent drop of downstream telemetry.",
                suggested_commands: [
                  { syntax: "show ip bgp summary" },
                  { syntax: "show run | section route-map" },
                  { syntax: "rollback configuration last 1" }
                ]
              }} />
            </div>
          )}
        </div>

        {/* Side Panels */}
        <div className="side-panels">
          {/* Missing Evidence List */}
          <div className="terminal-panel panel-small">
            <div className="panel-header">
              <h3><AlertTriangle size={18} style={{ color: 'var(--status-warning)' }} /> Missing Signals</h3>
            </div>
            <div className="list-compact">
              {missingEvidence.map(me => (
                <div key={me.eventId} className="list-item">
                  <p className="item-title">{me.sourceDeviceId}</p>
                  <p className="text-muted text-xs">{me.missingSignalType} - {me.secondsOverdue}s ago</p>
                </div>
              ))}
              {missingEvidence.length === 0 && <p className="text-muted text-xs">All systems reporting normally.</p>}
            </div>
          </div>
          
          {/* Recent Alerts Feed */}
          <div className="terminal-panel panel-small" style={{ flex: 1, marginTop: '16px' }}>
            <div className="panel-header">
              <h3>Recent Alerts</h3>
            </div>
            <div className="list-compact">
              {alerts.map(alt => (
                <div key={alt.alertId} className="list-item">
                  <div className="item-header">
                    <span className={`dot ${alt.severity?.toLowerCase()}`}></span>
                    <span className="item-title">{alt.sourceDeviceId}</span>
                  </div>
                  <p className="text-muted text-xs">{alt.alertName}</p>
                  <p className="text-xs" style={{ color: 'var(--accent-indigo)' }}>Source: {alt.alertSource}</p>
                </div>
              ))}
              {alerts.length === 0 && <p className="text-muted text-xs">No recent alerts.</p>}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}