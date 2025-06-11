<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
  <title>Workspace Management</title>
  <style>
    .workspace-container { margin: 20px; }
    .workspace-card { border: 1px solid #ccc; padding: 15px; margin-bottom: 10px; }
    .hidden { display: none; }
    .channel-list { margin-left: 20px; }
  </style>
</head>
<body>
<div class="workspace-container">
  <h1>My Workspaces</h1>

  <!-- Create New Workspace Form -->
  <div class="workspace-card">
    <h3>Create New Workspace</h3>
    <input type="text" id="newWorkspaceName" placeholder="Workspace name">
    <textarea id="newWorkspaceDesc" placeholder="Description"></textarea>
    <button onclick="createWorkspace()">Create</button>
  </div>

  <c:forEach var="espace" items="${espaces}">
    <div class="workspace-card" id="workspace-${espace.nom}">
      <h2>${espace.nom}</h2>
      <p>${espace.description}</p>

      <!-- Workspace Actions -->
      <div>
        <button onclick="showChannelForm('${espace.nom}')">Add Channel</button>
        <button onclick="generateWorkspaceInvite('${espace.nom}')">Generate Invite Link</button>
      </div>

      <!-- Channel Creation Form (Hidden by default) -->
      <div id="channel-form-${espace.nom}" class="hidden">
        <input type="text" id="newChannelName-${espace.nom}" placeholder="Channel name">
        <select id="newChannelStatus-${espace.nom}">
          <option value="public">Public</option>
          <option value="private">Private</option>
        </select>
        <button onclick="createChannel('${espace.nom}')">Create</button>
      </div>

      <!-- Channel List -->
      <div class="channel-list">
        <h4>Channels:</h4>
        <ul id="channels-${espace.nom}">
          <c:forEach var="canal" items="${espace.canaux}">
            <li>
                ${canal.nom} (${canal.statut})
              <button onclick="generateChannelInvite('${espace.nom}', '${canal.nom}')">Invite</button>
            </li>
          </c:forEach>
        </ul>
      </div>
    </div>
  </c:forEach>
</div>

<script>
  // Create new workspace
  function createWorkspace() {
    const name = document.getElementById('newWorkspaceName').value;
    const desc = document.getElementById('newWorkspaceDesc').value;

    fetch('/api/workspace/create', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name, description: desc })
    })
            .then(response => response.json())
            .then(data => {
              if (data.success) {
                location.reload(); // Refresh to show new workspace
              } else {
                alert('Error: ' + data.message);
              }
            });
  }

  // Show channel creation form for a workspace
  function showChannelForm(workspaceName) {
    document.getElementById(`channel-form-${workspaceName}`).classList.remove('hidden');
  }

  // Create new channel in a workspace
  function createChannel(workspaceName) {
    const channelName = document.getElementById(`newChannelName-${workspaceName}`).value;
    const status = document.getElementById(`newChannelStatus-${workspaceName}`).value;

    fetch('/api/channel/create', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        workspaceName,
        channelName,
        status
      })
    })
            .then(response => response.json())
            .then(data => {
              if (data.success) {
                // Add new channel to the list without page reload
                const channelList = document.getElementById(`channels-${workspaceName}`);
                const newItem = document.createElement('li');
                newItem.innerHTML = `
                        ${channelName} (${status})
                        <button onclick="generateChannelInvite('${workspaceName}', '${channelName}')">Invite</button>
                    `;
                channelList.appendChild(newItem);

                // Hide the form and clear inputs
                document.getElementById(`channel-form-${workspaceName}`).classList.add('hidden');
                document.getElementById(`newChannelName-${workspaceName}`).value = '';
              } else {
                alert('Error: ' + data.message);
              }
            });
  }

  // Generate workspace invite link
  function generateWorkspaceInvite(workspaceName) {
    fetch('/api/invite/workspace', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ workspaceName })
    })
            .then(response => response.json())
            .then(data => {
              if (data.success) {
                prompt('Share this invite link:', data.inviteLink);
              } else {
                alert('Error: ' + data.message);
              }
            });
  }

  // Generate channel invite link
  function generateChannelInvite(workspaceName, channelName) {
    fetch('/api/invite/channel', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ workspaceName, channelName })
    })
            .then(response => response.json())
            .then(data => {
              if (data.success) {
                prompt('Share this channel invite link:', data.inviteLink);
              } else {
                alert('Error: ' + data.message);
              }
            });
  }
</script>
</body>
</html>