import { useState, useEffect, useRef, useCallback } from 'react';
import ForceGraph2D from 'react-force-graph-2d';
import { Target, Maximize, ZoomIn, ZoomOut } from 'lucide-react';
import api from '../services/api';
import './Topology.css';

export default function Topology() {
  const [graphData, setGraphData] = useState({ nodes: [], links: [] });
  const [highlightNodes, setHighlightNodes] = useState(new Set());
  const [highlightLinks, setHighlightLinks] = useState(new Set());
  const fgRef = useRef();

  useEffect(() => {
    if (fgRef.current) {
      // Increase repulsive charge for wider spacing
      fgRef.current.d3Force('charge').strength(-800);
      fgRef.current.d3Force('link').distance(120);
    }
  }, [graphData]);

  useEffect(() => {
    const fetchTopology = async () => {
      try {
        const response = await api.get('/topology/devices');
        const devices = response.data?.data || [];
        
        const nodes = [];
        const links = [];
        
        devices.forEach(device => {
          nodes.push({
            id: device.id,
            group: device.deviceType,
            val: 10,
            status: device.status?.toLowerCase() || 'healthy'
          });
          
          if (device.dependencies) {
            device.dependencies.forEach(dep => {
              links.push({
                source: device.id,
                target: dep.target?.id || dep.target
              });
            });
          }
        });
        
        setGraphData({ nodes, links });
      } catch (error) {
        console.error('Failed to load topology graph:', error);
      }
    };
    
    fetchTopology();
  }, []);

  const handleNodeClick = useCallback(async (node) => {
    if (!node) return;
    try {
      const response = await api.get(`/topology/devices/${node.id}/blast-radius`);
      const blastNodes = response.data?.data || [];
      const blastIds = new Set(blastNodes.map(n => n.id));
      
      setGraphData(prev => ({
        ...prev,
        nodes: prev.nodes.map(n => {
          if (n.id === node.id) {
            return { ...n, status: 'root_cause' };
          }
          if (blastIds.has(n.id)) {
            return { ...n, status: 'impacted' };
          }
          return { ...n, status: 'healthy' };
        })
      }));
    } catch (error) {
      console.error('Failed to calculate blast radius:', error);
    }
  }, []);

  const handleNodeHover = useCallback((node) => {
    setHighlightNodes(new Set(node ? [node] : []));
    
    if (node) {
      const links = graphData.links.filter(l => l.source.id === node.id || l.target.id === node.id);
      setHighlightLinks(new Set(links));
      links.forEach(l => {
        setHighlightNodes(prev => new Set([...prev, l.source, l.target]));
      });
    } else {
      setHighlightLinks(new Set());
    }
  }, [graphData]);

  const paintNode = useCallback((node, ctx, globalScale) => {
    const label = node.id;
    const fontSize = 14 / globalScale;
    ctx.font = `${fontSize}px 'JetBrains Mono', monospace`;
    
    let baseColor = '#10B981';
    if (node.status === 'root_cause') baseColor = '#EF4444';
    else if (node.status === 'impacted') baseColor = '#F59E0B';

    const textWidth = ctx.measureText(label).width;
    const padding = 12 / globalScale;
    const width = textWidth + padding * 2;
    const height = fontSize + padding * 2;
    
    // Expand node.val so pointer interactions and force physics scale with the text box
    node.val = width / 2.5;

    const x = node.x - width / 2;
    const y = node.y - height / 2;
    const radius = 4 / globalScale;

    // Fill Background (10% opacity of base color)
    ctx.fillStyle = baseColor + '1A';
    ctx.beginPath();
    if (ctx.roundRect) {
      ctx.roundRect(x, y, width, height, radius);
    } else {
      ctx.rect(x, y, width, height);
    }
    ctx.fill();

    // Solid Border
    ctx.strokeStyle = highlightNodes.has(node) ? '#fff' : baseColor;
    ctx.lineWidth = (highlightNodes.has(node) ? 2 : 1) / globalScale;
    ctx.stroke();

    // Centered Text
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.fillStyle = '#F8FAFC';
    ctx.fillText(label, node.x, node.y);
  }, [highlightNodes]);

  const handleZoomIn = () => {
    if (fgRef.current) fgRef.current.zoom(fgRef.current.zoom() * 1.5, 400);
  };

  const handleZoomOut = () => {
    if (fgRef.current) fgRef.current.zoom(fgRef.current.zoom() / 1.5, 400);
  };

  const handleFullscreen = () => {
    const elem = document.querySelector('.graph-wrapper');
    if (!document.fullscreenElement) {
      elem.requestFullscreen().catch(err => console.log(err));
    } else {
      document.exitFullscreen();
    }
  };

  return (
    <div className="topology-container">
      <div className="topology-header terminal-panel">
        <div>
          <h2>Blast Radius Visualizer</h2>
          <p className="text-muted">Live topology mapping causal dependencies.</p>
        </div>
        <div className="toolbar">
          <button className="icon-btn" onClick={() => fgRef.current.zoomToFit(400)} title="Zoom to Fit"><Target size={20}/></button>
          <button className="icon-btn" onClick={handleZoomIn} title="Zoom In"><ZoomIn size={20}/></button>
          <button className="icon-btn" onClick={handleZoomOut} title="Zoom Out"><ZoomOut size={20}/></button>
          <button className="icon-btn" onClick={handleFullscreen} title="Toggle Fullscreen"><Maximize size={20}/></button>
        </div>
      </div>
      
      <div className="graph-wrapper terminal-panel">
        <div className="legend">
          <div className="legend-item"><span className="dot critical"></span> Root Cause</div>
          <div className="legend-item"><span className="dot warning"></span> Impacted</div>
          <div className="legend-item"><span className="dot" style={{background: '#10B981'}}></span> Healthy</div>
        </div>
        
        <ForceGraph2D
          ref={fgRef}
          graphData={graphData}
          nodeCanvasObject={paintNode}
          nodePointerAreaPaint={(node, color, ctx) => {
            ctx.fillStyle = color;
            ctx.beginPath();
            ctx.arc(node.x, node.y, node.val, 0, 2 * Math.PI, false);
            ctx.fill();
          }}
          onRenderFramePre={(ctx) => {
            const gridSize = 50;
            const span = 4000;
            ctx.save();
            ctx.beginPath();
            ctx.strokeStyle = 'rgba(255, 255, 255, 0.05)';
            // Keep line width constant visually regardless of zoom by dividing by the current transform scale
            // getTransform is supported in all modern browsers
            const scale = ctx.getTransform().a || 1;
            ctx.lineWidth = 1 / scale;
            
            for (let x = -span; x <= span; x += gridSize) {
              ctx.moveTo(x, -span);
              ctx.lineTo(x, span);
            }
            for (let y = -span; y <= span; y += gridSize) {
              ctx.moveTo(-span, y);
              ctx.lineTo(span, y);
            }
            
            ctx.stroke();
            ctx.restore();
          }}
          onNodeHover={handleNodeHover}
          onNodeClick={handleNodeClick}
          linkColor={link => highlightLinks.has(link) ? 'rgba(255,255,255,1)' : 'rgba(255,255,255,0.2)'}
          linkWidth={link => highlightLinks.has(link) ? 3 : 1}
          linkDirectionalParticles={4}
          linkDirectionalParticleWidth={link => highlightLinks.has(link) ? 4 : 2}
          linkDirectionalParticleColor={() => 'rgba(255,255,255,0.6)'}
          cooldownTicks={100}
        />
      </div>
    </div>
  );
}
