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

    response.sendRedirect(request.getContextPath() + "/dashboard.jsp");
%>
