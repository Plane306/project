<%@ page import="model.User"%>
<%@ page import="model.Family"%>

<!-- Initialise database connection -->
<jsp:include page="/ConnServlet" flush="true"/>

<html>
    <head>
        <title>Welcome to Your Family - Famney</title>
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
            
            .success-card {
                background: white;
                padding: 3rem;
                border-radius: 20px;
                box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
                max-width: 600px;
                width: 100%;
                text-align: center;
            }
            
            .success-icon {
                width: 80px;
                height: 80px;
                background: linear-gradient(135deg, #28a745, #20c997);
                color: white;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                margin: 0 auto 2rem;
                font-size: 2.5rem;
                font-weight: bold;
            }
            
            .success-card h1 {
                color: #2c3e50;
                font-size: 2rem;
                margin-bottom: 1rem;
            }
            
            .success-card p {
                color: #6c757d;
                font-size: 1.1rem;
                line-height: 1.6;
                margin-bottom: 2rem;
            }
            
            .family-info {
                background: #f8f9fa;
                padding: 2rem;
                border-radius: 15px;
                margin: 2rem 0;
                text-align: left;
                border-left: 5px solid #667eea;
            }
            
            .family-info h3 {
                color: #2c3e50;
                margin-bottom: 1rem;
                font-size: 1.3rem;
            }
            
            .info-item {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 0.8rem;
                padding: 0.5rem 0;
                border-bottom: 1px solid #e9ecef;
            }
            
            .info-item:last-child {
                border-bottom: none;
            }
            
            .info-label {
                font-weight: 600;
                color: #495057;
            }
            
            .info-value {
                color: #2c3e50;
                font-weight: 500;
            }
            
            .family-code-highlight {
                background: linear-gradient(135deg, #667eea, #764ba2);
                color: white;
                padding: 1.5rem;
                border-radius: 15px;
                font-size: 1.8rem;
                font-weight: bold;
                letter-spacing: 2px;
                margin: 1.5rem 0;
            }
            
            .code-instruction {
                background: #fff3cd;
                color: #856404;
                padding: 1rem;
                border-radius: 10px;
                margin: 1rem 0;
                border: 1px solid #ffeaa7;
            }
            
            .code-instruction h4 {
                margin-bottom: 0.5rem;
                font-size: 1.1rem;
            }
            
            .btn-primary {
                background: linear-gradient(135deg, #667eea, #764ba2);
                color: white;
                padding: 1rem 2rem;
                border: none;
                border-radius: 50px;
                font-size: 1.1rem;
                font-weight: 600;
                cursor: pointer;
                transition: all 0.3s ease;
                margin: 0.5rem;
                text-decoration: none;
                display: inline-block;
            }
            
            .btn-primary:hover {
                transform: translateY(-2px);
                box-shadow: 0 10px 25px rgba(102, 126, 234, 0.3);
            }
            
            .btn-secondary {
                background: transparent;
                color: #667eea;
                padding: 1rem 2rem;
                border: 2px solid #667eea;
                border-radius: 50px;
                font-size: 1.1rem;
                font-weight: 600;
                cursor: pointer;
                transition: all 0.3s ease;
                margin: 0.5rem;
                text-decoration: none;
                display: inline-block;
            }
            
            .btn-secondary:hover {
                background: #667eea;
                color: white;
            }
            
            .footer {
                background: #2c3e50;
                color: white;
                padding: 2rem;
                text-align: center;
            }
            
            @media (max-width: 768px) {
                .success-card {
                    margin: 1rem;
                    padding: 2rem;
                }
                
                .info-item {
                    flex-direction: column;
                    gap: 0.5rem;
                }
                
                .nav-menu {
                    gap: 1rem;
                }
            }
        </style>
    </head>
    
    <body>
        <%
            // Check if user is logged in
            User user = (User) session.getAttribute("user");
            Family family = (Family) session.getAttribute("family");
            
            if (user == null || family == null) {
                response.sendRedirect("login.jsp");
                return;
            }
            
            // Get success message
            String successMessage = (String) session.getAttribute("successMessage");
            if (successMessage != null) {
                session.removeAttribute("successMessage");
            }
            
            // Determine if new family or joined existing
            boolean isNewFamily = "Family Head".equals(user.getRole());
        %>
        
        <header class="header">
            <div class="nav-container">
                <a href="main.jsp" class="logo">Famney</a>
                <nav class="nav-menu">
                    <span>Welcome, <%= user.getFullName() %></span>
                    <a href="main.jsp">Dashboard</a>
                    <a href="LogoutServlet">Logout</a>
                </nav>
            </div>
        </header>
        
        <div class="main-container">
            <div class="success-card">
                <div class="success-icon">&#10003;</div>
                
                <% if (isNewFamily) { %>
                    <h1>Family Account Created Successfully!</h1>
                    <p>Congratulations! You've successfully created your family financial management account. You're now ready to start tracking expenses, managing budgets, and achieving your savings goals together.</p>
                <% } else { %>
                    <h1>Welcome to the Family!</h1>
                    <p>You've successfully joined <%= family.getFamilyName() %>. You now have access to your family's financial dashboard and can start contributing to your shared financial goals.</p>
                <% } %>
                
                <div class="family-info">
                    <h3>Your Account Details</h3>
                    <div class="info-item">
                        <span class="info-label">Family Name:</span>
                        <span class="info-value"><%= family.getFamilyName() %></span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">Your Name:</span>
                        <span class="info-value"><%= user.getFullName() %></span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">Email:</span>
                        <span class="info-value"><%= user.getEmail() %></span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">Role:</span>
                        <span class="info-value"><%= user.getRole() %></span>
                    </div>
                </div>
                
                <% if (isNewFamily) { %>
                    <div class="code-instruction">
                        <h4>Your Family Code</h4>
                        <p>Share this code with family members so they can join your family fund.</p>
                    </div>
                    
                    <div class="family-code-highlight">
                        <%= family.getFamilyCode() %>
                    </div>
                <% } %>
                
                <div style="margin-top: 2rem;">
                    <a href="main.jsp" class="btn-primary">Go to Dashboard</a>
                    <% if (isNewFamily) { %>
                        <a href="categories.jsp" class="btn-secondary">Set Up Categories</a>
                    <% } else { %>
                        <a href="edit_profile.jsp" class="btn-secondary">Edit Profile</a>
                    <% } %>
                </div>
            </div>
        </div>
        
        <footer class="footer">
            <div class="container">
                <p>&copy; 2025 Famney - Family Financial Management System</p>
            </div>
        </footer>
    </body>
</html>