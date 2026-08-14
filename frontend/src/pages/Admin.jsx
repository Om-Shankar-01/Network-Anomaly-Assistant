import { useState, useEffect } from 'react';
import { Settings, Play, Database, Server, Plus, Edit2, Trash2 } from 'lucide-react';
import api from '../services/api';
import './Admin.css';

export default function Admin() {
  const [activeTab, setActiveTab] = useState('ml');
  const [mlResult, setMlResult] = useState('');
  const [devices, setDevices] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (activeTab === 'registry') {
      fetchDevices();
    }
  }, [activeTab]);

  const fetchDevices = async () => {
    setLoading(true);
    try {
      const response = await api.get('/topology/devices');
      setDevices(response.data?.data || []);
    } catch (error) {
      console.error('Failed to fetch devices', error);
    } finally {
      setLoading(false);
    }
  };

  const handleAddDevice = async () => {
    const id = window.prompt("Enter Device ID (e.g. CoreRouter-02):");
    if (!id) return;
    const ipAddress = window.prompt("Enter IP Address:");
    const deviceType = window.prompt("Enter Device Type (ROUTER, SWITCH, SERVER):");
    
    try {
      await api.post('/topology/devices', {
        id,
        hostname: id,
        ipAddress,
        deviceType,
        status: 'HEALTHY'
      });
      fetchDevices(); // Refresh list
    } catch (error) {
      console.error('Failed to add device', error);
      alert('Failed to add device');
    }
  };

  const handleRunModel = async (model) => {
    setMlResult(`Executing ${model} model across telemetry data...\n`);
    try {
      let endpoint = '';
      if (model === 'Granger Causality') {
        endpoint = '/ml/test-granger';
      } else if (model === 'Bayesian Network') {
        endpoint = '/ml/test-bayesian';
      }
      
      const response = await api.post(endpoint);
      setMlResult(prev => prev + `\n[SUCCESS] Response from backend:\n` + JSON.stringify(response.data, null, 2));
    } catch (error) {
      setMlResult(prev => prev + `\n[ERROR] Failed to execute model: ${error.message}`);
    }
  };

  return (
    <div className="admin-container">
      <div className="admin-sidebar terminal-panel">
        <div className="admin-header">
          <Settings size={20} />
          <h3>Administration</h3>
        </div>
        <div className="admin-nav">
          <button 
            className={`admin-nav-item ${activeTab === 'ml' ? 'active' : ''}`}
            onClick={() => setActiveTab('ml')}
          >
            <Database size={16} /> ML Sandbox
          </button>
          <button 
            className={`admin-nav-item ${activeTab === 'registry' ? 'active' : ''}`}
            onClick={() => setActiveTab('registry')}
          >
            <Server size={16} /> Device Registry
          </button>
        </div>
      </div>

      <div className="admin-content terminal-panel">
        {activeTab === 'ml' && (
          <div className="ml-sandbox">
            <div className="panel-header">
              <div>
                <h3>Machine Learning Sandbox</h3>
                <p className="text-muted text-sm" style={{ marginTop: '4px' }}>Manually trigger inference models and tune causal hyperparameters.</p>
              </div>
            </div>
            
            <div className="model-grid">
              <div className="model-card">
                <h4>Granger Causality Test</h4>
                <p className="text-muted text-sm">Tests if X's time-series data predicts Y. Used to find correlated metrics.</p>
                <div className="model-params">
                  <label>Max Lag (L)</label>
                  <input type="number" defaultValue={5} />
                  <label>Significance Level (α)</label>
                  <input type="number" defaultValue={0.05} step={0.01} />
                </div>
                <button className="btn-primary" onClick={() => handleRunModel('Granger Causality')}>
                  <Play size={14} /> Run Test
                </button>
              </div>

              <div className="model-card">
                <h4>Bayesian Belief Network</h4>
                <p className="text-muted text-sm">Updates root-cause posterior probabilities using DAG and observed evidence.</p>
                <div className="model-params">
                  <label>Prior Confidence</label>
                  <input type="number" defaultValue={0.1} step={0.05} />
                </div>
                <button className="btn-primary" onClick={() => handleRunModel('Bayesian Network')}>
                  <Play size={14} /> Update Posteriors
                </button>
              </div>
            </div>

            {mlResult && (
              <div className="ml-console">
                <div className="console-header">Console Output</div>
                <pre>{mlResult}</pre>
              </div>
            )}
          </div>
        )}

        {activeTab === 'registry' && (
          <div className="device-registry">
            <div className="panel-header">
              <h3>Topology Device Registry</h3>
              <button className="btn-primary" onClick={handleAddDevice}><Plus size={16}/> Add Device</button>
            </div>
            
            <table className="registry-table">
              <thead>
                <tr>
                  <th>Device ID</th>
                  <th>IP Address</th>
                  <th>Type</th>
                  <th>Role</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {loading ? (
                  <tr>
                    <td colSpan="5" className="text-muted" style={{ textAlign: 'center' }}>Loading devices...</td>
                  </tr>
                ) : devices.length === 0 ? (
                  <tr>
                    <td colSpan="5" className="text-muted" style={{ textAlign: 'center' }}>No devices found in registry.</td>
                  </tr>
                ) : (
                  devices.map(device => (
                    <tr key={device.id}>
                      <td>{device.id}</td>
                      <td>{device.ipAddress}</td>
                      <td>{device.deviceType}</td>
                      <td>{device.status}</td>
                      <td>
                        <div style={{ display: 'flex', gap: '12px' }}>
                          <button className="icon-btn" title="Edit Device"><Edit2 size={16}/></button>
                          <button className="icon-btn text-critical" title="Remove Device"><Trash2 size={16}/></button>
                        </div>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
