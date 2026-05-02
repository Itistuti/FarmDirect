<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign up - FarmDirect</title>
    <base href="${pageContext.request.contextPath}/">
    <link rel="stylesheet" href="assets/styles.css">
    <script src="assets/auth.js" defer></script>
</head>

<body>
    <div class="container">
        <div class="card" style="max-width: 680px; margin: 0 auto;">
            <span class="badge">Create account</span>
            <h1>Register as a farmer or customer</h1>
            <p class="muted">Choose the role that matches how you want to use FarmDirect.</p>
            <% String flashType=null; String flashMessage=null; Object
                sessionFlashType=session.getAttribute("FLASH_TYPE"); Object
                sessionFlashMessage=session.getAttribute("FLASH_MESSAGE"); if (sessionFlashType instanceof String) {
                flashType=(String) sessionFlashType; } if (sessionFlashMessage instanceof String) {
                flashMessage=(String) sessionFlashMessage; } if (flashMessage==null || flashMessage.isBlank()) { String
                error=request.getParameter("error"); String message=request.getParameter("message"); if (error !=null &&
                !error.isBlank()) { flashType="error" ; flashMessage=error; } else if (message !=null &&
                !message.isBlank()) { flashType="success" ; flashMessage=message; } } if (flashMessage !=null &&
                !flashMessage.isBlank()) { session.removeAttribute("FLASH_TYPE");
                session.removeAttribute("FLASH_MESSAGE"); } String flashCss="flash" ; if (flashMessage==null ||
                flashMessage.isBlank()) { flashCss +=" hidden" ; } else if ("error".equalsIgnoreCase(flashType)) {
                flashCss +=" error" ; } else { flashCss +=" success" ; } String safeFlash=flashMessage==null ? "" :
                flashMessage .replace("&", "&amp;" ) .replace("<", "&lt;" ) .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
                %>
                <div id="flash-message" class="<%= flashCss %>">
                    <%= safeFlash %>
                </div>
                <form method="post" action="api/auth/signup" class="grid grid-2" style="gap: 16px;">
                    <div>
                        <label for="fullName">Full name</label>
                        <input id="fullName" name="fullName" type="text" placeholder="Your name" required>
                    </div>
                    <div>
                        <label for="email">Email address</label>
                        <input id="email" name="email" type="email" placeholder="Enter your Email" required>
                    </div>
                    <div>
                        <label for="password">Password</label>
                        <input id="password" name="password" type="password" placeholder="At least 8 characters"
                            minlength="8" required>
                    </div>
                    <div>
                        <label for="role">Account type</label>
                        <select id="role" name="role" required>
                            <option value="">Select a role</option>
                            <option value="FARMER">Farmer</option>
                            <option value="CUSTOMER">Customer</option>
                        </select>
                    </div>
                    <div>
                        <label for="location">Location</label>
                        <input id="location" name="location" type="text" placeholder="Enter your location (e.g. City, State)">
                    </div>
                    <div>
                        <label for="phone">Phone Number</label>
                        <input id="phone" name="phone" type="tel" placeholder="Enter your phone number">
                    </div>
                    <div style="grid-column: 1 / -1;">
                        <button type="submit">Create account</button>
                    </div>
                </form>
                <p class="muted" style="margin-top: 16px;">Already registered? <a href="login.jsp">Login</a></p>
        </div>
    </div>
</body>

</html>