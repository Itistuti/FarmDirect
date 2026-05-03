<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="agriconnect.farming.auth.SessionKeys" %>
<%
    Object role = session.getAttribute(SessionKeys.USER_ROLE);
    boolean isLoggedIn = (role != null);
    String dashboardLink = isLoggedIn ? ("FARMER".equalsIgnoreCase(role.toString()) ? "farmer-dashboard.jsp" : "customer-dashboard.jsp") : "login.jsp";
    String authBtnText = isLoggedIn ? "Go to Dashboard" : "Login / Sign Up";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Services - FarmDirect</title>
    <base href="${pageContext.request.contextPath}/">
<style>
* {
    box-sizing: border-box;
}

body {
    margin: 0;
    font-family: Arial, Helvetica, sans-serif;
    background: linear-gradient(180deg, #eef7e8 0%, #ffffff 100%);
    color: #1f2937;
}

.container {
    max-width: 960px;
    margin: 0 auto;
    padding: 32px 16px;
}

.card {
    background: #ffffff;
    border-radius: 16px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
    padding: 24px;
}

.grid {
    display: grid;
    gap: 24px;
}

@media (min-width: 768px) {
    .grid-2 {
        grid-template-columns: repeat(2, minmax(0, 1fr));
    }
}

h1, h2, h3 {
    margin-top: 0;
}

a {
    color: #166534;
    text-decoration: none;
}

a:hover {
    text-decoration: underline;
}

label {
    display: block;
    margin-bottom: 8px;
    font-weight: 600;
}

input, select, button {
    width: 100%;
    padding: 12px 14px;
    border: 1px solid #cbd5e1;
    border-radius: 10px;
    font: inherit;
}

input:focus, select:focus {
    outline: 2px solid #86efac;
    border-color: #16a34a;
}

button {
    background: #16a34a;
    color: #ffffff;
    border: none;
    cursor: pointer;
    font-weight: 700;
}

button:hover {
    background: #15803d;
}

.actions {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
}

.actions a, .actions button {
    width: auto;
    display: inline-flex;
    align-items: center;
    justify-content: center;
}

.muted {
    color: #6b7280;
}

.flash {
    margin-bottom: 16px;
    padding: 12px 14px;
    border-radius: 10px;
    display: none;
}

.flash.error {
    display: block;
    background: #fef2f2;
    color: #991b1b;
    border: 1px solid #fecaca;
}

.flash.success {
    display: block;
    background: #f0fdf4;
    color: #166534;
    border: 1px solid #bbf7d0;
}

.badge {
    display: inline-block;
    padding: 4px 10px;
    border-radius: 999px;
    background: #dcfce7;
    color: #166534;
    font-size: 0.85rem;
    font-weight: 700;
}

.panel {
    border: 1px solid #e5e7eb;
    border-radius: 14px;
    padding: 18px;
    background: #f9fafb;
}

.hidden {
    display: none !important;
}

/* Landing Page Styles */
.landing-page {
    background: #ffffff;
}

.navbar {
    background: #ffffff;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
    position: sticky;
    top: 0;
    z-index: 100;
}

.navbar-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 32px;
}

.brand-logo {
    font-size: 24px;
    font-weight: 800;
    color: #1bcc4b;
    text-decoration: none;
}

.nav-links {
    list-style: none;
    display: flex;
    gap: 32px;
    margin: 0;
    padding: 0;
}

.nav-links a {
    color: #4b5563;
    font-weight: 600;
    text-decoration: none;
    transition: color 0.2s;
}

.nav-links a:hover {
    color: #1bcc4b;
    text-decoration: none;
}

.btn-primary {
    background: #1bcc4b;
    color: white;
    padding: 10px 20px;
    border-radius: 8px;
    font-weight: bold;
    text-decoration: none;
    transition: background 0.2s;
    border: none;
    display: inline-block;
}

.btn-primary:hover {
    background: #15a339;
    text-decoration: none;
}

.btn-secondary {
    background: #ffffff;
    color: #1bcc4b;
    border: 2px solid #1bcc4b;
    padding: 10px 20px;
    border-radius: 8px;
    font-weight: bold;
    text-decoration: none;
    transition: all 0.2s;
    display: inline-block;
}

.btn-secondary:hover {
    background: #e0f8e6;
    text-decoration: none;
}

.btn-large {
    padding: 14px 28px;
    font-size: 16px;
}

.hero-section {
    background: #e0f8e6;
    padding: 80px 16px;
    text-align: center;
}

.hero-content {
    max-width: 800px;
    margin: 0 auto;
}

.hero-content h1 {
    font-size: 48px;
    color: #1f2937;
    margin-bottom: 24px;
    line-height: 1.2;
}

.hero-content p {
    font-size: 18px;
    color: #4b5563;
    margin-bottom: 40px;
    line-height: 1.6;
}

.hero-buttons {
    display: flex;
    gap: 16px;
    justify-content: center;
    flex-wrap: wrap;
}

.section-title {
    text-align: center;
    font-size: 32px;
    color: #1f2937;
    margin-bottom: 48px;
}

.how-it-works {
    padding: 80px 16px;
}

.steps-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 32px;
}

.step-card {
    text-align: center;
    padding: 32px;
    background: white;
    border-radius: 16px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
}

.step-icon {
    width: 64px;
    height: 64px;
    background: #1bcc4b;
    color: white;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24px;
    font-weight: bold;
    margin: 0 auto 24px;
}

.step-card h3 {
    margin-bottom: 16px;
    color: #1f2937;
}

.step-card p {
    color: #6b7280;
    line-height: 1.6;
}

