<%@ page import="agriconnect.farming.auth.SessionKeys" %>
    <%@ page import="java.net.URLEncoder" %>
        <%@ page import="java.nio.charset.StandardCharsets" %>
            <% Object role=session.getAttribute(SessionKeys.USER_ROLE); if (role==null ||
                !"FARMER".equalsIgnoreCase(role.toString())) { String msg=URLEncoder.encode("Please log in as a farmer",
                StandardCharsets.UTF_8); response.sendRedirect(request.getContextPath() + "/login.jsp?error=" + msg);
                return; } %>

                <!DOCTYPE html>
                <html lang="en">

                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>FarmDirect</title>
                    <base href="${pageContext.request.contextPath}/">
                    <link rel="stylesheet" href="assets/styles.css">
                    <script src="assets/auth.js" defer></script>
                    <script src="assets/farmer.js" defer></script>
                    <style>
                        /* Make this dashboard wider than global container */
                        body[data-page="farmer-dashboard"] .container {
                            max-width: 1240px;
                        }

                        .farmer-dashboard {
                            display: grid;
                            grid-template-columns: 1fr 2fr;
                            gap: 24px;
                            margin-top: 24px;
                            align-items: start;
                        }

                        .main-column {
                            display: grid;
                            grid-template-columns: 1fr 1fr;
                            gap: 24px;
                            min-width: 0;
                            align-items: start;
                        }

                        @media (max-width: 900px) {
                            .farmer-dashboard {
                                grid-template-columns: 1fr;
                            }

                            .main-column {
                                grid-template-columns: 1fr;
                            }
                        }

                        .orders-section {
                            background: #fff;
                            border: 1px solid #e0e0e0;
                            border-radius: 8px;
                            padding: 16px 16px 8px;
                        }

                        .orders-section .section-header {
                            margin-bottom: 12px;
                        }

                        .orders-list {
                            display: flex;
                            flex-direction: column;
                            gap: 10px;
                            max-height: 420px;
                            overflow-y: auto;
                        }

                        .order-card {
                            display: grid;
                            grid-template-columns: 1fr 1fr;
                            gap: 10px 16px;
                            padding: 12px 14px;
                            background: #f9fafb;
                            border: 1px solid #e5e7eb;
                            border-radius: 8px;
                            font-size: 13px;
                        }

                        @media (max-width: 700px) {
                            .order-card {
                                grid-template-columns: 1fr;
                            }
                        }

                        .order-card-label {
                            font-size: 11px;
                            font-weight: 700;
                            text-transform: uppercase;
                            letter-spacing: 0.04em;
                            color: #6b7280;
                            margin-bottom: 2px;
                        }

                        .order-card-value {
                            color: #111827;
                            word-break: break-word;
                        }

                        .order-card-header {
                            grid-column: 1 / -1;
                            display: flex;
                            flex-wrap: wrap;
                            justify-content: space-between;
                            align-items: baseline;
                            gap: 8px;
                            padding-bottom: 8px;
                            margin-bottom: 4px;
                            border-bottom: 1px dashed #e5e7eb;
                        }

                        .order-card-product {
                            font-weight: 700;
                            font-size: 15px;
                            color: #166534;
                        }

                        .order-card-date {
                            font-size: 12px;
                            color: #6b7280;
                        }

                        .sidebar-form {
                            background: #f9f9f9;
                            border: 1px solid #e0e0e0;
                            border-radius: 8px;
                            padding: 20px;
                            max-height: fit-content;
                            position: sticky;
                            top: 20px;
                        }

                        .sidebar-form h3 {
                            margin-top: 0;
                            font-size: 18px;
                            margin-bottom: 16px;
                            border-bottom: 2px solid #1bcc4b;
                            padding-bottom: 8px;
                        }

                        .form-group {
                            margin-bottom: 12px;
                        }

                        .form-group label {
                            display: block;
                            font-weight: 500;
                            margin-bottom: 4px;
                            font-size: 13px;
                            color: #333;
                        }

                        .form-group input,
                        .form-group textarea,
                        .form-group select {
                            width: 100%;
                            padding: 8px;
                            border: 1px solid #ddd;
                            border-radius: 4px;
                            font-size: 13px;
                            font-family: inherit;
                        }

                        .form-group textarea {
                            resize: vertical;
                            min-height: 60px;
                        }

                        .form-group input:focus,
                        .form-group textarea:focus,
                        .form-group select:focus {
                            outline: none;
                            border-color: #1bcc4b;
                            box-shadow: 0 0 0 2px rgba(27, 204, 75, 0.1);
                        }

                        .form-actions {
                            display: flex;
                            gap: 8px;
                            margin-top: 16px;
                        }

                        .form-actions button {
                            flex: 1;
                            padding: 10px;
                            border: none;
                            border-radius: 4px;
                            font-weight: 500;
                            cursor: pointer;
                            font-size: 13px;
                        }

                        .form-actions .btn-submit {
                            background: #1bcc4b;
                            color: white;
                        }

                        .form-actions .btn-submit:hover {
                            background: #15a339;
                        }

                        .form-actions .btn-cancel {
                            background: #f0f0f0;
                            color: #333;
                        }

                        .form-actions .btn-cancel:hover {
                            background: #e0e0e0;
                        }

                        .products-section {
                            background: white;
                        }

                        .section-header {
                            display: flex;
                            justify-content: space-between;
                            align-items: center;
                            margin-bottom: 16px;
                            padding-bottom: 12px;
                            border-bottom: 2px solid #1bcc4b;
                        }

                        .section-header h2 {
                            margin: 0;
                            font-size: 20px;
                        }

                        .stats {
                            display: grid;
                            grid-template-columns: repeat(3, 1fr);
                            gap: 12px;
                            margin-bottom: 20px;
                        }

                        .stat-card {
                            background: linear-gradient(135deg, #1bcc4b 0%, #1ba339 100%);
                            color: white;
                            padding: 16px;
                            border-radius: 8px;
                            text-align: center;
                        }

                        .stat-card .value {
                            font-size: 32px;
                            font-weight: bold;
                        }

                        .stat-card .label {
                            font-size: 12px;
                            opacity: 0.9;
                            margin-top: 4px;
                        }

                        .products-list {
                            display: grid;
                            gap: 12px;
                        }

                        .product-card {
                            background: white;
                            border: 1px solid #e0e0e0;
                            border-radius: 8px;
                            padding: 16px;
                            display: grid;
                            grid-template-columns: 1fr auto;
                            gap: 16px;
                            align-items: start;
                            transition: box-shadow 0.2s;
                        }

                        .product-card:hover {
                            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
                        }

                        .product-info {
                            display: grid;
                            gap: 8px;
                        }

                        .product-name {
                            font-size: 16px;
                            font-weight: 600;
                            color: #1a1a1a;
                        }

                        .product-meta {
                            display: flex;
                            gap: 12px;
                            font-size: 12px;
                            color: #666;
                        }

                        .product-meta span {
                            display: flex;
                            align-items: center;
                            gap: 4px;
                        }

                        .product-category {
                            display: inline-block;
                            background: #f0f0f0;
                            padding: 2px 8px;
                            border-radius: 4px;
                            font-size: 12px;
                            color: #666;
                        }

                        .product-description {
                            font-size: 13px;
                            color: #666;
                            margin-top: 8px;
                            line-height: 1.4;
                        }

                        .product-price {
                            font-size: 18px;
                            font-weight: bold;
                            color: #1bcc4b;
                            margin-top: 8px;
                        }

                        .product-stock {
                            background: #f9f9f9;
                            padding: 8px;
                            border-radius: 4px;
                            margin-top: 8px;
                            font-size: 12px;
                        }

                        .product-stock.low {
                            background: #fff3cd;
                            color: #856404;
                        }

                        .product-stock.out {
                            background: #f8d7da;
                            color: #721c24;
                        }

                        .product-actions {
                            display: flex;
                            flex-direction: column;
                            gap: 8px;
                            min-width: 100px;
                        }

                        .product-actions button {
                            padding: 8px 12px;
                            border: none;
                            border-radius: 4px;
                            font-size: 12px;
                            font-weight: 500;
                            cursor: pointer;
                            transition: all 0.2s;
                        }

                        .btn-edit {
                            background: #f0f0f0;
                            color: #333;
                        }

                        .btn-edit:hover {
                            background: #e0e0e0;
                        }

                        .btn-delete {
                            background: #dc3545;
                            color: white;
                        }

                        .btn-delete:hover {
                            background: #c82333;
                        }

                        .empty-state {
                            text-align: center;
                            padding: 40px 20px;
                            color: #999;
                        }

                        .empty-state-icon {
                            width: 72px;
                            height: 72px;
                            margin: 0 auto 16px;
                            border-radius: 50%;
                            background: #e8f5e9;
                            border: 2px solid #1bcc4b;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            font-size: 14px;
                            font-weight: 700;
                            color: #166534;
                            line-height: 1;
                        }

                        #flash-message {
                            margin-bottom: 16px;
                        }

                        .hidden {
                            display: none;
                        }
                    </style>
                </head>

                <body data-page="farmer-dashboard">
                    <div class="container">
                        <div class="card">
                            <div class="actions" style="justify-content: space-between; align-items: center;">
                                <div>
                                    <span class="badge">Farmer Dashboard</span>
                                    <h1 id="welcome-message">Your Farm</h1>
                                    <p class="muted">Manage your vegetable listings and inventory</p>
                                    <% String loc = (String) session.getAttribute(SessionKeys.USER_LOCATION);
                                       if(loc != null && !loc.trim().isEmpty()) { %>
                                        <p class="muted" style="margin-top: 4px;"><strong>Location:</strong> <%= loc %></p>
                                    <% } %>
                                    <% String phone = (String) session.getAttribute(SessionKeys.USER_PHONE);
                                       if(phone != null && !phone.trim().isEmpty()) { %>
                                        <p class="muted" style="margin-top: 4px;"><strong>Phone:</strong> <%= phone %></p>
                                    <% } %>
                                </div>
                                <button type="button" onclick="logout()" style="max-width: 180px;">Logout</button>
                            </div>

                            <div id="flash-message" class="flash"></div>

                            <div class="farmer-dashboard">
                                <!-- Sidebar Form -->
                                <aside class="sidebar-form">
                                    <h3 id="form-title">Add New Product</h3>
                                    <form id="product-form">
                                        <div class="form-group">
                                            <label for="product-name">Product Name *</label>
                                            <input id="product-name" name="name" type="text"
                                                placeholder="Enter product name" required>
                                        </div>

                                        <div class="form-group">
                                            <label for="product-category">Category *</label>
                                            <select id="product-category" name="category" required>
                                                <option value="">Select a category</option>
                                                <option value="Leafy Greens">Leafy Greens</option>
                                                <option value="Root Vegetables">Root Vegetables</option>
                                                <option value="Fruits">Fruits</option>
                                                <option value="Legumes">Legumes</option>
                                                <option value="Cruciferous">Cruciferous</option>
                                                <option value="Herbs">Herbs</option>
                                                <option value="Other">Other</option>
                                            </select>
                                        </div>

                                        <div class="form-group">
                                            <label for="product-description">Description</label>
                                            <textarea id="product-description" name="description"
                                                placeholder="Enter product description"></textarea>
                                        </div>

                                        <div class="form-group">
                                            <label for="product-price">Price (per kilogram) *</label>
                                            <input id="product-price" name="price" type="number" step="0.01" min="0.01"
                                                placeholder="0.00" required>
                                        </div>

                                        <div class="form-group">
                                            <label for="product-stock">Stock Quantity (in kg) *</label>
                                            <input id="product-stock" name="stock" type="number" min="0" placeholder="0"
                                                required>
                                        </div>

                                        <div class="form-actions">
                                            <button type="submit" class="btn-submit" id="form-submit-btn">Add
                                                Product</button>
                                            <button type="button" class="btn-cancel hidden" id="form-cancel-btn"
                                                onclick="cancelEdit()">Cancel</button>
                                        </div>
                                    </form>
                                </aside>

                                <div class="main-column">
                                    <!-- All customer orders (this farmer's products only) -->
                                    <section class="orders-section" aria-labelledby="orders-heading">
                                        <div class="section-header">
                                            <h2 id="orders-heading">Customer orders <span
                                                    style="font-size: 16px; font-weight: normal; color: #666; margin-left: 8px;">(<span
                                                    id="stat-orders">0</span>)</span></h2>
                                        </div>
                                        <div id="orders-container" class="orders-list">
                                            <p class="muted" style="margin: 0 0 12px; font-size: 14px;">Loading orders...</p>
                                        </div>
                                    </section>

                                    <!-- Products Section -->
                                    <section class="products-section">
                                        <div class="section-header">
                                            <h2>Your Products <span
                                                    style="font-size: 16px; font-weight: normal; color: #666; margin-left: 8px;">(Total:
                                                    <span id="stat-total">0</span>)</span></h2>
                                        </div>

                                        <div id="products-container" class="products-list">
                                            <div class="empty-state">
                                                <p>No products yet. Add your first vegetable listing to get started!</p>
                                            </div>
                                        </div>
                                    </section>
                                </div>
                            </div>
                        </div>
                    </div>
                </body>

                </html>