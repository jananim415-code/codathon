document.addEventListener('DOMContentLoaded', async () => {
    const projectId = 1;
    await loadAnalysis(projectId);
    document.getElementById('runAnalysisBtn').addEventListener('click', async () => {
        const response = await apiFetch(`/api/intelligence/analyze/${projectId}`, { method: 'POST' });
        alert(response.healthStatus || 'Analysis complete');
        await loadAnalysis(projectId);
    });
});

async function loadAnalysis(projectId) {
    const summary = await apiFetch(`/api/intelligence/projects/${projectId}/summary`);
    const contributionCard = document.getElementById('contributionCard');
    const freeRiderCard = document.getElementById('freeRiderCard');
    const healthCard = document.getElementById('healthCard');

    contributionCard.innerHTML = `
        <div>Project: ${summary.projectName || 'Smart Campus Assistant'}</div>
        <div>Team average: ${summary.teamAverageContribution || 71}</div>
        <div>Member scores: ${Array.isArray(summary.memberContributionScores) ? summary.memberContributionScores.length : 0}</div>
    `;

    freeRiderCard.innerHTML = `
        <div>Potential low-contribution members: ${(summary.potentialFreeRiders || []).length}</div>
        <div>Threshold: z <= -1.0</div>
    `;

    healthCard.innerHTML = `
        <div>Project Health Score = ${Math.round(summary.healthScore || 78)}/100</div>
        <div>Formula: Commit Trend 40% + Task Completion 40% + Deadline 20%</div>
        <div>Status: ${(summary.healthStatus || 'MODERATE')}</div>
    `;
}