.benefits-section {
    background: #e0f8e6;
    padding: 40px 16px;
    color: #1f2937;
}

.benefits-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 32px;
}

.benefit-item {
    background: #ffffff;
    padding: 24px;
    border-radius: 16px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}

.benefit-item h4 {
    font-size: 20px;
    margin-bottom: 12px;
}

.benefit-item p {
    color: #4b5563;
    line-height: 1.6;
}

.footer {
    background: #1f2937;
    color: #f9fafb;
    padding: 64px 16px 24px;
}

.footer-content {
    display: flex;
    justify-content: space-between;
    flex-wrap: wrap;
    gap: 40px;
    margin-bottom: 40px;
}

.footer-brand h3 {
    color: #1bcc4b;
    font-size: 24px;
    margin-bottom: 16px;
}

.footer-brand p {
    color: #9ca3af;
    max-width: 300px;
}

.footer-contact h4 {
    margin-bottom: 16px;
    color: white;
}

.footer-contact p {
    color: #9ca3af;
    margin: 8px 0;
}

.footer-bottom {
    text-align: center;
    padding-top: 24px;
    border-top: 1px solid #374151;
    color: #9ca3af;
}

@media (max-width: 768px) {
    .hero-content h1 {
        font-size: 36px;
    }
    .nav-links {
        display: none; /* simple mobile nav approach for now */
    }
}

    </style>
</head>
<body class="landing-page">
    <nav class="navbar">
        <div class="container navbar-container">
            <a href="index.jsp" class="brand-logo">FarmDirect</a>
            <ul class="nav-links">
                <li><a href="index.jsp">Home</a></li>
                <li><a href="about.jsp">About Us</a></li>
                <li><a href="services.jsp" style="color: #1bcc4b;">Services</a></li>
            </ul>
            <a href="<%= dashboardLink %>" class="btn-primary"><%= authBtnText %></a>
        </div>
    </nav>

    <div class="container" style="max-width: 900px; padding: 80px 20px;">
        <div style="text-align: center; margin-bottom: 48px;">
            <h1 style="font-size: 40px; color: #1f2937; margin-bottom: 16px;">Our Services</h1>
            <p style="font-size: 18px; color: #6b7280; max-width: 600px; margin: 0 auto;">Delivering a seamless farm-to-table experience</p>
        </div>
        
        <div class="card" style="padding: 48px; box-shadow: 0 12px 40px rgba(0,0,0,0.06);">
            <p style="font-size: 18px; line-height: 1.8; color: #4b5563; text-align: justify;">
                <strong style="color: #166534; font-size: 20px;">FarmDirect</strong> is a reliable platform that connects farmers directly with customers, ensuring fair pricing, fresh products, and a transparent buying process. It offers a simple and convenient way for farmers to manage their products and for customers to browse, order, and track vegetables with ease. By eliminating middlemen, FarmDirect supports local farmers while providing customers with better quality and value, making it a smart and efficient choice for everyday vegetable shopping.
            </p>
            
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 24px; margin-top: 48px;">
                <div style="background: #e0f8e6; padding: 24px; border-radius: 12px; text-align: left;">
                    <strong style="color: #166534; font-size: 18px; display: block; margin-bottom: 8px;">1. Fresh & Quality Produce</strong>
                    <p style="color: #4b5563; margin: 0; font-size: 15px;">Ensures vegetables are fresh, directly sourced from farmers.</p>
                </div>
                
                <div style="background: #e0f8e6; padding: 24px; border-radius: 12px; text-align: left;">
                    <strong style="color: #166534; font-size: 18px; display: block; margin-bottom: 8px;">2. Real-Time Product Availability</strong>
                    <p style="color: #4b5563; margin: 0; font-size: 15px;">Customers can see what is available right now from farmers.</p>
                </div>

                <div style="background: #e0f8e6; padding: 24px; border-radius: 12px; text-align: left;">
                    <strong style="color: #166534; font-size: 18px; display: block; margin-bottom: 8px;">3. Order Tracking</strong>
                    <p style="color: #4b5563; margin: 0; font-size: 15px;">Users can track their orders from confirmation to delivery/pickup.</p>
                </div>

                <div style="background: #e0f8e6; padding: 24px; border-radius: 12px; text-align: left;">
                    <strong style="color: #166534; font-size: 18px; display: block; margin-bottom: 8px;">4. Cash on Delivery / Offline Payment</strong>
                    <p style="color: #4b5563; margin: 0; font-size: 15px;">Customers can pay when receiving or collecting the vegetables.</p>
                </div>

                <div style="background: #e0f8e6; padding: 24px; border-radius: 12px; text-align: left;">
                    <strong style="color: #166534; font-size: 18px; display: block; margin-bottom: 8px;">5. Farmer Product Management</strong>
                    <p style="color: #4b5563; margin: 0; font-size: 15px;">Farmers can easily add, update, and manage their products.</p>
                </div>

                <div style="background: #e0f8e6; padding: 24px; border-radius: 12px; text-align: left;">
                    <strong style="color: #166534; font-size: 18px; display: block; margin-bottom: 8px;">6. Transparent Pricing</strong>
                    <p style="color: #4b5563; margin: 0; font-size: 15px;">No hidden charges, prices are set fairly between farmer and customer.</p>
                </div>
            </div>
        </div>
    </div>

    <footer class="footer" style="padding: 24px 16px;">
        <div class="container" style="text-align: center; color: #9ca3af;">
            <p style="margin: 0;">&copy; 2026 FarmDirect. All rights reserved. Connecting farms to tables.</p>
        </div>
    </footer>
</body>
</html>
