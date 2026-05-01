function renderFlashMessage() {
    const flash = document.getElementById('flash-message');
    if (!flash) {
        return;
    }

    const params = new URLSearchParams(window.location.search);
    const message = params.get('message');
    const error = params.get('error');
    const text = error || message || '';
    if (!text) {
        flash.classList.add('hidden');
        return;
    }

    flash.textContent = text;
    flash.className = error ? 'flash error' : 'flash success';
}

async function loadSession() {
    const welcome = document.getElementById('welcome-message');
    const roleBadge = document.getElementById('role-badge');
    const farmerPanel = document.getElementById('farmer-panel');
    const customerPanel = document.getElementById('customer-panel');

    if (!welcome || !roleBadge) {
        return;
    }

    const response = await fetch('api/auth/me', { credentials: 'include' }).catch(() => null);
    if (!response || !response.ok) {
        window.location.href = 'login.html?error=' + encodeURIComponent('Please log in first');
        return;
    }

    const user = await response.json();
    welcome.textContent = `Welcome, ${user.fullName}`;
    roleBadge.textContent = user.role;

    if (farmerPanel && customerPanel) {
        farmerPanel.classList.toggle('hidden', user.role !== 'FARMER');
        customerPanel.classList.toggle('hidden', user.role !== 'CUSTOMER');
    }

    const farmerLink = document.getElementById('farmer-link');
    const customerLink = document.getElementById('customer-link');
    if (farmerLink) {
        farmerLink.href = 'api/protected/farmer';
    }
    if (customerLink) {
        customerLink.href = 'api/protected/customer';
    }
}

async function logout() {
    await fetch('api/auth/logout', {
        method: 'POST',
        credentials: 'include'
    }).catch(() => null);
    window.location.href = 'login.html?message=' + encodeURIComponent('You have been logged out');
}

document.addEventListener('DOMContentLoaded', () => {
    renderFlashMessage();
    if (document.body.dataset.page === 'dashboard') {
        loadSession();
    }
});




