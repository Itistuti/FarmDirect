let currentProducts = [];
let cart = [];
let currentCategory = '';
let currentSearch = '';

const API_BASE = (document.querySelector('base')?.getAttribute('href') || '/').replace(/\/$/, '');
const CATEGORIES = ['Vegetables', 'Fruits', 'Dairy', 'Other'];

document.addEventListener('DOMContentLoaded', () => {
    if (document.body.dataset.page !== 'customer-dashboard') {
        return;
    }
    loadCart();
    bindCategoryButtons();
    bindSearch();
    bindModalOverlayClose();
    fetchProducts();
});

function bindSearch() {
    const searchInput = document.getElementById('search-input');
    if (!searchInput) {
        return;
    }
    searchInput.addEventListener('input', (e) => {
        currentSearch = e.target.value.trim();
        fetchProducts();
    });
}

function bindCategoryButtons() {
    const list = document.getElementById('category-list');
    if (!list) {
        return;
    }
    list.innerHTML = `
        <li data-category="" class="active">All Products</li>
        ${CATEGORIES.map((category) => `<li data-category="${category}">${category}</li>`).join('')}
    `;
    list.querySelectorAll('li').forEach((item) => {
        item.addEventListener('click', () => filterCategory(item.dataset.category || ''));
    });
}

function bindModalOverlayClose() {
    document.querySelectorAll('.modal-overlay').forEach((modal) => {
        modal.addEventListener('click', (event) => {
            if (event.target === modal) {
                modal.classList.remove('active');
            }
        });
    });
}

async function fetchProducts() {
    try {
        const res = await fetch(`${API_BASE}/api/customer/products/`);
        if (!res.ok) {
            showFlash('Failed to load products', 'error');
            return;
        }
        const data = await res.json();
        currentProducts = (data.products || []).filter((product) => applyProductFilters(product));
        renderProducts();
    } catch (err) {
        showFlash('Network error loading products', 'error');
    }
}

function applyProductFilters(product) {
    if (currentCategory) {
        const mapped = mapCategory(product.category);
        if (mapped !== currentCategory) {
            return false;
        }
    }
    if (!currentSearch) {
        return true;
    }
    const value = currentSearch.toLowerCase();
    return (
        (product.name || '').toLowerCase().includes(value) ||
        (product.description || '').toLowerCase().includes(value) ||
        (product.farmerName || '').toLowerCase().includes(value) ||
        (product.category || '').toLowerCase().includes(value)
    );
}

function mapCategory(rawCategory) {
    const value = (rawCategory || '').toLowerCase();
    if (value.includes('fruit')) {
        return 'Fruits';
    }
    if (value.includes('dairy') || value.includes('milk') || value.includes('cheese')) {
        return 'Dairy';
    }
    if (
        value.includes('vegetable') ||
        value.includes('leafy') ||
        value.includes('root') ||
        value.includes('legume') ||
        value.includes('herb') ||
        value.includes('cruciferous')
    ) {
        return 'Vegetables';
    }
    return 'Other';
}

function renderProducts() {
    const grid = document.getElementById('products-grid');
    if (!grid) {
        return;
    }
    if (currentProducts.length === 0) {
        grid.innerHTML = '<div class="empty-state" style="grid-column: 1 / -1; text-align: center; padding: 40px; color: #999;">No products found.</div>';
        return;
    }
    grid.innerHTML = currentProducts.map((p) => `
        <div class="product-card">
            <div class="product-details">
                <span class="product-category">${mapCategory(p.category)}</span>
                <h4>${escapeHtml(p.name)}</h4>
                <p style="font-size: 13px; color: #666; margin: 0; flex: 1;">${escapeHtml(p.description || 'No description')}</p>
                <div class="product-price">Rs. ${Number(p.price || 0).toFixed(2)}</div>
                <div class="farmer-info" onclick="showFarmerProfile(${p.id})">
                    <span><strong>Farmer:</strong> ${escapeHtml(p.farmerName || 'Unknown')}</span>
                    <span>Location: ${escapeHtml(p.farmerLocation || 'Location unknown')}</span>
                    <span style="margin-top: 4px; color: #1a7f37;">View profile & location</span>
                </div>
                <button class="btn-add-cart" onclick="addToCart(${p.id})" ${p.stock <= 0 ? 'disabled' : ''}>
                    ${p.stock > 0 ? 'Add to Cart' : 'Out of Stock'}
                </button>
            </div>
        </div>
    `).join('');
}

