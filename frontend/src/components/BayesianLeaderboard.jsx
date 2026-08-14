import './BayesianLeaderboard.css';

export default function BayesianLeaderboard({ candidates = [] }) {
  // Sort candidates by confidence score just in case backend didn't
  const sortedCandidates = [...candidates].sort((a, b) => b.confidence_score - a.confidence_score);

  return (
    <div className="leaderboard-container terminal-panel">
      <div className="panel-header">
        <h3>Bayesian Root-Cause Leaderboard</h3>
        <span className="text-muted text-xs">Probabilistic Ranking</span>
      </div>
      
      <div className="leaderboard-list">
        {sortedCandidates.length === 0 ? (
          <div className="p-4 text-center text-muted">
            <p>No causal candidates calculated yet.</p>
          </div>
        ) : (
          sortedCandidates.map((candidate, idx) => (
            <div key={candidate.node_id} className="leaderboard-item">
              <div className="item-rank">
                <span className={`rank-badge ${idx === 0 ? 'rank-first' : ''}`}>#{idx + 1}</span>
              </div>
              
              <div className="item-details">
                <div className="item-header">
                  <h4>{candidate.node_id}</h4>
                  <span className={`confidence-pill ${candidate.confidence_rating?.toLowerCase()}`}>
                    {candidate.confidence_rating} ({(candidate.confidence_score * 100).toFixed(1)}%)
                  </span>
                </div>
                
                <div className="progress-bar-bg">
                  <div 
                    className={`progress-bar-fill ${idx === 0 ? 'glow-critical' : 'glow-info'}`} 
                    style={{ width: `${candidate.confidence_score * 100}%` }}
                  ></div>
                </div>
                
                <div className="evidence-summary text-xs text-muted">
                  {candidate.evidence_summary?.recent_config_change && (
                    <span className="mr-2 border-bottom-warning" title="Config changed within 24h">Config Changed</span>
                  )}
                  {candidate.evidence_summary?.granger_causal_support && (
                    <span className="mr-2 border-bottom-info" title="Granger p-value < 0.05">Causal Support</span>
                  )}
                  {candidate.evidence_summary?.is_missing_evidence && (
                    <span className="border-bottom-critical" title="Node stopped communicating">Silent Drop</span>
                  )}
                </div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}
