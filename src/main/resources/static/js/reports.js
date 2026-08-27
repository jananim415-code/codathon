document.addEventListener('DOMContentLoaded', () => {
    const projectLink = document.getElementById('projectReportLink');
    const userLink = document.getElementById('userReportLink');
    if (projectLink) projectLink.href = '/api/reports/project/1';
    if (userLink) userLink.href = '/api/reports/project/1/user/1';
});