function filterCategory(category) {
    currentCategory = category;
    document.querySelectorAll('.category-list li').forEach((li) => {
        li.classList.toggle('active', (li.dataset.category || '') === category);
    });
    fetchProducts();
}

function getCategoryInitial(category) {
    const map = { Vegetables: 'V', Fruits: 'F', Dairy: 'D', Other: 'O' };
    return map[category] || '?';
}

function showFarmerProfile(productId) {
    const product = currentProducts.find((entry) => entry.id === productId);
    if (!product) {
        return;
    }
    const name = product.farmerName || 'Unknown Farmer';
    document.getElementById('farmer-modal-name').textContent = name;
    document.getElementById('farmer-modal-location').textContent = product.farmerLocation || 'Not provided';
    document.getElementById('farmer-modal-phone').textContent = product.farmerPhone || 'Not provided';
    const avatar = document.getElementById('farmer-modal-avatar');
    if (avatar) {
        avatar.textContent = initialsFromName(name);
    }
    document.getElementById('farmer-modal').classList.add('active');
}

function initialsFromName(fullName) {
    const parts = String(fullName || '').trim().split(/\s+/).filter(Boolean);
    if (parts.length === 0) {
        return '?';
    }
    if (parts.length === 1) {
        return parts[0].slice(0, 2).toUpperCase();
    }
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
}

function closeFarmerModal() {
    document.getElementById('farmer-modal').classList.remove('active');
}

function openProfile() {
    document.getElementById('profile-modal').classList.add('active');
}

function closeProfile() {
    document.getElementById('profile-modal').classList.remove('active');
}

function loadCart() {
    const saved = localStorage.getItem('farmdirect_cart');
    if (!saved) {
        return;
    }
    try {
        cart = JSON.parse(saved);
    } catch (e) {
        cart = [];
    }
    updateCartBadge();
}

function saveCart() {
    localStorage.setItem('farmdirect_cart', JSON.stringify(cart));
    updateCartBadge();
}

function updateCartBadge() {
    const badge = document.getElementById('cart-badge');
    if (!badge) {
        return;
    }
    const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);
    badge.textContent = String(totalItems);
    badge.style.display = totalItems > 0 ? 'inline-block' : 'none';
}

function addToCart(productId) {
    const product = currentProducts.find((entry) => entry.id === productId);
    if (!product) {
        showFlash('This product is no longer available.', 'error');
        return;
    }
    const existing = cart.find((item) => item.id === productId);
    if (existing) {
        if (existing.quantity >= product.stock) {
            showFlash(`Only ${product.stock} available for ${product.name}.`, 'error');
            return;
        }
        existing.quantity += 1;
    } else {
        cart.push({
            id: product.id,
            name: product.name,
            price: product.price,
            farmerName: product.farmerName,
            quantity: 1
        });
    }
    saveCart();
    showFlash(`${product.name} added to cart`, 'success');
}

function updateCartItem(productId, delta) {
    const item = cart.find((entry) => entry.id === productId);
    if (!item) {
        return;
    }
    item.quantity = Math.max(1, item.quantity + delta);
    saveCart();
    renderCart();
}

function removeFromCart(productId) {
    cart = cart.filter((entry) => entry.id !== productId);
    saveCart();
    renderCart();
}

function openCart() {
    renderCart();
    document.getElementById('cart-modal').classList.add('active');
}

function closeCart() {
    document.getElementById('cart-modal').classList.remove('active');
}

