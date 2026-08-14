import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Network, Shield, AlertCircle } from 'lucide-react';
import api from '../services/api';
import './Login.css';

export default function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const response = await api.post('/auth/login', { username, password });
      // ApiResponse wraps the map in a 'data' object
      localStorage.setItem('token', response.data.data.token);
      
      navigate('/');
    } catch (err) {
      setError('Invalid credentials or system unavailable.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-box terminal-panel">
        <div className="login-header">
          <div className="login-logo">
            <Network size={36} color="white" />
          </div>
          <h2>Network Anomaly Assistant</h2>
          <p className="text-muted">NRE Operations Command Center</p>
        </div>

        {error && (
          <div className="login-error">
            <AlertCircle size={18} />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleLogin} className="login-form">
          <div className="input-group">
            <label>Username</label>
            <input 
              type="text" 
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="admin"
              required
            />
          </div>
          <div className="input-group">
            <label>Password</label>
            <input 
              type="password" 
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              required
            />
          </div>
          
          <button type="submit" className="btn-primary login-btn" disabled={loading}>
            <Shield size={18} />
            {loading ? 'Authenticating...' : 'Secure Login'}
          </button>
        </form>
      </div>
      
      <div className="login-background-fx"></div>
    </div>
  );
}
