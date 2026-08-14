import { useState, useEffect } from 'react';
import { Activity, Bell, ShieldAlert, Cpu } from 'lucide-react';
import api from '../../services/api';
import './Layout.css';

export default function Header() {
  const [health, setHealth] = useState({ status: 'UNKNOWN' });
  const [activeAlerts, setActiveAlerts] = useState(0);

  useEffect(() => {
    const fetchHeaderData = async () => {
      try {
        const [healthRes, alertsRes] = await Promise.all([
          api.get('/system/health'),
          api.get('/alerts/active-window')
        ]);
        
        setHealth({
          status: healthRes.data?.data?.status || 'UNKNOWN'
        });
        
        setActiveAlerts(alertsRes.data?.data?.length || 0);
      } catch (error) {
        console.error('Failed to fetch header data:', error);
      }
    };

    fetchHeaderData();
    // In a real app, this would use WebSockets or polling. 
    // We'll set a basic polling interval for demonstration.
    const interval = setInterval(fetchHeaderData, 10000);
    return () => clearInterval(interval);
  }, []);

  return (
    <header className="app-header terminal-panel">
      <div className="header-left">
        <h1 className="text-gradient">NRE Command Center</h1>
      </div>
      
      <div className="header-right">
        <div className="status-badge">
          <Activity size={16} color={health.status === 'UP' ? 'var(--status-healthy)' : 'var(--status-critical)'} />
          <span>System: {health.status}</span>
        </div>
        <button className="icon-btn">
          <ShieldAlert size={20} />
          {activeAlerts > 0 && <span className="badge">{activeAlerts}</span>}
        </button>
        
        <div className="avatar">
          <span>Admin</span>
        </div>
      </div>
    </header>
  );
}
