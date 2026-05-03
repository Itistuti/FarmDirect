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
            background: linear-gradient(135deg, #e0f8e6 0%, #ffffff 100%);
            padding: 60px 16px;
            text-align: center;
        }
        .contact-header h1 {
            font-size: 40px;
            color: #1f2937;
            margin-bottom: 16px;
        }
        .contact-header p {
            font-size: 18px;
            color: #4b5563;
            max-width: 600px;
            margin: 0 auto;
            line-height: 1.6;
        }
        .contact-section {
            padding: 60px 16px;
        }
        .contact-info-card {
            background: #f9fafb;
            padding: 32px;
            border-radius: 16px;
            border: 1px solid #e5e7eb;
        }
        .contact-info-card h3 {
            color: #1f2937;
            margin-bottom: 24px;
            font-size: 24px;
        }
        .info-item {
            display: flex;
            align-items: flex-start;
            gap: 16px;
            margin-bottom: 24px;
        }
        .info-item .icon {
            color: #1bcc4b;
        }
        .info-item h4 {
            margin: 0 0 8px 0;
            color: #374151;
        }
        .info-item p {
            margin: 0;
            color: #6b7280;
            line-height: 1.5;
        }
        .support-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 24px;
            margin-top: 40px;
        }
        .support-card {
            background: #ffffff;
            border-radius: 12px;
            padding: 24px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
            border: 1px solid #f3f4f6;
            text-align: center;
        }
        .support-card h4 {
            color: #166534;
            font-size: 20px;
            margin-bottom: 12px;
        }
        .support-card p {
            color: #6b7280;
            margin-bottom: 16px;
        }
        .support-card a {
            font-weight: bold;
        }
        .social-links {
            display: flex;
            justify-content: center;
            gap: 24px;
            margin-top: 24px;
        }
        .social-links a {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            width: 48px;
            height: 48px;
            border-radius: 50%;
            background: #e0f8e6;
            color: #1bcc4b;
            transition: all 0.2s;
        }
        .social-links a:hover {
            background: #1bcc4b;
            color: #ffffff;
        }
        textarea {
            width: 100%;
            padding: 12px 14px;
            border: 1px solid #cbd5e1;
            border-radius: 10px;
            font: inherit;
            resize: vertical;
            min-height: 120px;
        }
        textarea:focus {
            outline: 2px solid #86efac;
            border-color: #16a34a;
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
                <li><a href="contact.jsp" style="color: #1bcc4b;">Contact Us</a></li>
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
        <div class="grid grid-2">
            <!-- Left Column: Form -->
            <div class="card">
                <h2 style="margin-bottom: 24px; color: #1f2937;">Send us a Message</h2>
                <form action="#" method="POST" class="grid" style="gap: 16px;">
                    <div>
                        <label for="fullName">Full Name</label>
                        <input type="text" id="fullName" name="fullName" placeholder="Your Name" required>
                    </div>
                    <div>
                        <label for="email">Email Address</label>
                        <input type="email" id="email" name="email" placeholder="you@example.com" required>
                    </div>

                    <div>
                        <label for="message">Message</label>
                        <textarea id="message" name="message" placeholder="How can we help you?" required></textarea>
                    </div>
                    <button type="submit" class="btn-primary" style="margin-top: 8px;">Send Message</button>
                </form>
            </div>

            <!-- Right Column: Info & More -->
            <div>
                <div class="contact-info-card" style="margin-bottom: 24px;">
                    <h3>Direct Contact</h3>
                    
                    <div class="info-item">
                        <div class="icon">
                            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"></path></svg>
                        </div>
                        <div>
                            <h4>Phone</h4>
                            <p>+977 98*******<br><span style="font-size: 0.9em; color: #9ca3af;">Mon-Fri from 9am to 6pm</span></p>
                        </div>
                    </div>

                    <div class="info-item">
                        <div class="icon">
                            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="4" width="20" height="16" rx="2" ry="2"></rect><path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7"></path></svg>
                        </div>
                        <div>
                            <h4>Email</h4>
                            <p>support@farmdirect.com.np<br><span style="font-size: 0.9em; color: #1bcc4b; font-weight: 500;">Responses provided within 24 hours</span></p>
                        </div>
                    </div>

                    <div class="info-item">
                        <div class="icon">
                            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z"></path><circle cx="12" cy="10" r="3"></circle></svg>
                        </div>
                        <div>
                            <h4>Location</h4>
                            <p>Kathmandu, Nepal<br><span style="font-size: 0.9em; color: #9ca3af;">Proudly serving the entire nation.</span></p>
                        </div>
                    </div>
                </div>

                <div class="card" style="margin-bottom: 24px;">
                    <h3 style="margin-bottom: 16px; font-size: 20px;">Helpful Links</h3>
                    <ul style="list-style: none; padding: 0; margin: 0; display: flex; flex-direction: column; gap: 12px;">
                        <li><a href="#" style="display: flex; align-items: center; gap: 8px;"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m9 18 6-6-6-6"/></svg> Frequently Asked Questions (FAQ)</a></li>
                        <li><a href="#" style="display: flex; align-items: center; gap: 8px;"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m9 18 6-6-6-6"/></svg> How FarmDirect Works</a></li>
                    </ul>
                </div>
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

        <div style="text-align: center; margin-top: 60px;">
            <h3 style="color: #374151;">Connect with us on Social Media</h3>
            <div class="social-links">
                <a href="#" aria-label="Facebook">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 2h-3a5 5 0 0 0-5 5v3H7v4h3v8h4v-8h3l1-4h-4V7a1 1 0 0 1 1-1h3z"></path></svg>
                </a>
                <a href="#" aria-label="Instagram">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="2" width="20" height="20" rx="5" ry="5"></rect><path d="M16 11.37A4 4 0 1 1 12.63 8 4 4 0 0 1 16 11.37z"></path><line x1="17.5" y1="6.5" x2="17.51" y2="6.5"></line></svg>
                </a>
                <a href="#" aria-label="WhatsApp">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"></path></svg>
                </a>
            </div>
        </div>
    </section>

    <footer class="footer">
        <div class="container footer-content">
            <div class="footer-brand">
                <h3>FarmDirect</h3>
                <p>Connecting farmers and customers directly for fresh, fair, and sustainable local produce.</p>
            </div>
            <div class="footer-contact">
                <h4>Quick Links</h4>
                <ul style="list-style: none; padding: 0; margin: 0; display: flex; flex-direction: column; gap: 8px;">
                    <li><a href="privacy.jsp" style="color: #9ca3af; text-decoration: none;">Privacy Policy</a></li>
                    <li><a href="terms.jsp" style="color: #9ca3af; text-decoration: none;">Terms of Service</a></li>
                </ul>
            </div>
        </div>
        <div class="footer-bottom">
            <div class="container" style="padding-top: 0; padding-bottom: 0;">
                <p style="margin: 0;">&copy; 2026 FarmDirect. All rights reserved. Connecting farms to tables.</p>
            </div>
        </div>
    </footer>
</body>
</html>
