import { useEffect, useRef, useState } from 'react';
import ForceGraph2D from 'react-force-graph-2d';
import { Maximize2, Minimize2 } from 'lucide-react';
import './CausalTopology.css';

export default function CausalTopology({ nodes = [], links = [] }) {
  const fgRef = useRef();
  const [isFullscreen, setIsFullscreen] = useState(false);
  const containerRef = useRef(null);

  useEffect(() => {
    // Make the graph zoom to fit when data changes
    if (fgRef.current && nodes.length > 0) {
      setTimeout(() => fgRef.current.zoomToFit(400, 50), 100);
    }
  }, [nodes, links]);

  const toggleFullscreen = () => {
    if (!document.fullscreenElement) {
      containerRef.current.requestFullscreen().catch(err => {
        console.error(`Error attempting to enable full-screen mode: ${err.message}`);
      });
    } else {
      document.exitFullscreen();
    }
  };

  useEffect(() => {
    const handleFullscreenChange = () => {
      setIsFullscreen(!!document.fullscreenElement);
    };
    document.addEventListener('fullscreenchange', handleFullscreenChange);
    return () => document.removeEventListener('fullscreenchange', handleFullscreenChange);
  }, []);

  return (
    <div className={`topology-container terminal-panel ${isFullscreen ? 'fullscreen' : ''}`} ref={containerRef}>
      <div className="panel-header">
        <h3>Granger Causal Topology</h3>
        <button className="btn-icon" onClick={toggleFullscreen} title="Toggle Fullscreen">
          {isFullscreen ? <Minimize2 size={18} /> : <Maximize2 size={18} />}
        </button>
      </div>
      
      <div className="topology-canvas">
        {nodes.length === 0 ? (
          <div className="empty-state">
            <p className="text-muted">No topology data available for this incident.</p>
          </div>
        ) : (
          <ForceGraph2D
            ref={fgRef}
            graphData={{ nodes, links }}
            nodeLabel="id"
            nodeColor={node => {
              if (node.isRootCause) return 'var(--status-critical)';
              if (node.isAffected) return 'var(--status-warning)';
              return 'var(--accent-indigo)';
            }}
            nodeRelSize={6}
            linkColor={() => 'var(--border-light)'}
            linkWidth={link => link.causal_weight ? link.causal_weight * 3 : 1}
            linkDirectionalArrowLength={3.5}
            linkDirectionalArrowRelPos={1}
            backgroundColor={isFullscreen ? 'var(--bg-obsidian)' : 'transparent'}
            width={isFullscreen ? window.innerWidth : undefined}
            height={isFullscreen ? window.innerHeight - 60 : 400}
            onNodeClick={(node) => {
              // Center node on click
              fgRef.current.centerAt(node.x, node.y, 1000);
              fgRef.current.zoom(8, 2000);
            }}
          />
        )}
      </div>
    </div>
  );
}