function renderCart() {
    const container = document.getElementById('cart-items');
    const totalEl = document.getElementById('cart-total-price');
    const checkoutBtn = document.getElementById('checkout-btn');
    if (!container || !totalEl || !checkoutBtn) {
        return;
    }
    if (cart.length === 0) {
        container.innerHTML = '<p style="text-align:center; color:#999;">Your cart is empty.</p>';
        totalEl.textContent = '0.00';
        checkoutBtn.disabled = true;
        return;
    }

    let total = 0;
    container.innerHTML = cart.map((item) => {
        const itemTotal = Number(item.price) * Number(item.quantity);
        total += itemTotal;
        return `
            <div class="cart-item">
                <div>
                    <h5>${escapeHtml(item.name)}</h5>
                    <div class="farmer-name">From ${escapeHtml(item.farmerName || 'Unknown')}</div>
                </div>
                <div>Rs. ${Number(item.price).toFixed(2)}</div>
                <div style="display:flex; align-items:center; gap:8px;">
                    <button class="btn-remove" onclick="updateCartItem(${item.id}, -1)">-</button>
                    <strong>${item.quantity}</strong>
                    <button class="btn-remove" onclick="updateCartItem(${item.id}, 1)">+</button>
                </div>
                <div style="display:flex; align-items:center; gap:8px; justify-content:flex-end;">
                    <strong>Rs. ${itemTotal.toFixed(2)}</strong>
                    <button class="btn-remove" onclick="removeFromCart(${item.id})">Remove</button>
                </div>
            </div>
        `;
    }).join('');
    totalEl.textContent = total.toFixed(2);
    checkoutBtn.disabled = false;
}

async function checkout() {
    if (cart.length === 0) {
        return;
    }
    const checkoutBtn = document.getElementById('checkout-btn');
    checkoutBtn.disabled = true;
    checkoutBtn.textContent = 'Processing...';

    const location = document.getElementById('customer-location')?.textContent?.trim() || 'Profile location unavailable';
    const phone = document.getElementById('customer-phone')?.textContent?.trim() || 'Profile phone unavailable';

    let successCount = 0;
    const failureMessages = [];
    for (const item of cart) {
        try {
            const res = await fetch(`${API_BASE}/api/customer/orders/`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    productId: String(item.id),
                    quantity: item.quantity,
                    location,
                    phoneNumber: phone
                })
            });
            if (res.ok) {
                successCount += 1;
            } else {
                let errorMessage = `Could not order ${item.name}.`;
                try {
                    const data = await res.json();
                    if (data && data.error) {
                        errorMessage = data.error;
                    }
                } catch (parseErr) {
                    // Keep fallback message if the error body is not JSON.
                }
                failureMessages.push(`${item.name}: ${errorMessage}`);
            }
        } catch (err) {
            failureMessages.push(`${item.name}: Network error while placing the order.`);
        }
    }

    if (successCount === cart.length) {
        cart = [];
        saveCart();
        closeCart();
        showFlash('Checkout successful. Your orders are now being processed.', 'success');
        fetchProducts();
    } else if (failureMessages.length === 1) {
        showFlash(failureMessages[0], 'error');
    } else {
        showFlash(failureMessages[0] || `Only ${successCount} of ${cart.length} items were ordered.`, 'error');
    }
    checkoutBtn.disabled = false;
    checkoutBtn.textContent = 'Proceed to Checkout';
}

async function openOrders() {
    document.getElementById('orders-modal').classList.add('active');
    toggleOrdersTab('tracking'); // Set default tab properly
    const tracking = document.getElementById('order-tracking-list');
    const history = document.getElementById('order-history-list');
    tracking.innerHTML = '<p>Loading tracking updates...</p>';
    history.innerHTML = '<p>Loading order history...</p>';
    try {
        const res = await fetch(`${API_BASE}/api/customer/orders/`);
        if (!res.ok) {
            throw new Error('Failed');
        }
        const data = await res.json();
        renderOrders(data.orders || []);
    } catch (err) {
        tracking.innerHTML = '<p style="color:red;">Unable to load tracking now.</p>';
        history.innerHTML = '<p style="color:red;">Unable to load order history now.</p>';
    }
}

function closeOrders() {
    document.getElementById('orders-modal').classList.remove('active');
}

function renderOrders(orders) {
    const tracking = document.getElementById('order-tracking-list');
    const history = document.getElementById('order-history-list');
    if (!orders.length) {
        tracking.innerHTML = '<p style="color:#999;">No active orders.</p>';
        history.innerHTML = '<p style="color:#999;">No previous orders yet.</p>';
        return;
    }
    const activeOrders = orders.filter((order) => order.status !== 'DELIVERED' && order.status !== 'CANCELLED');
    const completedOrders = orders; // History shows all orders

    tracking.innerHTML = activeOrders.length ? activeOrders.map(o => renderOrderCard(o, true)).join('') : '<p style="color:#999;">No active orders.</p>';
    history.innerHTML = completedOrders.map(o => renderOrderCard(o, false)).join('');
}

