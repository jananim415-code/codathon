document.addEventListener('DOMContentLoaded', async () => {
    const form = document.getElementById('taskForm');
    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        const payload = {
            title: form.title.value,
            description: form.description.value,
            status: form.status.value,
            priority: form.priority.value,
            dueDate: form.dueDate.value,
            project: form.projectId.value ? { id: Number(form.projectId.value) } : null,
            assignedUser: form.assignedUserId.value ? { id: Number(form.assignedUserId.value) } : null
        };
        await apiFetch('/api/tasks', { method: 'POST', body: JSON.stringify(payload) });
        form.reset();
        loadTasks();
    });
    await loadTasks();
});

async function loadTasks() {
    const tasks = await apiFetch('/api/tasks');
    const board = document.getElementById('taskList');
    board.innerHTML = tasks.map(task => `
        <div class="task-card">
            <h4>${task.title}</h4>
            <div>Status: ${task.status}</div>
            <div>Priority: ${task.priority}</div>
            <div>Due: ${task.dueDate || 'N/A'}</div>
        </div>
    `).join('');
}
