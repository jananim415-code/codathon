document.addEventListener('DOMContentLoaded', async () => {
    const form = document.getElementById('taskForm');
    try {
        const [projects, users] = await Promise.all([apiFetch('/api/projects'), apiFetch('/api/users')]);
        document.getElementById('projectSelect').innerHTML = '<option value="">No project</option>' +
            projects.map(item => `<option value="${item.id}">${escapeHtml(item.name)}</option>`).join('');
        document.getElementById('userSelect').innerHTML = '<option value="">Unassigned</option>' +
            users.map(item => `<option value="${item.id}">${escapeHtml(item.name)}</option>`).join('');
    } catch (error) { notify(error.message, true); }
    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        if (!requireAuthentication()) return;
        const payload = {
            title: form.title.value,
            description: form.description.value,
            status: form.status.value,
            priority: form.priority.value,
            dueDate: form.dueDate.value,
            projectId: form.projectId.value ? Number(form.projectId.value) : null,
            assignedUserId: form.assignedUserId.value ? Number(form.assignedUserId.value) : null
        };
        try { await apiFetch('/api/tasks', { method: 'POST', body: JSON.stringify(payload) }); form.reset(); notify('Task created'); await loadTasks(); }
        catch (error) { notify(error.message, true); }
    });
    await loadTasks();
});

async function loadTasks() {
    const tasks = await apiFetch('/api/tasks');
    const board = document.getElementById('taskList');
    board.innerHTML = tasks.length ? tasks.map(task => `
        <div class="task-card">
            <h4>${escapeHtml(task.title)}</h4>
            <p>${escapeHtml(task.description || '')}</p>
            <div>Status: <select data-status-task="${task.id}">
                ${['TODO', 'IN_PROGRESS', 'COMPLETED'].map(status => `<option value="${status}" ${task.status === status ? 'selected' : ''}>${status}</option>`).join('')}
            </select></div>
            <div>Priority: ${escapeHtml(task.priority)}</div>
            <div>Due: ${escapeHtml(task.dueDate || 'N/A')}</div>
            <div>Assigned: ${escapeHtml(task.assignedUser?.name || 'Unassigned')}</div>
            <button class="danger-btn" data-delete-task="${task.id}">Delete</button>
        </div>
    `).join('') : '<p class="empty-state">No tasks yet.</p>';
    board.querySelectorAll('[data-delete-task]').forEach(button => button.addEventListener('click', async () => {
        if (!requireAuthentication() || !confirm('Delete this task?')) return;
        try { await apiFetch(`/api/tasks/${button.dataset.deleteTask}`, { method: 'DELETE' }); notify('Task deleted'); await loadTasks(); }
        catch (error) { notify(error.message, true); }
    }));
    board.querySelectorAll('[data-status-task]').forEach(select => select.addEventListener('change', async () => {
        if (!requireAuthentication()) return;
        const task = tasks.find(item => String(item.id) === String(select.dataset.statusTask));
        try {
            await apiFetch(`/api/tasks/${task.id}`, { method: 'PUT', body: JSON.stringify({
                title: task.title, description: task.description, status: select.value,
                priority: task.priority, dueDate: task.dueDate,
                assignedUserId: task.assignedUser?.id || null, projectId: task.project?.id || null
            })});
            notify('Task updated'); await loadTasks();
        } catch (error) { notify(error.message, true); }
    }));
}
