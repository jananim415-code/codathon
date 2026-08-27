document.addEventListener('DOMContentLoaded', async () => {
    try {
        const data = await apiFetch('/api/dashboard');
        const cards = [
            { label: 'Total Teams', value: data.totalTeams ?? 1 },
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

        const healthList = document.getElementById('projectHealthList');
        const healthData = [{ name: 'Smart Campus Assistant', health: 84, status: 'HEALTHY' }, { name: 'Project Beta', health: 52, status: 'AT_RISK' }];
        healthList.innerHTML = healthData.map(item => `
            <div class="health-item">
                <strong>${item.name}</strong>
                <div>Health: ${item.health}/100 <span class="badge ${(item.status === 'HEALTHY' ? 'healthy' : item.status === 'AT_RISK' ? 'warning' : 'danger')}">${item.status}</span></div>
            </div>
        `).join('');

        const teamRows = [
            { name: 'Aisha', commits: 32, prs: 6, tasks: '90%', score: 91, status: 'Strong' },
            { name: 'Rahul', commits: 25, prs: 4, tasks: '80%', score: 76, status: 'Good' },
            { name: 'Priya', commits: 18, prs: 3, tasks: '70%', score: 68, status: 'Stable' },
            { name: 'Arjun', commits: 3, prs: 0, tasks: '20%', score: 24, status: 'Potential Low Contribution' }
        ];
        document.getElementById('teamContributionTable').innerHTML = teamRows.map(row => `
            <tr>
                <td>${row.name}</td>
                <td>${row.commits}</td>
                <td>${row.prs}</td>
                <td>${row.tasks}</td>
                <td>${row.score}</td>
                <td>${row.status}</td>
            </tr>
        `).join('');

        const alerts = document.getElementById('freeRiderAlerts');
        alerts.innerHTML = '<div class="alert-item">Arjun has low contribution versus team average. Potential low-contribution member.</div>';

        const activities = document.getElementById('recentActivity');
        const activityItems = [
            'Aisha pushed updates to Smart Campus Assistant',
            'Rahul reviewed documentation updates',
            'Priya completed dashboard work',
            'Demo GitHub Data'
        ];
        activities.innerHTML = activityItems.map(item => `<li>${item}</li>`).join('');

        document.getElementById('analyzeAllBtn').addEventListener('click', async () => {
            const result = await apiFetch('/api/intelligence/analyze-all', { method: 'POST' });
            alert(result.message || 'Analysis complete');
            window.location.reload();
        });
    } catch (error) {
        console.error(error);
    }
});
