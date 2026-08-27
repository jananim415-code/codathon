document.addEventListener('DOMContentLoaded', async () => {
    const form = document.getElementById('projectForm');
    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        const payload = {
            name: form.name.value,
            description: form.description.value,
            deadline: form.deadline.value,
            githubRepositoryUrl: form.githubRepositoryUrl.value,
            status: form.status.value
        };
        await apiFetch('/api/projects', { method: 'POST', body: JSON.stringify(payload) });
        form.reset();
        loadProjects();
    });
    await loadProjects();
});

async function loadProjects() {
    const projects = await apiFetch('/api/projects');
    const list = document.getElementById('projectList');
    list.innerHTML = projects.map(project => `
        <div class="project-card">
            <h4>${project.name}</h4>
            <p>${project.description || 'No description provided.'}</p>
            <div>Status: ${project.status}</div>
            <div>Deadline: ${project.deadline || 'N/A'}</div>
            <div>GitHub: ${project.githubRepositoryUrl || 'Not set'}</div>
        </div>
    `).join('');
}
