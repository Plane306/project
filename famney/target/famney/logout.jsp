<%@ page import="model.User"%>
<%@ page import="model.Family"%>

<html>
    <head>
        <title>Logout - Famney</title>
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
            
            .nav-menu a {
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
            
            .main-container {
                flex: 1;
                display: flex;
                align-items: center;
                justify-content: center;
                padding: 3rem 2rem;
            }
            
            .logout-card {
                background: white;
                padding: 3rem;
                border-radius: 20px;
                box-shadow: 0 20px 40px rgba(0,0,0,0.1);
                width: 100%;
                max-width: 500px;
                text-align: center;
            }
            
            .logout-icon {
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
            
            .logout-card h1 {
                color: #2c3e50;
                font-size: 2rem;
                margin-bottom: 1rem;
                font-weight: 300;
            }
            
            .logout-card p {
                color: #666;
                font-size: 1.1rem;
                line-height: 1.5;
                margin-bottom: 2rem;
            }
            
            .btn-primary {
                background: linear-gradient(135deg, #667eea, #764ba2);
                color: white;
                padding: 1rem 2rem;
                border: none;
                border-radius: 10px;
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
                border-radius: 10px;
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
                .logout-card {
                    margin: 1rem;
                    padding: 2rem;
                }
                
                .nav-menu {
                    gap: 1rem;
                }
            }
        </style>
    </head>
    
    <body>
        <%
            // Get userName from session (set by LogoutServlet before redirect)
            // Session was already invalidated in LogoutServlet, new session created with userName
            String userName = (String) session.getAttribute("logoutUserName");
            if (userName == null) {
                userName = "User";
            }
            
            // Clear the logout attribute after reading it
            session.removeAttribute("logoutUserName");
        %>
        
        <header class="header">
            <div class="nav-container">
                <a href="index.jsp" class="logo">Famney</a>
                <nav class="nav-menu">
                    <a href="index.jsp">Home</a>
                    <a href="login.jsp">Sign In</a>
                    <a href="register_family.jsp">Create Family</a>
                </nav>
            </div>
        </header>
        
        <div class="main-container">
            <div class="logout-card">
                <div class="logout-icon">&#10003;</div>
                <h1>Logged Out Successfully</h1>
                <p>You have been logged out successfully, <%= userName %>.</p>
                <p>Thank you for using Famney. We hope to see you again soon!</p>
                
                <div style="margin-top: 2rem;">
                    <a href="login.jsp" class="btn-primary">Sign In Again</a>
                    <a href="index.jsp" class="btn-secondary">Back to Home</a>
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