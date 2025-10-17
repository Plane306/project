<%@ page import="model.User"%>
<%@ page import="model.Family"%>

<!-- Initialise database connection -->
<jsp:include page="/ConnServlet" flush="true"/>

<html>
    <head>
        <title>Close Family Account - Famney</title>
        <style>
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            }
            
            body {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                min-height: 100vh;
                display: flex;
                flex-direction: column;
            }
            
            .header {
                background: #2c3e50;
                padding: 1rem 0;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            }
            
            .nav-container {
                max-width: 1200px;
                margin: 0 auto;
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: 0 2rem;
            }
            
            .logo {
                font-size: 2rem;
                font-weight: 700;
                color: white;
                text-decoration: none;
            }
            
            .nav-menu {
                display: flex;
                gap: 2rem;
            }
            
            .nav-menu a, .nav-menu span {
                color: white;
                text-decoration: none;
                padding: 0.5rem 1rem;
                border-radius: 25px;
                transition: all 0.3s ease;
                border: 2px solid transparent;
            }
            
            .nav-menu a:hover {
                background: rgba(255, 255, 255, 0.2);
                border-color: rgba(255, 255, 255, 0.3);
            }
            
            .nav-menu span {
                font-weight: 600;
                opacity: 0.9;
            }
            
            .main-container {
                flex: 1;
                display: flex;
                justify-content: center;
                align-items: center;
                padding: 2rem;
            }
            
            .close-family-form {
                background: white;
                padding: 3rem;
                border-radius: 20px;
                box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
                max-width: 600px;
                width: 100%;
            }
            
            .form-header {
                text-align: center;
                margin-bottom: 2rem;
            }
            
            .form-header h1 {
                color: #dc3545;
                font-size: 2rem;
                margin-bottom: 0.5rem;
            }
            
            .form-header p {
                color: #6c757d;
                font-size: 1rem;
            }
            
            .warning-box {
                background: #fff3f3;
                border: 2px solid #dc3545;
                padding: 2rem;
                border-radius: 15px;
                margin-bottom: 2rem;
            }
            
            .warning-box h3 {
                color: #dc3545;
                margin-bottom: 1rem;
                font-size: 1.3rem;
            }
            
            .warning-box ul {
                margin-left: 1.5rem;
                margin-bottom: 1rem;
            }
            
            .warning-box li {
                color: #721c24;
                margin-bottom: 0.5rem;
                line-height: 1.6;
            }
            
            .warning-box .emphasis {
                color: #dc3545;
                font-weight: 700;
                font-size: 1.1rem;
                text-align: center;
                margin-top: 1rem;
                padding: 1rem;
                background: white;
                border-radius: 10px;
            }
            
            .family-info {
                background: #f8f9fa;
                padding: 1.5rem;
                border-radius: 10px;
                margin-bottom: 2rem;
                border-left: 5px solid #dc3545;
            }
            
            .family-info p {
                color: #495057;
                margin-bottom: 0.5rem;
            }
            
            .family-info strong {
                color: #2c3e50;
            }
            
            .form-group {
                margin-bottom: 1.5rem;
            }
            
            .form-group label {
                display: block;
                margin-bottom: 0.5rem;
                color: #2c3e50;
                font-weight: 600;
                font-size: 0.9rem;
            }
            
            .form-group input {
                width: 100%;
                padding: 1rem;
                border: 2px solid #e1e8ed;
                border-radius: 10px;
                font-size: 1rem;
                transition: all 0.3s ease;
                background: #f8f9fa;
            }
            
            .form-group input:focus {
                outline: none;
                border-color: #dc3545;
                background: white;
                box-shadow: 0 0 0 3px rgba(220, 53, 69, 0.1);
            }
            
            .form-actions {
                display: flex;
                gap: 1rem;
                margin-top: 2rem;
            }
            
            .btn-danger {
                flex: 1;
                background: #dc3545;
                color: white;
                padding: 1rem;
                border: none;
                border-radius: 10px;
                font-size: 1.1rem;
                font-weight: 600;
                cursor: pointer;
                transition: all 0.3s ease;
            }
            
            .btn-danger:hover {
                background: #c82333;
                transform: translateY(-2px);
                box-shadow: 0 10px 25px rgba(220, 53, 69, 0.3);
            }
            
            .btn-secondary {
                flex: 1;
                background: transparent;
                color: #667eea;
                padding: 1rem;
                border: 2px solid #667eea;
                border-radius: 10px;
                font-size: 1rem;
                font-weight: 600;
                text-decoration: none;
                text-align: center;
                cursor: pointer;
                transition: all 0.3s ease;
                display: inline-block;
            }
            
            .btn-secondary:hover {
                background: #667eea;
                color: white;
            }
            
            .error-message {
                background: #f8d7da;
                color: #721c24;
                padding: 1rem;
                border-radius: 10px;
                margin-bottom: 1rem;
                border: 1px solid #f5c6cb;
                text-align: center;
            }
            
            .footer {
                background: #2c3e50;
                color: white;
                padding: 2rem;
                text-align: center;
            }
            
            @media (max-width: 768px) {
                .close-family-form {
                    margin: 1rem;
                    padding: 2rem;
                }
                
                .form-actions {
                    flex-direction: column;
                }
                
                .nav-menu {
                    gap: 1rem;
                }
            }
        </style>
    </head>
    
    <body>
        <%
            // Check if user is logged in and is Family Head
            User user = (User) session.getAttribute("user");
            Family family = (Family) session.getAttribute("family");
            
            if (user == null || family == null) {
                response.sendRedirect("login.jsp");
                return;
            }
            
            // Only Family Head can access this page
            if (!"Family Head".equals(user.getRole())) {
                response.sendRedirect("main.jsp");
                return;
            }
            
            // Get error message if any
            String errorMessage = (String) session.getAttribute("errorMessage");
            if (errorMessage != null) {
                session.removeAttribute("errorMessage");
            }
        %>
        
        <header class="header">
            <div class="nav-container">
                <a href="index.jsp" class="logo">Famney</a>
                <nav class="nav-menu">
                    <span>Family Head: <%= user.getFullName() %></span>
                    <a href="family_management.jsp">Family Management</a>
                    <a href="main.jsp">Dashboard</a>
                </nav>
            </div>
        </header>
        
        <div class="main-container">
            <div class="close-family-form">
                <div class="form-header">
                    <h1>Close Family Account</h1>
                    <p>Permanently close your family financial management account</p>
                </div>
                
                <% if (errorMessage != null) { %>
                    <div class="error-message">
                        <%= errorMessage %>
                    </div>
                <% } %>
                
                <div class="warning-box">
                    <h3>WARNING: This Action Cannot Be Undone</h3>
                    <p>Closing your family account will have the following consequences:</p>
                    <ul>
                        <li>All family members will be <strong>immediately logged out</strong></li>
                        <li>All user accounts will be <strong>deactivated</strong> (no one can login)</li>
                        <li>The family account will be marked as <strong>inactive</strong></li>
                        <li>All financial data will become <strong>inaccessible</strong></li>
                        <li>This action is <strong>permanent and irreversible</strong></li>
                    </ul>
                    <div class="emphasis">
                        ⚠️ THIS ACTION CANNOT BE UNDONE ⚠️
                    </div>
                </div>
                
                <div class="family-info">
                    <p><strong>Family Name:</strong> <%= family.getFamilyName() %></p>
                    <p><strong>Family Code:</strong> <%= family.getFamilyCode() %></p>
                    <p><strong>Total Members:</strong> <%= family.getMemberCount() %></p>
                    <p><strong>Your Email:</strong> <%= user.getEmail() %></p>
                </div>
                
                <form action="CloseFamilyServlet" method="post" onsubmit="return confirm('Are you absolutely sure? This will permanently close the family account and deactivate all members.')">
                    <div class="form-group">
                        <label for="password">Enter Your Password to Confirm *</label>
                        <input type="password" id="password" name="password" 
                               placeholder="Enter your current password" required>
                        <small style="color: #6c757d; display: block; margin-top: 0.5rem;">
                            You must enter your password to confirm this critical action
                        </small>
                    </div>
                    
                    <div class="form-actions">
                        <button type="submit" class="btn-danger">Close Family Account</button>
                        <a href="family_management.jsp" class="btn-secondary">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
        
        <footer class="footer">
            <div class="container">
                <p>&copy; 2025 Famney - Family Financial Management System</p>
            </div>
        </footer>
    </body>
</html>