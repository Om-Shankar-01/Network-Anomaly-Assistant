import { useState, useEffect } from 'react';
import ReactMarkdown from 'react-markdown';
import { Copy, Terminal, History, GitCommit } from 'lucide-react';
import api from '../services/api';
import './AuditReports.css';

export default function AuditReports() {
  const [report, setReport] = useState('');
  const [timeline, setTimeline] = useState([]);

  useEffect(() => {
    const fetchAuditData = async () => {
      try {
        // Hardcoded INC-TEST for phase F4 integration testing
        const incidentId = 'INC-TEST';
        const [reportRes, historyRes] = await Promise.all([
          api.get(`/audit/incidents/${incidentId}/export`),
          api.get(`/audit/incidents/${incidentId}/history`)
        ]);

        if (reportRes.data?.data?.postMortemReport) {
          setReport(reportRes.data.data.postMortemReport);
        }

        const historyData = historyRes.data?.data || [];
        const formattedTimeline = historyData.map(record => {
          let color = 'var(--text-secondary)';
          if (record.eventType === 'ANOMALY_DETECTED') color = 'var(--status-critical)';
          else if (record.eventType === 'CORRELATED') color = 'var(--status-warning)';
          else if (record.eventType === 'ROOT_CAUSE_CONFIRMED' || record.eventType === 'CONFIDENCE_BOOSTED') color = 'var(--accent-indigo)';

          return {
            time: new Date(record.timestampUtc).toLocaleTimeString(),
            state: record.eventType,
            desc: record.details || record.newHypothesis || 'Audit record generated',
            color
          };
        });

        if (formattedTimeline.length > 0) {
          setTimeline(formattedTimeline);
        }
      } catch (err) {
        console.error('Failed to load audit data:', err);
      }
    };
    
    fetchAuditData();
  }, []);

  const copyCommand = (cmd) => {
    navigator.clipboard.writeText(cmd);
    // show toast in real app
  };

  const CodeBlock = ({ node, inline, className, children, ...props }) => {
    const match = /language-(\w+)/.exec(className || '');
    const codeString = String(children).replace(/\n$/, '');
    
    if (!inline && match && match[1] === 'bash') {
      return (
        <div className="cli-block">
          <div className="cli-header">
            <Terminal size={14} /> <span>Bash Command</span>
            <button className="copy-btn" onClick={() => copyCommand(codeString)}>
              <Copy size={14} /> Copy
            </button>
          </div>
          <pre className={className} {...props}>
            <code>{children}</code>
          </pre>
        </div>
      );
    }
    return <code className={className} {...props}>{children}</code>;
  };

  return (
    <div className="audit-container">
      {/* Markdown Viewer */}
      <div className="report-panel terminal-panel">
        <div className="panel-header">
          <h3>LLM Diagnostic Report</h3>
        </div>
        <div className="markdown-content">
          <ReactMarkdown components={{ code: CodeBlock }}>
            {report}
          </ReactMarkdown>
        </div>
      </div>

      {/* Audit Timeline */}
      <div className="timeline-panel terminal-panel">
        <div className="panel-header">
          <h3><History size={18} /> Audit Trail & History</h3>
        </div>
        <div className="timeline-content">
          {timeline.map((event, idx) => (
            <div key={idx} className="timeline-item">
              <div className="timeline-marker" style={{ background: event.color }}>
                <GitCommit size={14} />
              </div>
              <div className="timeline-details">
                <span className="timeline-time">{event.time}</span>
                <h4 style={{ color: event.color }}>{event.state}</h4>
                <p>{event.desc}</p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
