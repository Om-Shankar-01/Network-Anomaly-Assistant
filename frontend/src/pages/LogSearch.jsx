import { useState } from 'react';
import { Search, Filter, Download } from 'lucide-react';
import api from '../services/api';
import './LogSearch.css';

export default function LogSearch() {
  const [query, setQuery] = useState('');
  const [logs, setLogs] = useState([]);

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!query) return;
    try {
      const response = await api.get(`/storage/logs/search?query=${encodeURIComponent(query)}`);
      const results = response.data?.data || [];
      const formattedLogs = results.map(log => ({
        time: new Date(log.timestamp).toLocaleTimeString(),
        source: log.sourceDeviceId,
        level: log.logLevel,
        msg: log.logMessage
      }));
      setLogs(formattedLogs);
    } catch (error) {
      console.error('Search failed:', error);
    }
  };

  return (
    <div className="log-search-container">
      <div className="search-header terminal-panel">
        <form className="search-bar" onSubmit={handleSearch}>
          <Search size={20} className="text-muted" />
          <input 
            type="text" 
            placeholder="Search logs by keyword, device, or trace ID (e.g. error AND CoreRouter)..." 
            value={query}
            onChange={(e) => setQuery(e.target.value)}
          />
          <button type="submit" className="btn-primary">Search</button>
        </form>
        <div className="search-actions">
          <button className="btn-secondary"><Filter size={16} /> Filters</button>
          <button className="btn-secondary"><Download size={16} /> Export</button>
        </div>
      </div>

      <div className="log-results terminal-panel">
        {logs.length === 0 ? (
          <div className="empty-state text-muted">
            <Search size={48} style={{ opacity: 0.2, marginBottom: '16px' }} />
            <p>Enter a query to search across unified logs and alerts.</p>
          </div>
        ) : (
          <table className="log-table">
            <thead>
              <tr>
                <th>Timestamp</th>
                <th>Source</th>
                <th>Level</th>
                <th>Message</th>
              </tr>
            </thead>
            <tbody>
              {logs.map((log, idx) => (
                <tr key={idx} className={log.level.toLowerCase()}>
                  <td className="log-time">{log.time}</td>
                  <td>{log.source}</td>
                  <td><span className={`badge ${log.level.toLowerCase()}`}>{log.level}</span></td>
                  <td className="log-msg">{log.msg}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
