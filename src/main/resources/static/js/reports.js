document.addEventListener('DOMContentLoaded', () => {
    const projectLink = document.getElementById('projectReportLink');
    const userLink = document.getElementById('userReportLink');
    Promise.all([apiFetch('/api/projects'), apiFetch('/api/users')]).then(([projects, users]) => {
        const projectSelect = document.getElementById('projectReportSelect');
        const userSelect = document.getElementById('userReportSelect');
        projectSelect.innerHTML = '<option value="">Select project</option>' +
            projects.map(project => `<option value="${project.id}">${escapeHtml(project.name)}</option>`).join('');
        userSelect.innerHTML = '<option value="">Select user</option>' +
            users.map(user => `<option value="${user.id}">${escapeHtml(user.name)}</option>`).join('');
        const updateLinks = () => {
            const projectId = projectSelect.value;
            const userId = userSelect.value;
            projectLink.href = projectId ? `/api/reports/project/${projectId}` : '#';
            userLink.href = projectId && userId ? `/api/reports/project/${projectId}/user/${userId}` : '#';
            projectLink.classList.toggle('disabled', !projectId);
            userLink.classList.toggle('disabled', !(projectId && userId));
        };
        projectSelect.addEventListener('change', updateLinks);
        userSelect.addEventListener('change', updateLinks);
    }).catch(error => notify(error.message, true));
});
