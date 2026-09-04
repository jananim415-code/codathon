const API_TOKEN_KEY = 'projectsphere_token';

async function apiFetch(url, options = {}) {
    const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };
    const token = localStorage.getItem(API_TOKEN_KEY);
    if (token && !headers.Authorization) headers.Authorization = `Bearer ${token}`;
    const response = await fetch(url, { ...options, headers });
    if (!response.ok) {
        let message = `Request failed (${response.status})`;
        try {
            const body = await response.json();
            message = body.message || body.error || message;
        } catch (_) {
            const text = await response.text();
            if (text) message = text;
        }
        const error = new Error(message);
        error.status = response.status;
        throw error;
    }
    if (response.status === 204) return null;
    return response.headers.get('Content-Type')?.includes('application/json') ? response.json() : response.text();
}

function escapeHtml(value) {
    return String(value ?? '').replace(/[&<>"']/g, character => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;'
    }[character]));
}

function notify(message, isError = false) {
    let node = document.getElementById('appNotification');
    if (!node) {
        node = document.createElement('div');
        node.id = 'appNotification';
        node.className = 'notification';
        document.body.appendChild(node);
    }
    node.textContent = message;
    node.classList.toggle('error', isError);
    node.classList.add('visible');
    window.setTimeout(() => node.classList.remove('visible'), 3500);
}

function requireAuthentication() {
    if (!localStorage.getItem(API_TOKEN_KEY)) {
        notify('Sign in to make changes. Read-only data remains available.');
        return false;
    }
    return true;
}
