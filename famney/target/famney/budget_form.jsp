<%@ page import="model.User"%>
<%@ page import="model.Family"%>
<%-- Import your feature model here --%>

<html>
    <head>
        <title>Your Feature Title - Famney</title>
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
            
            /* Header */
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
            
            /* Main Container */
            .main-container {
                flex: 1;
                display: flex;
                justify-content: center;
                align-items: center;
                padding: 2rem;
            }
            
            .content-box {
                background: white;
                padding: 3rem;
                border-radius: 20px;
                box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
                max-width: 500px;
                width: 100%;
            }
            
            .content-header {
                text-align: center;
                margin-bottom: 2rem;
            }
            
            .content-header h1 {
                color: #2c3e50;
                font-size: 2rem;
                margin-bottom: 0.5rem;
            }
            
            .content-header p {
                color: #7f8c8d;
                font-size: 1rem;
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
                border: 2px solid #ecf0f1;
                border-radius: 10px;
                font-size: 1rem;
                transition: all 0.3s ease;
                background: #fafafa;
            }
            
            .form-group input:focus {
                outline: none;
                border-color: #667eea;
                background: white;
                box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
            }
            
            .btn-primary {
                text-align: center;
                width: 100%;
                background: linear-gradient(135deg, #667eea, #764ba2);
                color: white;
                padding: 1rem;
                border: none;
                border-radius: 10px;
                font-size: 1.1rem;
                font-weight: 600;
                cursor: pointer;
                transition: all 0.3s ease;
                margin-bottom: 1rem;
                text-decoration: none;
            }
            
            .btn-primary:hover {
                transform: translateY(-2px);
                box-shadow: 0 10px 25px rgba(102, 126, 234, 0.3);
            }
            
            .btn-secondary {
                display: inline-block;
                text-align: center;
                width: 100%;
                background: transparent;
                color: #667eea;
                padding: 1rem;
                border: 2px solid #667eea;
                border-radius: 10px;
                font-size: 1rem;
                font-weight: 600;
                text-decoration: none;
                transition: all 0.3s ease;
            }
            
            .btn-secondary:hover {
                background: #667eea;
                color: white;
            }
            
            .success-message {
                background: #d4edda;
                color: #155724;
                padding: 1rem;
                border-radius: 10px;
                margin-bottom: 1rem;
                border: 1px solid #c3e6cb;
                text-align: center;
                font-weight: 600;
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
            
            /* Footer */
            .footer {
                background: #2c3e50;
                color: white;
                padding: 2rem;
                text-align: center;
            }
            
            /* Responsive */
            @media (max-width: 768px) {
                .content-box {
                    margin: 1rem;
                    padding: 2rem;
                }
                
                .nav-menu {
                    gap: 1rem;
                }
            }
                    .budget-actions {
                        display: flex;
                        flex-direction: column;
                        gap: 1rem;
                        align-items: center;
                        margin-top: 2rem;
                        width: 100%;
                    }
                    
                    .budget-actions .btn {
                        display: block;
                        width: 80%;
                        max-width: 300px;
                        margin: 0;
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
            if (user.isTeen() || user.isKid()) {
                response.sendRedirect("BudgetServlet");
                return;
            }
        %>
        
        <header class="header">
            <div class="nav-container">
                <a href="index.jsp" class="logo">Famney</a>
                <nav class="nav-menu">
                    <span>Welcome, <%= user.getFullName() %></span>
                    <a href="main.jsp">Dashboard</a>
                    <a href="logout.jsp">Logout</a>
                </nav>
            </div>
        </header>
        
        <div class="main-container">
            <div class="content-box">
                <div class="content-header">
                    <h1>Budget Management</h1>
                    <p>Create or view your family budgets</p>
                </div>



                <div class="budget-actions">
                    <a href="create_budget.jsp" class="btn btn-primary">Create New Budget</a>
                    <a href="BudgetServlet" class="btn btn-secondary">View Existing Budgets</a>
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