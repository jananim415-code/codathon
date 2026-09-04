document.addEventListener('DOMContentLoaded', async () => {
    const form = document.getElementById('userForm');
    form.addEventListener('submit', async event => {
        event.preventDefault();
        if (!requireAuthentication()) return;
        try {
            await apiFetch('/api/users', { method: 'POST', body: JSON.stringify({
                name: form.name.value, email: form.email.value, role: form.role.value,
                githubUsername: form.githubUsername.value
            })});
            form.reset(); notify('User created'); await loadUsers();
        } catch (error) { notify(error.message, true); }
    });
    await loadUsers();
});
async function loadUsers() {
    try {
        const users = await apiFetch('/api/users');
        const list = document.getElementById('userList');
        list.innerHTML = users.length ? users.map(user => `<div class="user-card"><h4>${escapeHtml(user.name)}</h4><p>${escapeHtml(user.email)}</p><div>${escapeHtml(user.role || 'STUDENT')}</div><div>Teams: ${(user.teams || []).length}</div><button class="danger-btn" data-delete-user="${user.id}">Delete</button></div>`).join('') : '<p class="empty-state">No users yet.</p>';
        list.querySelectorAll('[data-delete-user]').forEach(button => button.addEventListener('click', async () => {
            if (!requireAuthentication() || !confirm('Delete this user? Their tasks will be unassigned.')) return;
            try { await apiFetch(`/api/users/${button.dataset.deleteUser}`, { method: 'DELETE' }); notify('User deleted'); await loadUsers(); } catch (error) { notify(error.message, true); }
        }));
    } catch (error) { notify(error.message, true); }
}
