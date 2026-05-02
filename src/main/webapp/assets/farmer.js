// Farmer Dashboard JavaScript

let currentEditingId = null;

document.addEventListener('DOMContentLoaded', function() {
    // Check if user is authenticated and is a farmer
    fetch('api/protected/any', { credentials: 'include' })
        .then(response => {
            if (!response.ok) {
                window.location.href = 'login.jsp';
                throw new Error('Not authenticated');
            }
            return response.json();
        })
        .then(data => {
            if (data.role !== 'FARMER') {
                window.location.href = 'dashboard.jsp';
                return;
            }
            loadProducts();
        })
        .catch(error => {
            console.error('Error checking auth:', error);
            window.location.href = 'login.jsp';
        });

    // Form submission
    const form = document.getElementById('product-form');
    form.addEventListener('submit', handleFormSubmit);
});

function handleFormSubmit(e) {
    e.preventDefault();

    const formData = {
        name: document.getElementById('product-name').value,
        category: document.getElementById('product-category').value,
        description: document.getElementById('product-description').value,
        price: parseFloat(document.getElementById('product-price').value),
        stock: parseInt(document.getElementById('product-stock').value)
    };

    if (currentEditingId) {
        updateProduct(currentEditingId, formData);
    } else {
        addProduct(formData);
    }
}

function addProduct(productData) {
    fetch('api/farmer/products', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify(productData)
    })
        .then(response => {
            if (response.ok) {
                showFlash('Product added successfully!', 'success');
                resetForm();
                loadProducts();
            } else {
                return response.json().then(data => {
                    showFlash(data.error || 'Error adding product', 'error');
                });
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showFlash('Error adding product: ' + error.message, 'error');
        });
}

function updateProduct(productId, productData) {
    fetch('api/farmer/products/' + productId, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify(productData)
    })
        .then(response => {
            if (response.ok) {
                showFlash('Product updated successfully!', 'success');
                resetForm();
                currentEditingId = null;
                loadProducts();
            } else {
                return response.json().then(data => {
                    showFlash(data.error || 'Error updating product', 'error');
                });
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showFlash('Error updating product: ' + error.message, 'error');
        });
}

function deleteProduct(productId) {
    if (confirm('Are you sure you want to delete this product?')) {
        fetch('api/farmer/products/' + productId, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include'
        })
            .then(response => {
                if (response.ok) {
                    showFlash('Product deleted successfully!', 'success');
                    loadProducts();
                } else {
                    return response.json().then(data => {
                        showFlash(data.error || 'Error deleting product', 'error');
                    });
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showFlash('Error deleting product: ' + error.message, 'error');
            });
    }
}

function editProduct(productId) {
    fetch('api/farmer/products/' + productId, {
        method: 'GET',
        credentials: 'include'
    })
        .then(response => response.json())
        .then(product => {
            document.getElementById('product-name').value = product.name;
            document.getElementById('product-category').value = product.category;
            document.getElementById('product-description').value = product.description;
            document.getElementById('product-price').value = product.price;
            document.getElementById('product-stock').value = product.stock;

            currentEditingId = productId;
            document.getElementById('form-title').textContent = 'Edit Product';
            document.getElementById('form-submit-btn').textContent = 'Update Product';
            document.getElementById('form-cancel-btn').classList.remove('hidden');

            // Scroll to form
            document.querySelector('.sidebar-form').scrollIntoView({ behavior: 'smooth' });
        })
        .catch(error => {
            console.error('Error:', error);
            showFlash('Error loading product: ' + error.message, 'error');
        });
}

function cancelEdit() {
    resetForm();
    currentEditingId = null;
}

function resetForm() {
    document.getElementById('product-form').reset();
    document.getElementById('form-title').textContent = 'Add New Product';
    document.getElementById('form-submit-btn').textContent = 'Add Product';
    document.getElementById('form-cancel-btn').classList.add('hidden');
}

