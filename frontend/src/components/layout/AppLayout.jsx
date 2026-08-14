import { useState, useCallback, useEffect } from 'react';
import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';
import Header from './Header';
import './Layout.css';

export default function AppLayout() {
  const [sidebarWidth, setSidebarWidth] = useState(280);
  const [isResizing, setIsResizing] = useState(false);
  const [isCollapsed, setIsCollapsed] = useState(false);

  const startResizing = useCallback((e) => {
    e.preventDefault();
    setIsResizing(true);
  }, []);

  const stopResizing = useCallback(() => {
    setIsResizing(false);
  }, []);

  const resize = useCallback((e) => {
    if (isResizing) {
      const fixedWidths = [80, 220, 280, 360, 480];
      const newWidth = fixedWidths.reduce((prev, curr) => 
        Math.abs(curr - e.clientX) < Math.abs(prev - e.clientX) ? curr : prev
      );
      
      if (newWidth === 80) {
        setIsCollapsed(true);
      } else {
        setIsCollapsed(false);
        setSidebarWidth(newWidth);
      }
    }
  }, [isResizing]);

  useEffect(() => {
    if (isResizing) {
      window.addEventListener('mousemove', resize);
      window.addEventListener('mouseup', stopResizing);
      document.body.style.cursor = 'col-resize';
      document.body.style.userSelect = 'none';
    } else {
      document.body.style.cursor = '';
      document.body.style.userSelect = '';
    }
    return () => {
      window.removeEventListener('mousemove', resize);
      window.removeEventListener('mouseup', stopResizing);
    };
  }, [isResizing, resize, stopResizing]);

  const cycleSidebarWidth = useCallback(() => {
    const fixedWidths = [80, 220, 280, 360, 480];
    const currentWidth = isCollapsed ? 80 : sidebarWidth;
    let nextIndex = fixedWidths.indexOf(currentWidth) + 1;
    if (nextIndex >= fixedWidths.length) nextIndex = 0;
    
    const newWidth = fixedWidths[nextIndex];
    if (newWidth === 80) {
      setIsCollapsed(true);
    } else {
      setIsCollapsed(false);
      setSidebarWidth(newWidth);
    }
  }, [isCollapsed, sidebarWidth]);

  return (
    <div className="app-layout" style={{ '--sidebar-width': isCollapsed ? '80px' : `${sidebarWidth}px` }}>
      <Sidebar 
        isCollapsed={isCollapsed} 
        setIsCollapsed={setIsCollapsed} 
        sidebarWidth={sidebarWidth} 
        cycleSidebarWidth={cycleSidebarWidth}
      />
      <div className={`sidebar-resizer ${isResizing ? 'resizing' : ''}`} onMouseDown={startResizing} />
      <div className="main-wrapper">
        <Header />
        <main className="main-content">
          {/* This is where nested routes will render */}
          <Outlet />
        </main>
      </div>
    </div>
  );
}
