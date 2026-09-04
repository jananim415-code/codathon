document.addEventListener('DOMContentLoaded', async () => {
    const form = document.getElementById('documentForm');
    try {
        const [projects, users] = await Promise.all([apiFetch('/api/projects'), apiFetch('/api/users')]);
        document.getElementById('projectSelect').innerHTML = '<option value="">Select project</option>' +
            projects.map(item => `<option value="${item.id}">${escapeHtml(item.name)}</option>`).join('');
        document.getElementById('userSelect').innerHTML = '<option value="">Select author</option>' +
            users.map(item => `<option value="${item.id}">${escapeHtml(item.name)}</option>`).join('');
    } catch (error) { notify(error.message, true); }
    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        if (!requireAuthentication()) return;
        const payload = {
            title: form.title.value,
            content: form.content.value,
            projectId: Number(form.projectId.value),
            createdById: Number(form.createdById.value)
        };
        try { await apiFetch('/api/documents', { method: 'POST', body: JSON.stringify(payload) }); form.reset(); notify('Document saved'); await loadDocuments(); }
        catch (error) { notify(error.message, true); }
    });
    await loadDocuments();
});

async function loadDocuments() {
    const docs = await apiFetch('/api/documents');
    const list = document.getElementById('documentList');
    list.innerHTML = docs.length ? docs.map(doc => `
        <div class="document-item">
            <h4>${escapeHtml(doc.title)}</h4>
            <small>${escapeHtml(doc.project?.name || 'No project')} · ${escapeHtml(doc.createdBy?.name || 'Unknown author')}</small>
            <div>${escapeHtml(doc.content || 'No content yet.')}</div>
            <button class="danger-btn" data-delete-document="${doc.id}">Delete</button>
        </div>
    `).join('') : '<p class="empty-state">No documents yet.</p>';
    list.querySelectorAll('[data-delete-document]').forEach(button => button.addEventListener('click', async () => {
        if (!requireAuthentication() || !confirm('Delete this document?')) return;
        try { await apiFetch(`/api/documents/${button.dataset.deleteDocument}`, { method: 'DELETE' }); notify('Document deleted'); await loadDocuments(); }
        catch (error) { notify(error.message, true); }
    }));
}
