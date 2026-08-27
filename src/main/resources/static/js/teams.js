document.addEventListener('DOMContentLoaded', async () => {
    const form = document.getElementById('teamForm');
    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        const payload = {
            name: form.name.value,
            description: form.description.value
        };
        await apiFetch('/api/teams', { method: 'POST', body: JSON.stringify(payload) });
        form.reset();
        loadTeams();
    });
    await loadTeams();
});

async function loadTeams() {
    const teams = await apiFetch('/api/teams');
    const list = document.getElementById('teamList');
    list.innerHTML = teams.map(team => `
        <div class="team-card">
            <h4>${team.name}</h4>
            <p>${team.description || 'No description yet.'}</p>
            <div>Members: ${team.members ? team.members.length : 0}</div>
            <div>Projects: ${team.projects ? team.projects.length : 0}</div>
        </div>
    `).join('');
}
