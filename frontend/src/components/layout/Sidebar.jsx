import { NavLink } from 'react-router-dom';
import { 
  LayoutDashboard, 
  Network, 
  BrainCircuit, 
  FileText, 
  Search,
  Settings,
  ArrowRightLeft
} from 'lucide-react';
import './Layout.css';

export default function Sidebar({ isCollapsed, setIsCollapsed, sidebarWidth, cycleSidebarWidth }) {
  const navItems = [
    { name: 'Dashboard', path: '/', icon: <LayoutDashboard size={20} /> },
    { name: 'Topology', path: '/topology', icon: <Network size={20} /> },
    { name: 'Causal Analysis', path: '/causal', icon: <BrainCircuit size={20} /> },
    { name: 'Audit & Reports', path: '/audit', icon: <FileText size={20} /> },
    { name: 'Log Search', path: '/search', icon: <Search size={20} /> },
    { name: 'ML Sandbox', path: '/admin', icon: <Settings size={20} /> },
  ];

  const showTitle = !isCollapsed && sidebarWidth >= 280;

  return (
    <aside className={`app-sidebar terminal-panel ${isCollapsed ? 'collapsed' : ''}`}>
      <div className="logo-container" onClick={() => setIsCollapsed(!isCollapsed)} style={{ cursor: 'pointer', justifyContent: showTitle ? 'flex-start' : 'center' }}>
        <div className="logo-icon">
          <Network size={28} color="white" />
        </div>
        {showTitle && <span className="logo-text">AnomalyAssistant</span>}
      </div>

      <nav className="sidebar-nav">
        {navItems.map((item) => (
          <NavLink 
            key={item.path} 
            to={item.path}
            className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
            title={isCollapsed ? item.name : undefined}
          >
            <span className="nav-icon">{item.icon}</span>
            {!isCollapsed && item.name}
          </NavLink>
        ))}
      </nav>
      
      <div className="sidebar-footer" style={{ display: 'flex', alignItems: 'center', justifyContent: isCollapsed ? 'center' : 'space-between' }}>
        {!isCollapsed && <p className="version">v2.1.0-alpha</p>}
        <button 
          className="icon-btn" 
          onClick={cycleSidebarWidth} 
          title="Cycle Sidebar Width"
          style={{ padding: '8px' }}
        >
          <ArrowRightLeft size={16} />
        </button>
      </div>
    </aside>
  );
}
