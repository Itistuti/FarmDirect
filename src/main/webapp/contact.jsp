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
    <title>Contact Us - FarmDirect</title>
    <base href="${pageContext.request.contextPath}/">
    <link rel="stylesheet" href="assets/styles.css">
    <style>
        .contact-header {
            background: linear-gradient(135deg, rgba(163, 177, 138, 0.25) 0%, #FAF9F6 100%);
            padding: 60px 16px;
            text-align: center;
        }
        .contact-header h1 {
            font-size: 40px;
            color: var(--color-forest);
            margin-bottom: 16px;
        }
        .contact-header p {
            font-size: 18px;
            color: var(--color-muted);
            max-width: 600px;
            margin: 0 auto;
            line-height: 1.6;
        }
        .contact-section {
            padding: 60px 16px;
        }
        .contact-info-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 24px;
            margin-bottom: 60px;
        }
        .contact-info-card {
            background: var(--color-surface);
            padding: 40px 32px;
            border-radius: 16px;
            border: 1px solid var(--color-border);
            box-shadow: var(--shadow-card);
            text-align: center;
        }
        .contact-info-card h4 {
            color: var(--color-forest);
            margin-bottom: 12px;
            font-size: 20px;
        }
        .contact-info-card p {
            color: var(--color-muted);
            line-height: 1.6;
            margin: 0;
        }
        .support-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 24px;
            margin-top: 40px;
        }
        .support-card {
            background: var(--color-surface);
            border-radius: 16px;
            padding: 32px;
            box-shadow: var(--shadow-card);
            border: 1px solid var(--color-border);
            text-align: center;
        }
        .support-card h4 {
            color: var(--color-forest);
            font-size: 20px;
            margin-bottom: 12px;
        }
        .support-card p {
            color: var(--color-muted);
            margin-bottom: 16px;
        }
        .support-card a {
            font-weight: bold;
            color: var(--color-warm);
            text-decoration: none;
        }
        .social-links {
            display: flex;
            justify-content: center;
            gap: 24px;
            margin-top: 24px;
        }
        .social-links a {
            color: var(--color-primary);
            text-decoration: none;
            font-weight: bold;
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
                <li><a href="services.jsp">Services</a></li>
                <li><a href="contact.jsp" style="color: var(--color-primary);">Contact Us</a></li>
            </ul>
            <a href="<%= dashboardLink %>" class="btn-primary"><%= authBtnText %></a>
        </div>
    </nav>

    <header class="contact-header">
        <div class="container">
            <h1>Get in Touch</h1>
            <p>Need help with buying, selling, or using the platform? We're here for you. Reach out to our team using the form below or our direct contact options.</p>
        </div>
    </header>

    <section class="contact-section container">
        <div class="contact-info-grid">
            <div class="contact-info-card">
                <h4>Phone</h4>
                <p>+977 98*******<br><span style="font-size: 0.9em; color: var(--color-muted);">Mon-Fri from 9am to 6pm</span></p>
            </div>

            <div class="contact-info-card">
                <h4>Email</h4>
                <p>support@farmdirect.com.np<br><span style="font-size: 0.9em; color: var(--color-primary); font-weight: 500;">Responses within 24 hours</span></p>
            </div>

            <div class="contact-info-card">
                <h4>Location</h4>
                <p>Kathmandu, Nepal<br><span style="font-size: 0.9em; color: var(--color-muted);">Proudly serving the entire nation.</span></p>
            </div>
        </div>
        
        <h2 class="section-title" style="margin-top: 60px; margin-bottom: 32px;">Dedicated Support</h2>
        <div class="support-grid">
            <div class="support-card">
                <h4>Farmer Support</h4>
                <p>Assistance with listings, payments, and account management for our farming partners.</p>
                <a href="mailto:farmers@farmdirect.com.np">farmers@farmdirect.com.np</a>
            </div>
            <div class="support-card">
                <h4>Customer Support</h4>
                <p>Help with finding products, tracking orders, and general shopping inquiries.</p>
                <a href="mailto:customers@farmdirect.com.np">customers@farmdirect.com.np</a>
            </div>
        </div>

        <div style="text-align: center; margin-top: 60px; margin-bottom: 24px;">
            <h3 style="color: var(--color-forest); font-size: 20px;">Helpful Links</h3>
            <ul style="list-style: none; padding: 0; margin: 16px 0 0 0; display: flex; justify-content: center; gap: 24px;">
                <li><a href="services.jsp" style="font-weight: 500; color: var(--color-primary); text-decoration: none;">How FarmDirect Works</a></li>
            </ul>
        </div>

        <div style="text-align: center; margin-top: 60px;">
            <h3 style="color: var(--color-forest);">Connect with us on Social Media</h3>
            <div class="social-links">
                <a href="#">Facebook</a>
                <a href="#">Instagram</a>
                <a href="#">WhatsApp</a>
            </div>
        </div>
    </section>

    <footer class="footer" style="padding: 24px 16px;">
        <div class="container" style="text-align: center; color: var(--color-muted);">
            <p style="margin: 0;">&copy; 2026 FarmDirect. All rights reserved. Connecting farms to tables.</p>
        </div>
    </footer>
</body>
</html>
