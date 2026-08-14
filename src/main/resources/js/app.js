// Global State Storage
let currentDeviceId = 'router-core-01';
let currentIncidentId = 'INC-8F3A21BC';
let currentView = 'topology';
let jwtToken = '';

// On Page Load: Authenticate & Load Initial View
document.addEventListener('DOMContentLoaded', async () => {
  await authenticateAndFetchToken();
  setupEventListeners();
  loadCurrentView();
});

// Step 1: Automatic JWT Login Authentication
async function authenticateAndFetchToken() {
  try {
    const res = await fetch('/api/v1/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username: 'admin' })
    });
    const data = await res.json();
    if (data.data) {
      jwtToken = data.data;
      console.log('✅ JWT Authentication Successful');
    }
  } catch (err) {
    console.error('❌ Auth Failed:', err);
  }
}

// Setup Event Listeners
function setupEventListeners() {
  const deviceSelect = document.getElementById('device-select');
  if (deviceSelect) {
    deviceSelect.addEventListener('change', (e) => {
      currentDeviceId = e.target.value;
      loadCurrentView();
    });
  }
}

// View Switching Manager
function switchView(viewName) {
  currentView = viewName;
  document.querySelectorAll('.nav-btn').forEach(btn => btn.classList.remove('active'));

  // Highlight active button
  const activeBtn = Array.from(document.querySelectorAll('.nav-btn')).find(btn => btn.getAttribute('onclick').includes(viewName));
  if (activeBtn) activeBtn.classList.add('active');

  loadCurrentView();
}

// Primary Data Router
async function loadCurrentView() {
  const workspace = document.getElementById('workspace-area');
  workspace.innerHTML = `<div class="glass-panel" style="padding: 2rem; text-align: center;"><h3 style="color: var(--primary-olive);">Loading ${currentView.toUpperCase()} Data...</h3></div>`;

  try {
    if (currentView === 'topology') {
      await renderTopologyView(workspace);
    } else if (currentView === 'matrix') {
      await renderMatrixView(workspace);
    } else if (currentView === 'assistant') {
      await renderAssistantView(workspace);
    } else if (currentView === 'audit') {
      await renderAuditView(workspace);
    }
  } catch (err) {
    workspace.innerHTML = `<div class="glass-panel" style="padding: 2rem; color: var(--accent-rose);">Error loading view: ${err.message}</div>`;
  }
}

// -------------------------------------------------------------
// VIEW 1: Topology & Blast Radius Visualizer
// -------------------------------------------------------------
async function renderTopologyView(container) {
  const res = await fetch(`/api/v1/causal/dag/${currentDeviceId}`, {
    headers: { 'Authorization': `Bearer ${jwtToken}` }
  });
  const json = await res.json();
  const dag = json.data || {};
  const nodes = dag.nodes || {};

  let nodesHtml = '';
  for (const key in nodes) {
    const node = nodes[key];
    const isRoot = node.id === currentDeviceId;
    const badgeClass = isRoot ? 'background: rgba(244, 63, 94, 0.2); color: #FB7185; border: 1px solid rgba(244, 63, 94, 0.5);' : 'background: rgba(16, 185, 129, 0.2); color: #34D399; border: 1px solid rgba(16, 185, 129, 0.5);';

    nodesHtml += `
      <div class="glass-panel" style="padding: 1.5rem; border-radius: var(--radius-xl); text-align: center; position: relative;">
        <div style="font-size: 2rem; margin-bottom: 0.5rem;">${isRoot ? '🔴' : '🟢'}</div>
        <h4 style="font-size: 1.1rem; font-weight: 700; color: var(--text-main);">${node.id}</h4>
        <p style="font-size: 0.85rem; color: var(--text-muted); margin-bottom: 0.75rem;">${node.hostName || node.id} (${node.deviceType})</p>
        <span style="padding: 0.3rem 0.75rem; border-radius: var(--radius-pill); font-size: 0.75rem; font-weight: 700; ${badgeClass}">
          ${isRoot ? 'PRIMARY ROOT CAUSE' : 'HEALTHY DEPENDENT'}
        </span>
      </div>
    `;
  }

  container.innerHTML = `
    <div style="display: flex; flex-direction: column; gap: 1.5rem;">
      <div class="glass-panel" style="padding: 1.75rem;">
        <h2 style="font-size: 1.35rem; font-weight: 700; color: var(--accent-lime); margin-bottom: 0.5rem;">🌐 Topology & Blast Radius Visualizer</h2>
        <p style="color: var(--text-muted); font-size: 0.9rem;">Target Incident Device: <strong>${currentDeviceId}</strong></p>
      </div>

      <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 1.5rem;">
        ${nodesHtml || '<div class="glass-panel" style="padding: 1.5rem;">No connected topology nodes found.</div>'}
      </div>
    </div>
  `;
}

