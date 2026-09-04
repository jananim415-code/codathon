document.addEventListener('DOMContentLoaded', async () => {
    const form = document.getElementById('teamForm');
    const membershipForm = document.getElementById('membershipForm');
    try {
        const users = await apiFetch('/api/users');
        document.getElementById('membershipUser').innerHTML = '<option value="">Select user</option>' +
            users.map(user => `<option value="${user.id}">${escapeHtml(user.name)}</option>`).join('');
    } catch (error) { notify(error.message, true); }
    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        const payload = {
            name: form.name.value,
            description: form.description.value
        };
        if (!requireAuthentication()) return;
        try { await apiFetch('/api/teams', { method: 'POST', body: JSON.stringify(payload) }); form.reset(); notify('Team created'); await loadTeams(); }
        catch (error) { notify(error.message, true); }
    });
    membershipForm.addEventListener('submit', async event => {
        event.preventDefault();
        if (!requireAuthentication() || !membershipForm.teamId.value || !membershipForm.userId.value) return;
        try {
            await apiFetch(`/api/teams/${membershipForm.teamId.value}/members/${membershipForm.userId.value}`, { method: 'POST' });
            notify('Member added'); await loadTeams();
        } catch (error) { notify(error.message, true); }
    });
    await loadTeams();
});

async function loadTeams() {
    const teams = await apiFetch('/api/teams');
    const list = document.getElementById('teamList');
    const teamSelect = document.getElementById('membershipTeam');
    const selected = teamSelect.value;
    teamSelect.innerHTML = '<option value="">Select team</option>' +
        teams.map(team => `<option value="${team.id}">${escapeHtml(team.name)}</option>`).join('');
    teamSelect.value = selected;
    list.innerHTML = teams.length ? teams.map(team => `
        <div class="team-card">
            <h4>${escapeHtml(team.name)}</h4>
            <p>${escapeHtml(team.description || 'No description yet.')}</p>
            <div>Members: ${team.members ? team.members.length : 0}</div>
            <div>Projects: ${team.projects ? team.projects.length : 0}</div>
            <details><summary>Members</summary><div>${(team.members || []).map(member => `<span class="chip">${escapeHtml(member.name)} <button type="button" class="danger-btn" data-remove-team="${team.id}" data-remove-user="${member.id}">×</button></span>`).join('') || 'No members'}</div></details>
            <button class="danger-btn" data-delete-team="${team.id}">Delete</button>
        </div>
    `).join('') : '<p class="empty-state">No teams yet.</p>';
    list.querySelectorAll('[data-delete-team]').forEach(button => button.addEventListener('click', async () => {
        if (!requireAuthentication() || !confirm('Delete this team? Projects will remain without a team.')) return;
        try { await apiFetch(`/api/teams/${button.dataset.deleteTeam}`, { method: 'DELETE' }); notify('Team deleted'); await loadTeams(); }
        catch (error) { notify(error.message, true); }
    }));
    list.querySelectorAll('[data-remove-team]').forEach(button => button.addEventListener('click', async () => {
        if (!requireAuthentication()) return;
        try { await apiFetch(`/api/teams/${button.dataset.removeTeam}/members/${button.dataset.removeUser}`, { method: 'DELETE' }); notify('Member removed'); await loadTeams(); }
        catch (error) { notify(error.message, true); }
    }));
}
