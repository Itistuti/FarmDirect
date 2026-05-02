<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - FarmDirect</title>
    <base href="${pageContext.request.contextPath}/">
    <link rel="stylesheet" href="assets/styles.css">
    <script src="assets/auth.js" defer></script>
</head>

<body>
    <div class="container">
        <div class="card" style="max-width: 560px; margin: 0 auto;">
            <span class="badge">Login</span>
            <h1>Welcome to FarmDirect</h1>
            <p class="muted">Use your farmer or customer account to continue.</p>
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
                <form method="post" action="api/auth/login" class="grid" style="gap: 16px;">
                    <div>
                        <label for="email">Email address</label>
                        <input id="email" name="email" type="email" placeholder="Enter Your Email" required>
                    </div>
                    <div>
                        <label for="password">Password</label>
                        <input id="password" name="password" type="password" placeholder="Enter Your Password" required>
                    </div>
                    <button type="submit">Login</button>
                </form>
                <p class="muted" style="margin-top: 16px;">New here? <a href="signup.jsp">Create an account</a></p>
        </div>
    </div>
</body>

</html>