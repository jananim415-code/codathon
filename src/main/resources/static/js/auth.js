document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('authForm'), name = form.name, github = form.githubUsername, submit = form.querySelector('button');
    let registering = false;
    const setMode = value => { registering = value; name.hidden = github.hidden = !registering; name.required = registering; submit.textContent = registering ? 'Register' : 'Sign in'; };
    document.getElementById('loginTab').onclick = () => setMode(false);
    document.getElementById('registerTab').onclick = () => setMode(true);
    form.addEventListener('submit', async event => {
        event.preventDefault();
        try {
            const payload = { email: form.email.value, password: form.password.value };
            if (registering) { payload.name = name.value; payload.githubUsername = github.value; }
            const response = await apiFetch(`/api/auth/${registering ? 'register' : 'login'}`, { method: 'POST', body: JSON.stringify(payload) });
            localStorage.setItem(API_TOKEN_KEY, response.token); localStorage.setItem('projectsphere_user', JSON.stringify(response.user));
            notify('Authenticated'); setTimeout(() => window.location.href = '/index.html', 500);
        } catch (error) { notify(error.message, true); }
    });
});
