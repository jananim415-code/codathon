document.addEventListener('DOMContentLoaded', async () => {
    const form = document.getElementById('projectForm');
    try {
        const teams = await apiFetch('/api/teams');
        document.getElementById('teamSelect').innerHTML = '<option value="">No team</option>' +
            teams.map(team => `<option value="${team.id}">${escapeHtml(team.name)}</option>`).join('');
    } catch (error) { notify(error.message, true); }
    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        if (!requireAuthentication()) return;
        const payload = {
            name: form.name.value,
            description: form.description.value,
            deadline: form.deadline.value,
            githubRepositoryUrl: form.githubRepositoryUrl.value,
            status: form.status.value,
            teamId: form.teamId.value ? Number(form.teamId.value) : null
        };
        try {
            await apiFetch('/api/projects', { method: 'POST', body: JSON.stringify(payload) });
            form.reset(); notify('Project created'); await loadProjects();
        } catch (error) { notify(error.message, true); }
    });
    await loadProjects();
});

async function loadProjects() {
    const projects = await apiFetch('/api/projects');
    const list = document.getElementById('projectList');
    list.innerHTML = projects.length ? projects.map(project => `
        <div class="project-card">
            <h4>${escapeHtml(project.name)}</h4>
            <p>${escapeHtml(project.description || 'No description provided.')}</p>
            <div>Status: <span class="badge">${escapeHtml(project.status)}</span></div>
            <div>Deadline: ${escapeHtml(project.deadline || 'N/A')}</div>
            <div>Team: ${escapeHtml(project.team?.name || 'No team')}</div>
            ${project.githubRepositoryUrl ? `<a href="${escapeHtml(project.githubRepositoryUrl)}" target="_blank" rel="noopener">GitHub ↗</a>` : ''}
            <button class="danger-btn" data-delete-project="${project.id}">Delete</button>
        </div>
    `).join('') : '<p class="empty-state">No projects yet.</p>';
    list.querySelectorAll('[data-delete-project]').forEach(button => button.addEventListener('click', async () => {
        if (!requireAuthentication() || !confirm('Delete this project and its tasks/documents?')) return;
        try { await apiFetch(`/api/projects/${button.dataset.deleteProject}`, { method: 'DELETE' }); notify('Project deleted'); await loadProjects(); }
        catch (error) { notify(error.message, true); }
    }));
}
