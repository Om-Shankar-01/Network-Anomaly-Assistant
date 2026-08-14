import { ShieldCheck, Activity, WifiOff } from 'lucide-react';
import './EvidenceMatrix.css';

export default function EvidenceMatrix({ evidence = {} }) {
  // Extract categories from evidence prop (with defaults if undefined)
  const confirmed = evidence.confirmed || [];
  const correlated = evidence.correlated || [];
  const missing = evidence.missing || [];

  return (
    <div className="evidence-matrix-container terminal-panel">
      <div className="panel-header">
        <h3>3-Way Evidence Matrix</h3>
        <span className="text-muted text-xs">Incident Context Grouping</span>
      </div>
      
      <div className="kanban-board">
        {/* Column 1: Confirmed */}
        <div className="kanban-column">
          <div className="kanban-header border-critical">
            <ShieldCheck size={16} className="text-critical" />
            <h4>Confirmed</h4>
            <span className="badge">{confirmed.length}</span>
          </div>
          <div className="kanban-body">
            {confirmed.map((item, idx) => (
              <div key={idx} className="evidence-card">
                <p className="evidence-source">{item.source}</p>
                <p className="evidence-desc">{item.description}</p>
                <span className="evidence-time">{item.timestamp}</span>
              </div>
            ))}
            {confirmed.length === 0 && <p className="text-muted text-center text-sm p-4">No hard evidence.</p>}
          </div>
        </div>

        {/* Column 2: Correlated */}
        <div className="kanban-column">
          <div className="kanban-header border-info">
            <Activity size={16} style={{ color: 'var(--accent-indigo)' }} />
            <h4>Correlated</h4>
            <span className="badge">{correlated.length}</span>
          </div>
          <div className="kanban-body">
            {correlated.map((item, idx) => (
              <div key={idx} className="evidence-card">
                <p className="evidence-source">{item.source}</p>
                <p className="evidence-desc">{item.description}</p>
                <div className="evidence-footer">
                  <span className="evidence-time">{item.timestamp}</span>
                  {item.metric && <span className="evidence-metric text-info">{item.metric}</span>}
                </div>
              </div>
            ))}
            {correlated.length === 0 && <p className="text-muted text-center text-sm p-4">No statistical anomalies.</p>}
          </div>
        </div>

        {/* Column 3: Missing */}
        <div className="kanban-column">
          <div className="kanban-header border-warning">
            <WifiOff size={16} className="text-warning" />
            <h4>Missing</h4>
            <span className="badge">{missing.length}</span>
          </div>
          <div className="kanban-body">
            {missing.map((item, idx) => (
              <div key={idx} className="evidence-card">
                <p className="evidence-source">{item.source}</p>
                <p className="evidence-desc text-warning">{item.description}</p>
                <span className="evidence-time">{item.timestamp}</span>
              </div>
            ))}
            {missing.length === 0 && <p className="text-muted text-center text-sm p-4">No dropped signals.</p>}
          </div>
        </div>
      </div>
    </div>
  );
}
