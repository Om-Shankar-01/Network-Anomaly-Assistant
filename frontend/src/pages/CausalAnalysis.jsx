import { useState, useEffect } from 'react';
import { BrainCircuit, CheckCircle2, ActivitySquare, ShieldQuestion } from 'lucide-react';
import api from '../services/api';
import './CausalAnalysis.css';

export default function CausalAnalysis() {
  const [evidence, setEvidence] = useState({
    confirmed: [],
    correlated: [],
    missing: [],
    leaderboard: []
  });

  useEffect(() => {
    const fetchContext = async () => {
      try {
        // Hardcoding CoreRouter-01 for now, in a real app this would come from a selected incident or URL param
        const response = await api.get('/explainability/context/CoreRouter-01');
        const data = response.data?.data;
        if (!data) return;

        setEvidence({
          confirmed: (data.confirmedEvidence || []).map((text, idx) => ({ id: `ev-${idx}`, text, time: new Date().toLocaleTimeString(), source: 'Syslog' })),
          correlated: (data.correlatedSignals || []).map((text, idx) => ({ id: `co-${idx}`, text, stat: 'Correlated' })),
          missing: (data.missingEvidence || []).map((text, idx) => ({ id: `mi-${idx}`, text, expected: 'Expected' })),
          leaderboard: [
            { 
              candidate: data.rootCauseHypothesis || 'Unknown', 
              probability: data.confidenceScore || 0, 
              level: data.confidenceRating || 'LOW' 
            }
          ]
        });
      } catch (error) {
        console.error('Failed to fetch causal context:', error);
      }
    };
    
    fetchContext();
  }, []);

  return (
    <div className="causal-container">
      <div className="causal-header terminal-panel">
        <div className="header-icon-wrapper">
          <BrainCircuit size={28} color="white" />
        </div>
        <div>
          <h2>Causal Inference Engine</h2>
          <p className="text-muted">Bayesian Root-Cause Ranker & 3-Way Evidence Matrix</p>
        </div>
      </div>

      <div className="causal-grid">
        {/* Evidence Matrix */}
        <div className="evidence-matrix">
          <div className="evidence-col terminal-panel confirmed">
            <div className="col-header">
              <CheckCircle2 size={20} className="text-success" />
              <h3>Confirmed Facts</h3>
            </div>
            <div className="col-content">
              {evidence.confirmed.map(item => (
                <div key={item.id} className="evidence-card">
                  <p className="ev-text">{item.text}</p>
                  <div className="ev-meta">
                    <span className="badge">{item.source}</span>
                    <span className="text-muted text-xs">{item.time}</span>
                  </div>
                </div>
              ))}
            </div>
          </div>

          <div className="evidence-col terminal-panel correlated">
            <div className="col-header">
              <ActivitySquare size={20} className="text-warning" />
              <h3>Correlated Signals</h3>
            </div>
            <div className="col-content">
              {evidence.correlated.map(item => (
                <div key={item.id} className="evidence-card">
                  <p className="ev-text">{item.text}</p>
                  <div className="ev-meta">
                    <span className="text-accent text-xs font-bold">{item.stat}</span>
                  </div>
                </div>
              ))}
            </div>
          </div>

          <div className="evidence-col terminal-panel missing">
            <div className="col-header">
              <ShieldQuestion size={20} className="text-critical" />
              <h3>Missing Signals</h3>
            </div>
            <div className="col-content">
              {evidence.missing.map(item => (
                <div key={item.id} className="evidence-card">
                  <p className="ev-text">{item.text}</p>
                  <div className="ev-meta">
                    <span className="text-muted text-xs">Expected: {item.expected}</span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Bayesian Leaderboard */}
        <div className="leaderboard terminal-panel">
          <div className="panel-header">
            <h3>Bayesian Root-Cause Leaderboard</h3>
            <span className="badge">Updated Live</span>
          </div>
          <div className="leaderboard-content">
            {evidence.leaderboard.map((item, idx) => (
              <div key={idx} className="leaderboard-row">
                <div className="lb-info">
                  <h4>{item.candidate}</h4>
                  <span className={`badge ${item.level.toLowerCase()}`}>{item.level} CONFIDENCE</span>
                </div>
                <div className="lb-bar-container">
                  <div 
                    className={`lb-bar ${item.level.toLowerCase()}`} 
                    style={{ width: `${item.probability}%` }}
                  ></div>
                  <span className="lb-pct">{item.probability}%</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
