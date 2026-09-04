document.addEventListener('DOMContentLoaded', async () => {
    const select = document.getElementById('projectSelect');
    try {
        const projects = await apiFetch('/api/projects');
        select.innerHTML = '<option value="">Select project</option>' + projects.map(project => `<option value="${project.id}">${escapeHtml(project.name)}</option>`).join('');
        if (projects.length) { select.value = projects[0].id; await loadAnalysis(projects[0].id); }
    } catch (error) { notify(error.message, true); }
    select.addEventListener('change', () => select.value && loadAnalysis(select.value).catch(error => notify(error.message, true)));
    document.getElementById('runAnalysisBtn').addEventListener('click', async () => {
        if (!requireAuthentication() || !select.value) return;
        try {
            await apiFetch(`/api/intelligence/analyze/${select.value}`, { method: 'POST' });
            notify('Analysis complete'); await loadAnalysis(select.value);
        } catch (error) { notify(error.message, true); }
    });
});

async function loadAnalysis(projectId) {
    const summary = await apiFetch(`/api/intelligence/projects/${projectId}/summary`);
    const contributionCard = document.getElementById('contributionCard');
    const freeRiderCard = document.getElementById('freeRiderCard');
    const healthCard = document.getElementById('healthCard');

    contributionCard.innerHTML = `
        <div>Project: ${escapeHtml(summary.projectName || 'Unknown project')}</div>
        <div>Team average: ${Math.round(summary.teamAverageContribution || 0)}</div>
        <div>Member scores: ${Array.isArray(summary.memberContributionScores) ? summary.memberContributionScores.length : 0}</div>
    `;

    freeRiderCard.innerHTML = `
        <div>Potential low-contribution members: ${(summary.potentialFreeRiders || []).length}</div>
        <div>Threshold: z <= -1.0</div>
    `;

    healthCard.innerHTML = `
        <div>Project Health Score = ${Math.round(summary.healthScore || 0)}/100</div>
        <div>Formula: Commit Trend 40% + Task Completion 40% + Deadline 20%</div>
        <div>Status: ${escapeHtml(summary.healthStatus || 'NOT_ANALYZED')}</div>
    `;
}