function renderOrderCard(order, isActiveTracking) {
    const statusLabel = String(order.status || 'PLACED').replace('_', ' ');
    let statusColor = '#1f2937';
    if (order.status === 'DELIVERED') statusColor = '#166534';
    if (order.status === 'CANCELLED') statusColor = '#dc3545';
    
    const canCancel = isActiveTracking && order.status !== 'DELIVERED' && order.status !== 'CANCELLED';

    return `
        <div class="order-item">
            <div>
                <h5>${escapeHtml(order.productName || `Product #${order.productId}`)}</h5>
                <div class="farmer-name">Farmer: ${escapeHtml(order.farmerName || 'Unknown')}</div>
                <div class="farmer-name">Placed: ${new Date(order.createdAtMs || Date.now()).toLocaleString()}</div>
            </div>
            <div>Qty: ${order.quantity}</div>
            <div style="font-size:12px; color:${statusColor}; font-weight:700;">${statusLabel}</div>
            <div style="display: flex; flex-direction: column; align-items: flex-end; gap: 8px;">
                <div style="font-size:12px; color:#666; text-align: right;">
                    Deliver to: ${escapeHtml(order.deliveryLocation || '-')}<br>
                    Total: Rs. ${Number(order.totalPrice || 0).toFixed(2)}
                </div>
                ${canCancel ? `<button class="btn-remove" onclick="cancelOrder('${order.id}')">Cancel Order</button>` : ''}
                ${!isActiveTracking ? `<button class="btn-add-cart" style="margin-top: 0; padding: 4px 8px; font-size: 12px;" onclick="addToCart(${order.productId})">Reorder</button>` : ''}
            </div>
        </div>
    `;
}

let orderToCancel = null;

function cancelOrder(orderId) {
    orderToCancel = orderId;
    document.getElementById('cancel-confirm-modal').classList.add('active');
}

function closeCancelConfirm() {
    document.getElementById('cancel-confirm-modal').classList.remove('active');
    orderToCancel = null;
}

document.addEventListener('DOMContentLoaded', () => {
    const btnConfirmCancel = document.getElementById('btn-confirm-cancel');
    if (btnConfirmCancel) {
        btnConfirmCancel.addEventListener('click', proceedCancelOrder);
    }
});

async function proceedCancelOrder() {
    if (!orderToCancel) return;
    const orderId = orderToCancel;
    closeCancelConfirm();
    
    try {
        const res = await fetch(`${API_BASE}/api/customer/orders/${orderId}`, {
            method: 'DELETE'
        });
        if (res.ok) {
            showFlash('Order cancelled successfully', 'success');
            openOrders(); // Refresh the list
        } else {
            let errorMsg = 'Failed to cancel order';
            try {
                const data = await res.json();
                if (data && data.error) errorMsg = data.error;
            } catch(e) {}
            showFlash(errorMsg, 'error');
        }
    } catch (err) {
        showFlash('Network error while cancelling order', 'error');
    }
}

function toggleOrdersTab(tab) {
    const trackingTab = document.getElementById('orders-tab-tracking');
    const historyTab = document.getElementById('orders-tab-history');
    const trackingPanel = document.getElementById('order-tracking-list');
    const historyPanel = document.getElementById('order-history-list');

    const isTracking = tab === 'tracking';
    trackingTab.classList.toggle('active', isTracking);
    historyTab.classList.toggle('active', !isTracking);
    trackingPanel.style.display = isTracking ? 'block' : 'none';
    historyPanel.style.display = isTracking ? 'none' : 'block';
}

function showFlash(message, type) {
    const flash = document.getElementById('flash-message');
    if (!flash) {
        return;
    }
    flash.className = `flash ${type}`;
    flash.textContent = message;
    setTimeout(() => flash.classList.add('hidden'), 3000);
}

function escapeHtml(value) {
    return String(value || '')
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#39;');
}
