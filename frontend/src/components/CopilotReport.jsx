import { useState } from 'react';
import { Copy, Check, Terminal, FileText } from 'lucide-react';
import './CopilotReport.css';

export default function CopilotReport({ report }) {
  const [copiedCmd, setCopiedCmd] = useState(null);

  if (!report) return null;

  const handleCopy = (command, id) => {
    navigator.clipboard.writeText(command);
    setCopiedCmd(id);
    setTimeout(() => setCopiedCmd(null), 2000);
  };

  return (
    <div className="copilot-container terminal-panel mt-4">
      <div className="panel-header border-bottom-info">
        <h3><FileText size={18} className="mr-2" /> Gemini AI Root-Cause Copilot</h3>
        <span className="badge">AI GENERATED</span>
      </div>
      
      <div className="copilot-body">
        {/* Markdown-style content block */}
        <div className="report-markdown">
          <h4>Incident Summary</h4>
          <p>{report.summary}</p>
          
          <h4 className="mt-4">Root Cause Analysis</h4>
          <p>{report.analysis}</p>
        </div>

        {/* Suggested Diagnostic Commands */}
        {report.suggested_commands && report.suggested_commands.length > 0 && (
          <div className="diagnostic-commands mt-6">
            <h4 className="text-info mb-2 flex items-center">
              <Terminal size={16} className="mr-2" /> Recommended Diagnostic Commands
            </h4>
            
            <div className="commands-list">
              {report.suggested_commands.map((cmd, idx) => (
                <div key={idx} className="command-block">
                  <div className="command-text">
                    <span className="prompt">$</span> {cmd.syntax}
                  </div>
                  <button 
                    className="btn-copy" 
                    onClick={() => handleCopy(cmd.syntax, idx)}
                    title="Copy to Clipboard"
                  >
                    {copiedCmd === idx ? <Check size={16} className="text-healthy" /> : <Copy size={16} />}
                  </button>
                </div>
              ))}
            </div>
            <p className="text-xs text-muted mt-2">
              Run these commands to verify the Bayesian Engine's hypothesis before initiating remediation.
            </p>
          </div>
        )}
      </div>
    </div>
  );
}
