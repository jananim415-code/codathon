document.addEventListener('DOMContentLoaded', async () => {
    try {
        const data = await apiFetch('/api/dashboard');
        const cards = [
            { label: 'Total Teams', value: data.totalTeams ?? 0 },
            { label: 'Total Projects', value: data.totalProjects ?? 0 },
            { label: 'Active Projects', value: data.activeProjects ?? 0 },
            { label: 'At-Risk Projects', value: data.atRiskProjects ?? 0 },
            { label: 'Total Team Members', value: data.totalTeamMembers ?? 0 },
            { label: 'Overall Task Completion', value: `${data.overallTaskCompletion ?? 0}%` }
        ];
        const statsGrid = document.getElementById('statsGrid');
        statsGrid.innerHTML = cards.map(card => `
            <div class="stat-card panel">
                <span>${card.label}</span>
                <strong>${card.value}</strong>
            </div>
        `).join('');

        const projects = await apiFetch('/api/projects');
        const primaryProject = projects[0];
        const [contributions, freeRiders, activity] = primaryProject
            ? await Promise.all([
                apiFetch(`/api/intelligence/projects/${primaryProject.id}/contributions`),
                apiFetch(`/api/intelligence/projects/${primaryProject.id}/free-riders`),
                apiFetch(`/api/projects/${primaryProject.id}/github/activity`)
            ])
            : [[], [], []];

        const healthList = document.getElementById('projectHealthList');
        const healthData = data.projectsHealth || [];
        healthList.innerHTML = healthData.map(item => `
            <div class="health-item">
                <strong>${escapeHtml(item.name)}</strong>
                <div>Health: ${Math.round(item.health || 0)}/100 <span class="badge ${(item.status === 'HEALTHY' ? 'healthy' : item.status === 'AT_RISK' ? 'warning' : 'danger')}">${escapeHtml(item.status)}</span></div>
            </div>
        `).join('');

        const flaggedNames = new Set(freeRiders.map(item => item.user));
        const teamRows = contributions.map(row => ({
            ...row,
            tasks: `${Math.round(row.taskCompletion)}%`,
            status: flaggedNames.has(row.user) ? 'Potential Low Contribution'
                : row.score >= 80 ? 'Strong' : row.score >= 60 ? 'Stable' : 'Needs Attention'
        }));
        document.getElementById('teamContributionTable').innerHTML = teamRows.map(row => `
            <tr>
                <td>${escapeHtml(row.user)}</td>
                <td>${row.commits}</td>
                <td>${row.prs}</td>
                <td>${row.tasks}</td>
                <td>${row.score}</td>
                <td>${row.status}</td>
            </tr>
        `).join('');

        const alerts = document.getElementById('freeRiderAlerts');
        alerts.innerHTML = freeRiders.length
            ? freeRiders.map(item => `<div class="alert-item">${escapeHtml(item.explanation)}</div>`).join('')
            : '<div class="alert-item">No potential low-contribution members detected.</div>';

        const activities = document.getElementById('recentActivity');
        activities.innerHTML = activity.length ? activity.map(item => `<li>${escapeHtml(item)}</li>`).join('') : '<li>No recent activity.</li>';

        document.getElementById('analyzeAllBtn').addEventListener('click', async () => {
            if (!requireAuthentication()) return;
            try {
                const result = await apiFetch('/api/intelligence/analyze-all', { method: 'POST' });
                notify(result.message || 'Analysis complete'); window.location.reload();
            } catch (error) { notify(error.message, true); }
        });
    } catch (error) {
        notify(error.message || 'Unable to load dashboard', true);
    }
});