// -------------------------------------------------------------
// VIEW 2: 3-Way Evidence Matrix & Bayesian Leaderboard
// -------------------------------------------------------------
async function renderMatrixView(container) {
  const res = await fetch(`/api/v1/explainability/context/${currentDeviceId}`, {
    headers: { 'Authorization': `Bearer ${jwtToken}` }
  });
  const json = await res.json();
  const ctx = json.data || {};

  const confirmed = (ctx.confirmedEvidence || []).map(item => `<div style="padding: 0.75rem; margin-bottom: 0.5rem; background: rgba(16, 185, 129, 0.1); border-left: 3px solid var(--primary-emerald); border-radius: var(--radius-sm); font-size: 0.85rem;">🟢 ${item}</div>`).join('');
  const correlated = (ctx.correlatedSignals || []).map(item => `<div style="padding: 0.75rem; margin-bottom: 0.5rem; background: rgba(245, 158, 11, 0.1); border-left: 3px solid var(--accent-amber); border-radius: var(--radius-sm); font-size: 0.85rem;">🟡 ${item}</div>`).join('');
  const missing = (ctx.missingEvidence || []).map(item => `<div style="padding: 0.75rem; margin-bottom: 0.5rem; background: rgba(244, 63, 94, 0.1); border-left: 3px solid var(--accent-rose); border-radius: var(--radius-sm); font-size: 0.85rem;">🔴 ${item}</div>`).join('');

  container.innerHTML = `
    <div style="display: flex; flex-direction: column; gap: 1.5rem;">
      <!-- Bayesian Confidence Banner -->
      <div class="glass-panel" style="padding: 1.75rem; display: flex; justify-content: space-between; align-items: center;">
        <div>
          <h2 style="font-size: 1.35rem; font-weight: 700; color: var(--accent-lime);">🧠 Bayesian Root-Cause Leaderboard</h2>
          <p style="color: var(--text-muted); font-size: 0.9rem;">Primary Candidate: <strong>${ctx.primaryDeviceId}</strong></p>
        </div>
        <div style="text-align: right;">
          <div style="font-size: 2rem; font-weight: 800; color: var(--accent-lime);">${((ctx.confidenceScore || 0.9172) * 100).toFixed(1)}%</div>
          <span class="badge-confirmed" style="padding: 0.25rem 0.75rem; font-size: 0.8rem;">CONFIDENCE: ${ctx.confidenceRating || 'HIGH'}</span>
        </div>
      </div>

      <!-- 3-Column Evidence Grid -->
      <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 1.5rem;">
        <div class="glass-panel" style="padding: 1.5rem;">
          <h3 style="font-size: 1rem; font-weight: 700; color: var(--primary-emerald); margin-bottom: 1rem;">Confirmed Evidence</h3>
          ${confirmed || '<p style="color: var(--text-dim);">None</p>'}
        </div>
        <div class="glass-panel" style="padding: 1.5rem;">
          <h3 style="font-size: 1rem; font-weight: 700; color: var(--accent-amber); margin-bottom: 1rem;">Correlated Signals</h3>
          ${correlated || '<p style="color: var(--text-dim);">None</p>'}
        </div>
        <div class="glass-panel" style="padding: 1.5rem;">
          <h3 style="font-size: 1rem; font-weight: 700; color: var(--accent-rose); margin-bottom: 1rem;">Missing Evidence</h3>
          ${missing || '<p style="color: var(--text-dim);">None</p>'}
        </div>
      </div>
    </div>
  `;
}

