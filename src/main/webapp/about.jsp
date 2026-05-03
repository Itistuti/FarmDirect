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
    <title>About Us - FarmDirect</title>
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
                <li><a href="about.jsp" style="color: #1bcc4b;">About Us</a></li>
                <li><a href="services.jsp">Services</a></li>
            </ul>
            <a href="<%= dashboardLink %>" class="btn-primary"><%= authBtnText %></a>
        </div>
    </nav>

    <div class="container" style="max-width: 800px; padding: 60px 20px;">
        <h1 style="font-size: 36px; color: #1f2937; margin-bottom: 32px; text-align: center;">About FarmDirect</h1>
        
        <div class="card" style="padding: 40px; font-size: 16px; line-height: 1.8; color: #4b5563;">
            <p style="margin-bottom: 24px;">FarmDirect is a platform designed to connect farmers directly with customers, eliminating the need for middlemen in the vegetable supply chain. Our goal is to ensure that farmers receive fair prices for their produce while customers gain access to fresh, affordable vegetables in a simple and convenient way. The system promotes transparency, efficiency, and direct communication, helping create a more balanced and trustworthy marketplace for everyday use.</p>

            <p style="margin-bottom: 24px;">As a farmer, you can create and manage your account, list your vegetables with details such as price and quantity, update or remove products, and monitor incoming orders while tracking your overall sales and earnings. This allows you to have full control over your products and pricing without relying on intermediaries.</p>

            <p style="margin-bottom: 24px;">As a customer, you can register and log in to browse a wide range of fresh vegetables, search products by name or category, view detailed product information, add items to your cart, place orders, and track the status of your purchases. You can also view your order history, making the entire shopping experience smooth, transparent, and user-friendly.</p>

            <p>Overall, FarmDirect aims to support local farmers, reduce unnecessary costs, and provide customers with a reliable and efficient way to purchase fresh vegetables directly from the source.</p>
        </div>
    </div>

    <footer class="footer" style="padding: 24px 16px;">
        <div class="container" style="text-align: center; color: #9ca3af;">
            <p style="margin: 0;">&copy; 2026 FarmDirect. All rights reserved. Connecting farms to tables.</p>
        </div>
    </footer>
</body>
</html>
