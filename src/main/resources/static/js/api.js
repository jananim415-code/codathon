async function apiFetch(url, options = {}) {
    const response = await fetch(url, {
        headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
        ...options
    });
    if (!response.ok) {
        const error = await response.text();
        throw new Error(error || 'Request failed');
    }
    return response.headers.get('Content-Type')?.includes('application/json') ? response.json() : response.text();
}