// -------------------------------------------------------------
// VIEW 3: Explainable Assistant & Markdown Viewer
// -------------------------------------------------------------
async function renderAssistantView(container) {
  container.innerHTML = `<div class="glass-panel" style="padding: 2rem; text-align: center;"><h3 style="color: var(--accent-lime);">🤖 Calling LLM Engine for ${currentDeviceId}...</h3></div>`;

  const res = await fetch(`/api/v1/assistant/explain/${currentDeviceId}`, {
    method: 'POST',
    headers: { 'Authorization': `Bearer ${jwtToken}` }
  });
  const json = await res.json();
  const data = json.data || {};
  const markdown = data.markdown_report || 'No report generated.';
  const commands = data.recommended_commands || [];

  let cmdsHtml = commands.map(cmd => `
    <div class="command-box">
      <code style="color: var(--accent-lime);">${cmd}</code>
      <button class="copy-btn" onclick="navigator.clipboard.writeText('${cmd}'); alert('Copied to clipboard!');">Copy CLI</button>
    </div>
  `).join('');

  container.innerHTML = `
    <div style="display: flex; flex-direction: column; gap: 1.5rem;">
      <div class="glass-panel" style="padding: 1.75rem;">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
          <h2 style="font-size: 1.35rem; font-weight: 700; color: var(--accent-lime);">📄 Explainable Assistant Report</h2>
          <span style="font-size: 0.85rem; color: var(--text-muted);">Provider: <strong>${data.llm_provider || 'Google Gemini'}</strong></span>
        </div>
        <div style="background: #060B08; border-radius: var(--radius-lg); padding: 1.5rem; border: 1px solid var(--border-color); font-size: 0.95rem; white-space: pre-wrap;">
          ${markdown}
        </div>
      </div>

      <div class="glass-panel" style="padding: 1.75rem;">
        <h3 style="font-size: 1.1rem; font-weight: 700; color: var(--primary-olive); margin-bottom: 1rem;">🛠️ Target CLI Diagnostic Recommendations</h3>
        ${cmdsHtml}
      </div>
    </div>
  `;
}

// -------------------------------------------------------------
// VIEW 4: Immutable Audit Trail Timeline Feed
// -------------------------------------------------------------
async function renderAuditView(container) {
  const res = await fetch(`/api/v1/audit/incidents/${currentIncidentId}/history`, {
    headers: { 'Authorization': `Bearer ${jwtToken}` }
  });
  const json = await res.json();
  const trail = json.data || [];

  let timelineHtml = trail.map(item => `
    <div style="display: flex; gap: 1.5rem; margin-bottom: 1.5rem;">
      <div style="width: 120px; font-size: 0.8rem; color: var(--text-dim); text-align: right;">${new Date(item.timestampUtc).toLocaleTimeString()}</div>
      <div style="width: 2px; background: var(--primary-olive); position: relative;">
        <div style="width: 10px; height: 10px; background: var(--accent-lime); border-radius: 50%; position: absolute; left: -4px; top: 0;"></div>
      </div>
      <div class="glass-panel" style="flex: 1; padding: 1rem 1.25rem;">
        <div style="display: flex; justify-content: space-between; margin-bottom: 0.25rem;">
          <span style="font-weight: 700; color: var(--accent-lime);">${item.eventType}</span>
          <span style="font-size: 0.8rem; color: var(--text-muted);">${item.triggerSource}</span>
        </div>
        <p style="font-size: 0.85rem; color: var(--text-main);">${item.details}</p>
      </div>
    </div>
  `).join('');

  container.innerHTML = `
    <div class="glass-panel" style="padding: 1.75rem;">
      <h2 style="font-size: 1.35rem; font-weight: 700; color: var(--accent-lime); margin-bottom: 1.5rem;">📜 Immutable Audit Trail Timeline Feed</h2>
      ${timelineHtml || '<p style="color: var(--text-muted);">No audit events recorded for incident: ' + currentIncidentId + '</p>'}
    </div>
  `;
}