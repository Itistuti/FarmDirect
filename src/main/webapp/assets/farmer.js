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
            const ordersByProductId = groupOrdersByProductId(orders);

            renderProducts(products, ordersByProductId);
            updateStats(products);
        })
        .catch(error => {
            console.error('Error:', error);
            showFlash('Error loading products: ' + error.message, 'error');
        });
}

function renderProducts(products, ordersByProductId) {
    const container = document.getElementById('products-container');

    if (!products || products.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">🌱</div>
                <p>No products yet. Add your first vegetable listing to get started!</p>
            </div>
        `;
        return;
    }

    container.innerHTML = products.map(product => {
        const orders = (ordersByProductId && ordersByProductId[String(product.id)]) || [];
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

            <div class="product-orders">
                <h4>Orders</h4>
                ${orders.length === 0 ? `<div class="muted">No orders yet.</div>` : orders.map(order => `
                    <div class="order-item">
                        <div><strong>${escapeHtml(order.customerName || '')}</strong> ordered <strong>${order.quantity} kg</strong></div>
                        <div>Location: ${escapeHtml(order.location || '')}</div>
                        <div>Phone number: ${escapeHtml(order.phoneNumber || '')}</div>
                    </div>
                `).join('')}
            </div>

            <div class="product-actions">
                <button class="btn-edit" onclick="editProduct('${product.id}')">Edit</button>
                <button class="btn-delete" onclick="deleteProduct('${product.id}')">Delete</button>
            </div>
        </div>
    `;
    }).join('');
}

function groupOrdersByProductId(orders) {
    const map = {};
    if (!orders) {
        return map;
    }
    orders.forEach(order => {
        const key = String(order.productId);
        if (!map[key]) {
            map[key] = [];
        }
        map[key].push(order);
    });
    return map;
}

function updateStats(products) {
    const total = products.length;
    
    const totalEl = document.getElementById('stat-total');
    if (totalEl) totalEl.textContent = total;
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
    return value.toFixed(2);
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

