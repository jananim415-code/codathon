document.addEventListener('DOMContentLoaded', async () => {
    const form = document.getElementById('documentForm');
    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        const payload = {
            title: form.title.value,
            content: form.content.value,
            project: form.projectId.value ? { id: Number(form.projectId.value) } : null,
            createdBy: form.createdById.value ? { id: Number(form.createdById.value) } : null
        };
        await apiFetch('/api/documents', { method: 'POST', body: JSON.stringify(payload) });
        form.reset();
        loadDocuments();
    });
    await loadDocuments();
});

async function loadDocuments() {
    const docs = await apiFetch('/api/documents');
    const list = document.getElementById('documentList');
    list.innerHTML = docs.map(doc => `
        <div class="document-item">
            <h4>${doc.title}</h4>
            <div>${doc.content || 'No content yet.'}</div>
        </div>
    `).join('');
}