function loadProducts() {
    Promise.all([
        fetch('api/farmer/products', { method: 'GET', credentials: 'include' }).then(r => r.json()),
        fetch('api/farmer/orders', { method: 'GET', credentials: 'include' }).then(r => r.json()).catch(() => ({ orders: [] }))
    ])
        .then(([productData, orderData]) => {
            const products = productData.products || [];
            const orders = orderData.orders || [];

            renderOrdersPanel(orders);
            renderProducts(products);
            updateStats(products, orders.length);
        })
        .catch(error => {
            console.error('Error:', error);
            const oc = document.getElementById('orders-container');
            if (oc) {
                oc.innerHTML = '<p class="muted" style="margin: 0 0 12px;">Could not load orders. Refresh the page to try again.</p>';
            }
            const statOrders = document.getElementById('stat-orders');
            if (statOrders) {
                statOrders.textContent = '0';
            }
            showFlash('Error loading products: ' + error.message, 'error');
        });
}

function renderProducts(products) {
    const container = document.getElementById('products-container');

    if (!products || products.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <p>No products yet. Add your first vegetable listing to get started!</p>
            </div>
        `;
        return;
    }

    container.innerHTML = products.map(product => {
        return `
        <div class="product-card">
            <div class="product-info">
                <div class="product-name">${escapeHtml(product.name)}</div>
                <div class="product-meta">
                    <span class="product-category">${escapeHtml(product.category)}</span>
                    <span>ID: ${product.id}</span>
                </div>
                ${product.description ? `<div class="product-description">${escapeHtml(product.description)}</div>` : ''}
                <div class="product-price">Price per kilogram = <strong>${formatPrice(product.price)}</strong></div>
                <div class="product-stock ${product.stock === 0 ? 'out' : product.stock < 10 ? 'low' : ''}">
                    Stocks = <strong>${product.stock} kg</strong>
                </div>
            </div>

            <div class="product-actions">
                <button class="btn-edit" onclick="editProduct('${product.id}')">Edit</button>
                <button class="btn-delete" onclick="deleteProduct('${product.id}')">Delete</button>
            </div>
        </div>
    `;
    }).join('');
}

function renderOrdersPanel(orders) {
    const container = document.getElementById('orders-container');
    if (!container) {
        return;
    }
    if (!orders || orders.length === 0) {
        container.innerHTML = '<p class="muted" style="margin: 0 0 12px; font-size: 14px;">No customer orders yet. When buyers place orders for your products, they will appear here with delivery details.</p>';
        return;
    }

    const sorted = [...orders].sort((a, b) => {
        const ta = orderTimestampMs(a);
        const tb = orderTimestampMs(b);
        return tb - ta;
    });

    container.innerHTML = sorted.map(order => {
        const isCancelled = order.status === 'CANCELLED';
        const isDelivered = order.status === 'DELIVERED';
        
        return `
        <article class="order-card" style="${isCancelled ? 'opacity: 0.7;' : ''}">
            <div class="order-card-header">
                <span class="order-card-product">${escapeHtml(order.productName || 'Product')}</span>
                <span class="order-card-date">${escapeHtml(formatOrderPlaced(order))}</span>
            </div>
            <div>
                <div class="order-card-label">Buyer</div>
                <div class="order-card-value">${escapeHtml(order.customerName || 'Unknown')}</div>
            </div>
            <div>
                <div class="order-card-label">Quantity</div>
                <div class="order-card-value">${escapeHtml(String(order.quantity))} kg</div>
            </div>
            <div>
                <div class="order-card-label">Delivery location</div>
                <div class="order-card-value">${escapeHtml(order.location || 'Not provided')}</div>
            </div>
            <div>
                <div class="order-card-label">Buyer phone</div>
                <div class="order-card-value">${escapeHtml(order.phoneNumber || 'Not provided')}</div>
            </div>
            <div style="grid-column: 1 / -1; margin-top: 8px; padding-top: 8px; border-top: 1px dashed #e5e7eb;">
                <div class="order-card-label">Status Update</div>
                ${isCancelled ? `
                    <div style="color: #dc3545; font-weight: bold;">CANCELLED</div>
                ` : `
                    <select class="status-select" style="padding: 4px 8px; border-radius: 4px; border: 1px solid #ccc; font-size: 12px;" 
                        onchange="updateOrderStatus('${order.id}', this.value)" ${isDelivered ? 'disabled' : ''}>
                        <option value="PLACED" ${order.status === 'PLACED' ? 'selected' : ''}>Placed</option>
                        <option value="PACKING" ${order.status === 'PACKING' ? 'selected' : ''}>Packing</option>
                        <option value="IN_TRANSIT" ${order.status === 'IN_TRANSIT' ? 'selected' : ''}>In Transit</option>
                        <option value="DELIVERED" ${order.status === 'DELIVERED' ? 'selected' : ''}>Delivered</option>
                    </select>
                `}
            </div>
        </article>
        `;
    }).join('');
}

function updateOrderStatus(orderId, newStatus) {
    fetch('api/farmer/orders/' + orderId + '/status', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify({ status: newStatus })
    })
    .then(response => {
        if (response.ok) {
            showFlash('Order status updated', 'success');
            loadProducts(); // refresh orders list
        } else {
            return response.json().then(data => {
                showFlash(data.error || 'Failed to update order status', 'error');
                loadProducts();
            });
        }
    })
    .catch(err => {
        showFlash('Network error', 'error');
        loadProducts();
    });
}

function orderTimestampMs(order) {
    const raw = order && order.createdAt;
    if (raw == null) {
        return 0;
    }
    if (typeof raw === 'number') {
        return raw < 1e12 ? raw * 1000 : raw;
    }
    if (typeof raw === 'string') {
        const t = Date.parse(raw);
        return Number.isNaN(t) ? 0 : t;
    }
    if (Array.isArray(raw) && raw.length > 0) {
        const sec = Number(raw[0]);
        const nano = raw.length > 1 ? Number(raw[1]) : 0;
        if (!Number.isNaN(sec)) {
            return sec * 1000 + Math.floor(nano / 1e6);
        }
    }
    if (typeof raw === 'object' && raw !== null && typeof raw.epochSecond === 'number') {
        const sec = raw.epochSecond;
        const nano = typeof raw.nano === 'number' ? raw.nano : 0;
        return sec * 1000 + Math.floor(nano / 1e6);
    }
    return 0;
}

function formatOrderPlaced(order) {
    const ms = orderTimestampMs(order);
    if (!ms) {
        return 'Date unknown';
    }
    return new Date(ms).toLocaleString();
}

function updateStats(products, orderCount) {
    const total = products.length;

    const totalEl = document.getElementById('stat-total');
    if (totalEl) {
        totalEl.textContent = total;
    }
    const ordersEl = document.getElementById('stat-orders');
    if (ordersEl) {
        ordersEl.textContent = typeof orderCount === 'number' ? orderCount : 0;
    }
}

function showFlash(message, type = 'info') {
    const flashContainer = document.getElementById('flash-message');
    flashContainer.textContent = message;
    flashContainer.className = 'flash ' + type;
    flashContainer.style.display = 'block';

    if (type !== 'error') {
        setTimeout(() => {
            flashContainer.style.display = 'none';
        }, 4000);
    }
}

function escapeHtml(unsafe) {
    return unsafe
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}

function formatPrice(value) {
    if (typeof value !== 'number' || Number.isNaN(value)) {
        return '';
    }
    return 'Rs. ' + value.toFixed(2);
}

function logout() {
    fetch('api/auth/logout', {
        method: 'POST',
        credentials: 'include'
    })
        .then(() => {
            window.location.href = 'login.jsp';
        })
        .catch(error => {
            console.error('Logout error:', error);
            window.location.href = 'login.jsp';
        });
}

