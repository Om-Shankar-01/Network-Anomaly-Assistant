import { useState, useEffect } from 'react';
import { History, Download, Filter, Search } from 'lucide-react';
import api from '../services/api';
import './IncidentHistory.css';

export default function IncidentHistory() {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    // Mock fetching historical incidents for the Feed
    const fetchHistory = async () => {
      try {
        // Normally this would be a dedicated history endpoint
        const res = await api.get('/storage/incidents').catch(() => ({ data: { data: [] } }));
        // Mock data if empty
        const data = res.data?.data?.length > 0 ? res.data.data : [
          { incidentId: 'INC-2026-0801', primaryDeviceId: 'core-router-sjc', severity: 'CRITICAL', status: 'RESOLVED', startTime: new Date(Date.now() - 86400000).toISOString(), resolutionTime: new Date(Date.now() - 82400000).toISOString(), rootCause: 'BGP Flap detected due to bad config push.' },
          { incidentId: 'INC-2026-0728', primaryDeviceId: 'fw-edge-02', severity: 'WARNING', status: 'RESOLVED', startTime: new Date(Date.now() - 486400000).toISOString(), resolutionTime: new Date(Date.now() - 482400000).toISOString(), rootCause: 'High CPU utilization caused by unoptimized ACLs.' }
        ];
        setHistory(data);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    
    fetchHistory();
  }, []);

  const handleExport = (id) => {
    alert(`Mock: Generating SOC2 compliance PDF for incident ${id}...`);
  };

  const filteredHistory = history.filter(inc => 
    inc.incidentId.toLowerCase().includes(searchTerm.toLowerCase()) || 
    inc.primaryDeviceId.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="history-container fade-in">
      <div className="terminal-panel panel-large">
        <div className="panel-header">
          <h3><History size={18} className="mr-2" /> Incident Post-Mortem Feed</h3>
          <div className="actions flex gap-2">
            <div className="search-box">
              <Search size={14} className="text-muted" />
              <input 
                type="text" 
                placeholder="Search history..." 
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
            <button className="btn-secondary"><Filter size={14} className="mr-2"/> Filter</button>
          </div>
        </div>

        <div className="history-list">
          {loading ? (
            <p className="p-4 text-muted text-center">Loading archives...</p>
          ) : filteredHistory.length === 0 ? (
            <p className="p-4 text-muted text-center">No historical incidents found.</p>
          ) : (
            filteredHistory.map(inc => (
              <div key={inc.incidentId} className="history-card">
                <div className="history-card-header">
                  <div className="flex items-center gap-2">
                    <span className={`dot ${inc.severity?.toLowerCase()}`}></span>
                    <h4>{inc.incidentId}</h4>
                    <span className="badge text-xs">{inc.status}</span>
                  </div>
                  <button className="btn-secondary text-xs flex items-center gap-1" onClick={() => handleExport(inc.incidentId)}>
                    <Download size={12} /> SOC2 PDF Export
                  </button>
                </div>
                
                <div className="history-card-body">
                  <div className="grid grid-2">
                    <div>
                      <p className="text-xs text-muted mb-1">Target Device</p>
                      <p className="font-mono text-sm">{inc.primaryDeviceId}</p>
                    </div>
                    <div>
                      <p className="text-xs text-muted mb-1">Severity</p>
                      <p className={`text-sm ${inc.severity === 'CRITICAL' ? 'text-critical' : 'text-warning'}`}>{inc.severity}</p>
                    </div>
                  </div>
                  
                  <div className="grid grid-2 mt-4">
                    <div>
                      <p className="text-xs text-muted mb-1">Detected</p>
                      <p className="text-sm">{new Date(inc.startTime).toLocaleString()}</p>
                    </div>
                    <div>
                      <p className="text-xs text-muted mb-1">Resolved</p>
                      <p className="text-sm">{inc.resolutionTime ? new Date(inc.resolutionTime).toLocaleString() : 'N/A'}</p>
                    </div>
                  </div>

                  <div className="mt-4 pt-4 border-top">
                    <p className="text-xs text-muted mb-1">Identified Root Cause</p>
                    <p className="text-sm">{inc.rootCause || 'Root cause investigation not documented.'}</p>
                  </div>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
}
