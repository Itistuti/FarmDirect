<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="agriconnect.farming.auth.SessionKeys" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.nio.charset.StandardCharsets" %>
<% 
    Object role = session.getAttribute(SessionKeys.USER_ROLE); 
    if (role == null || !"CUSTOMER".equalsIgnoreCase(role.toString())) { 
        String msg = URLEncoder.encode("Please log in as a customer", StandardCharsets.UTF_8); 
        response.sendRedirect(request.getContextPath() + "/login.jsp?error=" + msg);
        return; 
    } 
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Customer Dashboard - FarmDirect</title>
    <base href="${pageContext.request.contextPath}/">
    <link rel="stylesheet" href="assets/styles.css">
    <script src="assets/auth.js" defer></script>
    <script src="assets/customer.js" defer></script>
    <style>
        .header-actions {
            display: flex;
            align-items: center;
            gap: 16px;
        }

        .header-actions input[type="text"] {
            padding: 8px 12px;
            border: 1px solid #ccc;
            border-radius: 20px;
            font-size: 14px;
            outline: none;
            width: 250px;
            transition: border-color 0.2s, box-shadow 0.2s;
        }

        .header-actions input[type="text"]:focus {
            border-color: #1bcc4b;
            box-shadow: 0 0 0 2px rgba(27, 204, 75, 0.2);
        }

        .btn-icon {
            background: transparent;
            border: none;
            color: #333;
            font-weight: 600;
            cursor: pointer;
            padding: 8px 16px;
            border-radius: 20px;
            transition: background 0.2s;
            position: relative;
        }

        .btn-icon:hover {
            background: #f0f0f0;
        }

        .badge-count {
            position: absolute;
            top: -2px;
            right: 0px;
            background: #dc3545;
            color: white;
            font-size: 10px;
            padding: 2px 6px;
            border-radius: 10px;
            font-weight: bold;
        }

        .customer-dashboard {
            display: grid;
            grid-template-columns: 200px 1fr;
            gap: 24px;
            margin-top: 24px;
        }

        @media (max-width: 768px) {
            .customer-dashboard {
                grid-template-columns: 1fr;
            }
        }

        .sidebar-categories {
            background: #f9f9f9;
            border: 1px solid #e0e0e0;
            border-radius: 8px;
            padding: 16px;
            height: fit-content;
            position: sticky;
            top: 20px;
        }

        .sidebar-categories h3 {
            font-size: 16px;
            margin-bottom: 12px;
            border-bottom: 2px solid #1bcc4b;
            padding-bottom: 8px;
        }

        .category-list {
            list-style: none;
            padding: 0;
            margin: 0;
            display: flex;
            flex-direction: column;
            gap: 8px;
        }

        .category-list li {
            cursor: pointer;
            padding: 8px;
            border-radius: 4px;
            transition: background 0.2s, color 0.2s;
            font-size: 14px;
        }

        .category-list li:hover {
            background: #e0f8e6;
        }

        .category-list li.active {
            background: #1bcc4b;
            color: white;
            font-weight: bold;
        }

        .products-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
            gap: 20px;
        }

        .product-card {
            background: white;
            border: 1px solid #e0e0e0;
            border-radius: 12px;
            overflow: hidden;
            display: flex;
            flex-direction: column;
            transition: transform 0.2s, box-shadow 0.2s;
        }

        .product-card:hover {
            transform: translateY(-4px);
            box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
        }

        .product-image-placeholder {
            height: 140px;
            background: linear-gradient(135deg, #1bcc4b22 0%, #1ba33944 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 32px;
            color: #1bcc4b;
        }

        .category-initial {
            width: 64px;
            height: 64px;
            border-radius: 50%;
            background: rgba(27, 204, 75, 0.2);
            border: 2px solid #1bcc4b;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 22px;
            font-weight: 800;
            letter-spacing: -0.02em;
        }

        .farmer-modal-avatar {
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
        }

        .product-details {
            padding: 16px;
            display: flex;
            flex-direction: column;
            flex: 1;
        }

        .product-details h4 {
            margin: 0 0 8px 0;
            font-size: 18px;
        }

        .product-category {
            font-size: 12px;
            color: #666;
            background: #f0f0f0;
            padding: 2px 8px;
            border-radius: 12px;
            align-self: flex-start;
            margin-bottom: 8px;
        }

        .product-price {
            font-size: 20px;
            font-weight: bold;
            color: #1bcc4b;
            margin: 8px 0;
        }

        .farmer-info {
            font-size: 12px;
            color: #555;
            margin-top: auto;
            padding-top: 12px;
            border-top: 1px dashed #eee;
            cursor: pointer;
            transition: color 0.2s;
        }
        
        .farmer-info:hover {
            color: #1bcc4b;
            text-decoration: underline;
        }

        .farmer-info span {
            display: block;
        }

        .btn-add-cart {
            background: #1bcc4b;
            color: white;
            border: none;
            padding: 10px;
            border-radius: 6px;
            font-weight: bold;
            cursor: pointer;
            margin-top: 12px;
            transition: background 0.2s;
        }

        .btn-add-cart:hover {
            background: #15a339;
        }

        .btn-add-cart:disabled {
            background: #ccc;
            cursor: not-allowed;
        }

        /* Modals */
        .modal-overlay {
            position: fixed;
            top: 0; left: 0; right: 0; bottom: 0;
            background: rgba(0,0,0,0.5);
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 1000;
            opacity: 0;
            pointer-events: none;
            transition: opacity 0.3s;
        }

        .modal-overlay.active {
            opacity: 1;
            pointer-events: auto;
        }

        .modal-content {
            background: white;
            border-radius: 12px;
            padding: 24px;
            width: 100%;
            max-width: 600px;
            max-height: 80vh;
            overflow-y: auto;
            position: relative;
            transform: translateY(20px);
            transition: transform 0.3s;
        }

        .modal-overlay.active .modal-content {
            transform: translateY(0);
        }

        .modal-close {
            position: absolute;
            top: 16px;
            right: 16px;
            background: none;
            border: none;
            font-size: 24px;
            cursor: pointer;
            color: #666;
        }

        .modal-close:hover {
            color: #000;
        }

        .cart-item, .order-item {
            display: grid;
            grid-template-columns: 2fr 1fr 1fr auto;
            gap: 12px;
            align-items: center;
            padding: 12px 0;
            border-bottom: 1px solid #eee;
        }

        .cart-item h5, .order-item h5 {
            margin: 0;
            font-size: 16px;
        }
        
        .cart-item .farmer-name, .order-item .farmer-name {
            font-size: 12px;
            color: #666;
        }

        .cart-total {
            text-align: right;
            font-size: 20px;
            font-weight: bold;
            margin-top: 16px;
            color: #333;
        }

        .btn-checkout {
            width: 100%;
            background: #1bcc4b;
            color: white;
            padding: 12px;
            border: none;
            border-radius: 6px;
            font-size: 16px;
            font-weight: bold;
            cursor: pointer;
            margin-top: 16px;
        }

        .btn-checkout:hover {
            background: #15a339;
        }
        
        .btn-remove {
            background: #ff4d4d;
            color: white;
            border: none;
            padding: 4px 8px;
            border-radius: 4px;
            cursor: pointer;
            font-size: 12px;
        }

        .orders-tabs {
            display: flex;
            gap: 8px;
            margin-bottom: 16px;
        }

        .orders-tab {
            background: #f5f5f5;
            border: 1px solid #ddd;
            color: #333;
            font-weight: 600;
            padding: 8px 12px;
            border-radius: 8px;
            cursor: pointer;
            width: auto;
        }

        .orders-tab.active {
            background: #1bcc4b;
            color: white;
            border-color: #1bcc4b;
        }
    </style>
</head>

<body data-page="customer-dashboard">
    <div class="container">
        <div class="card" style="max-width: 1200px;">
            <div class="actions" style="justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 16px;">
                <div>
                    <span class="badge">Customer Dashboard</span>
                    <h1 id="welcome-message">FarmDirect Market</h1>
                    <% String loc = (String) session.getAttribute(SessionKeys.USER_LOCATION);
                       if(loc != null && !loc.trim().isEmpty()) { %>
                        <p class="muted" style="margin-top: 4px; font-size: 14px;"><strong>Your Location:</strong> <span id="customer-location"><%= loc %></span></p>
                    <% } %>
                    <% String phone = (String) session.getAttribute(SessionKeys.USER_PHONE);
                       if(phone != null && !phone.trim().isEmpty()) { %>
                        <p class="muted" style="margin-top: 4px; font-size: 14px;"><strong>Your Phone:</strong> <span id="customer-phone"><%= phone %></span></p>
                    <% } %>
                </div>
                
                <div class="header-actions">
                    <input type="text" id="search-input" placeholder="Search vegetables, fruits...">
                    <button type="button" class="btn-icon" onclick="openCart()">
                        Cart <span id="cart-badge" class="badge-count" style="display: none;">0</span>
                    </button>
                    <button type="button" class="btn-icon" onclick="openOrders()">
                        Orders
                    </button>
                    <button type="button" class="btn-icon" onclick="openProfile()">
                        Profile
                    </button>
                    <button type="button" onclick="logout()" style="padding: 8px 16px;">Logout</button>
                </div>
            </div>

            <div id="flash-message" class="flash hidden"></div>

            <div class="customer-dashboard">
                <!-- Sidebar Categories -->
                <aside class="sidebar-categories">
                    <h3>Categories</h3>
                    <ul class="category-list" id="category-list">
                        <li class="active" onclick="filterCategory('')">All Products</li>
                        <li onclick="filterCategory('Leafy Greens')">Leafy Greens</li>
                        <li onclick="filterCategory('Root Vegetables')">Root Vegetables</li>
                        <li onclick="filterCategory('Fruits')">Fruits</li>
                        <li onclick="filterCategory('Legumes')">Legumes</li>
                        <li onclick="filterCategory('Cruciferous')">Cruciferous</li>
                        <li onclick="filterCategory('Herbs')">Herbs</li>
                        <li onclick="filterCategory('Dairy')">Dairy</li>
                        <li onclick="filterCategory('Other')">Other</li>
                    </ul>
                </aside>

                <!-- Products Grid -->
                <main>
                    <div id="products-grid" class="products-grid">
                        <div class="empty-state" style="grid-column: 1 / -1; text-align: center; padding: 40px; color: #999;">
                            <p>Loading fresh products...</p>
                        </div>
                    </div>
                </main>
            </div>
        </div>
    </div>

    <!-- Cart Modal -->
    <div class="modal-overlay" id="cart-modal">
        <div class="modal-content">
            <button class="modal-close" onclick="closeCart()">&times;</button>
            <h2>Your Cart</h2>
            <div id="cart-items">
                <!-- Cart items injected here -->
            </div>
            <div class="cart-total">
                Total: Rs. <span id="cart-total-price">0.00</span>
            </div>
            <button class="btn-checkout" onclick="checkout()" id="checkout-btn">Proceed to Checkout</button>
        </div>
    </div>

    <!-- Orders Modal -->
    <div class="modal-overlay" id="orders-modal">
        <div class="modal-content" style="max-width: 800px;">
            <button class="modal-close" onclick="closeOrders()">&times;</button>
            <h2>Order History</h2>
            <div class="orders-tabs">
                <button id="orders-tab-tracking" class="orders-tab active" onclick="toggleOrdersTab('tracking')">Order Tracking</button>
                <button id="orders-tab-history" class="orders-tab" onclick="toggleOrdersTab('history')">Order History</button>
            </div>
            <div id="order-tracking-list">
                <!-- Tracking data injected here -->
            </div>
            <div id="order-history-list">
                <!-- Orders injected here -->
            </div>
        </div>
    </div>

    <!-- Farmer Profile Modal -->
    <div class="modal-overlay" id="farmer-modal">
        <div class="modal-content" style="max-width: 400px; text-align: center;">
            <button class="modal-close" onclick="closeFarmerModal()">&times;</button>
            <div class="farmer-modal-avatar" id="farmer-modal-avatar" aria-hidden="true">Farmer</div>
            <h2 id="farmer-modal-name">Farmer Name</h2>
            <p style="color: #666; font-size: 16px; margin: 8px 0;"><strong>Location:</strong> <span id="farmer-modal-location">Location</span></p>
            <p style="color: #666; font-size: 16px; margin: 8px 0;"><strong>Phone:</strong> <span id="farmer-modal-phone">Phone</span></p>
            <button class="btn-checkout" onclick="closeFarmerModal()" style="margin-top: 24px;">Close</button>
        </div>
    </div>

    <!-- Customer Profile Modal -->
    <div class="modal-overlay" id="profile-modal">
        <div class="modal-content" style="max-width: 420px;">
            <button class="modal-close" onclick="closeProfile()">&times;</button>
            <h2>Your Profile</h2>
            <p><strong>Name:</strong> <%= session.getAttribute(SessionKeys.USER_NAME) %></p>
            <p><strong>Email:</strong> <%= session.getAttribute(SessionKeys.USER_EMAIL) %></p>
            <p><strong>Location:</strong> <span id="profile-location"><%= session.getAttribute(SessionKeys.USER_LOCATION) %></span></p>
            <p><strong>Phone:</strong> <span id="profile-phone"><%= session.getAttribute(SessionKeys.USER_PHONE) %></span></p>
            <button class="btn-checkout" onclick="closeProfile()">Close</button>
        </div>
    </div>
    <!-- Cancel Confirmation Modal -->
    <div class="modal-overlay" id="cancel-confirm-modal">
        <div class="modal-content" style="max-width: 400px; text-align: center;">
            <button class="modal-close" onclick="closeCancelConfirm()">&times;</button>
            <h2>Cancel Order</h2>
            <p style="margin: 16px 0; color: #555;">Are you sure you want to cancel this order?</p>
            <div style="display: flex; gap: 12px; margin-top: 24px;">
                <button class="btn-checkout" style="background: #e0e0e0; color: #333; margin-top: 0;" onclick="closeCancelConfirm()">No, Keep Order</button>
                <button class="btn-checkout" style="background: #dc3545; margin-top: 0;" id="btn-confirm-cancel">Yes, Cancel</button>
            </div>
        </div>
    </div>
</body>
</html>
