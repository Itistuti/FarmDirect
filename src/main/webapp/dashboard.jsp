<!DOCTYPE html>
<%@ page import="agriconnect.farming.auth.SessionKeys" %>
<%
    Object role = session.getAttribute(SessionKeys.USER_ROLE);
    if (role == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    if ("FARMER".equalsIgnoreCase(role.toString())) {
        response.sendRedirect(request.getContextPath() + "/farmer-dashboard.jsp");
        return;
    }
%>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FarmDirect</title>
    <base href="${pageContext.request.contextPath}/">
    <link rel="stylesheet" href="assets/styles.css">
    <script src="assets/auth.js" defer></script>
</head>
<body data-page="dashboard">
<div class="container">
    <div class="card">
        <div class="actions" style="justify-content: space-between; align-items: center;">
            <div>
                <span class="badge">Dashboard</span>
                <h1 id="welcome-message">Checking session...</h1>
                <p class="muted">Role: <strong id="role-badge">-</strong></p>
            </div>
            <button type="button" onclick="logout()" style="max-width: 180px;">Logout</button>
        </div>

        <div id="flash-message" class="flash"></div>

        <div class="grid grid-2" style="margin-top: 24px;">
            <div id="farmer-panel" class="panel hidden">
                <h2>Farmer area</h2>
                <p>Use this area to manage vegetable listings, stock, and order progress.</p>
                <p><a href="farmer-dashboard.jsp">Go to Farmer Dashboard</a></p>
            </div>
            <div id="customer-panel" class="panel hidden">
                <h2>Customer area</h2>
                <p>Use this area to browse produce, place orders, and track your history.</p>
                <p><a href="dashboard.jsp">Go to Customer Dashboard</a></p>
            </div>
        </div>
    </div>
</div>
</body>
</html>
