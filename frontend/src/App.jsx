import { Routes, Route } from 'react-router-dom';
import AppLayout from './components/layout/AppLayout';

import Dashboard from './pages/Dashboard';
import Topology from './pages/Topology';
import CausalAnalysis from './pages/CausalAnalysis';
import AuditReports from './pages/AuditReports';
import IncidentHistory from './pages/IncidentHistory';

import LogSearch from './pages/LogSearch';
import Admin from './pages/Admin';

import Login from './pages/Login';

function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/" element={<AppLayout />}>
        <Route index element={<Dashboard />} />
        <Route path="topology" element={<Topology />} />
        <Route path="causal" element={<CausalAnalysis />} />
        <Route path="audit" element={<AuditReports />} />
        <Route path="history" element={<IncidentHistory />} />
        <Route path="search" element={<LogSearch />} />
        <Route path="admin" element={<Admin />} />
      </Route>
    </Routes>
  );
}

export default App;
