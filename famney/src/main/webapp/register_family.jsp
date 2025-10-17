<%@ page import="model.User"%>
<%@ page import="model.Family"%>

<!-- Initialise database connection -->
<jsp:include page="/ConnServlet" flush="true"/>

<html>
    <head>
        <title>Create Family Account - Famney</title>
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
            
            .register-form {
                background: white;
                padding: 3rem;
                border-radius: 20px;
                box-shadow: 0 20px 40px rgba(0,0,0,0.1);
                width: 100%;
                max-width: 500px;
            }
            
            .form-header {
                text-align: center;
                margin-bottom: 2rem;
            }
            
            .form-header h1 {
                color: #2c3e50;
                font-size: 2rem;
                margin-bottom: 0.5rem;
                font-weight: 300;
            }
            
            .form-header p {
                color: #666;
                font-size: 1rem;
                line-height: 1.5;
            }
            
            .form-group {
                margin-bottom: 1.5rem;
            }
            
            .form-row {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 1rem;
            }
            
            .form-group label {
                display: block;
                margin-bottom: 0.5rem;
                color: #2c3e50;
                font-weight: 600;
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
                border-color: #667eea;
                background: white;
                box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
            }
            
            .btn-primary {
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
            }
            
            .btn-primary:hover {
                transform: translateY(-2px);
                box-shadow: 0 10px 25px rgba(102, 126, 234, 0.3);
            }
            
            .form-links {
                text-align: center;
                margin-top: 1.5rem;
            }
            
            .form-links a {
                color: #667eea;
                text-decoration: none;
                font-weight: 600;
                transition: color 0.3s ease;
            }
            
            .form-links a:hover {
                color: #764ba2;
            }
            
            .error-message {
                background: #fee;
                color: #c33;
                padding: 1rem;
                border-radius: 10px;
                margin-bottom: 1rem;
                border: 1px solid #fcc;
                text-align: center;
            }
            
            .footer {
                background: #2c3e50;
                color: white;
                padding: 2rem;
                text-align: center;
            }
            
            @media (max-width: 768px) {
                .register-form {
                    margin: 1rem;
                    padding: 2rem;
                }
                
                .form-row {
                    grid-template-columns: 1fr;
                    gap: 0;
                }
                
                .nav-menu {
                    gap: 1rem;
                }
            }
        </style>
    </head>
    
    <body>
        <%
            // Check if already logged in
            User user = (User) session.getAttribute("user");
            if (user != null) {
                response.sendRedirect("main.jsp");
                return;
            }
            
            // Get flash message from session
            String errorMessage = (String) session.getAttribute("errorMessage");
            if (errorMessage != null) {
                session.removeAttribute("errorMessage");
            }
        %>
        
        <header class="header">
            <div class="nav-container">
                <a href="index.jsp" class="logo">Famney</a>
                <nav class="nav-menu">
                    <a href="index.jsp">Home</a>
                    <a href="login.jsp">Sign In</a>
                </nav>
            </div>
        </header>
        
        <div class="main-container">
            <div class="register-form">
                <div class="form-header">
                    <h1>Create Family Account</h1>
                    <p>Start your family's financial journey by creating a new family fund</p>
                </div>
                
                <% if (errorMessage != null) { %>
                    <div class="error-message">
                        <%= errorMessage %>
                    </div>
                <% } %>
                
                <form action="CreateFamilyServlet" method="post">
                    <div class="form-group">
                        <label for="familyName">Family Name *</label>
                        <input type="text" id="familyName" name="familyName" 
                               placeholder="Enter your family name (e.g., The Smiths)" required>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label for="fullName">Your Full Name *</label>
                            <input type="text" id="fullName" name="fullName" 
                                   placeholder="Enter your full name" required>
                        </div>
                        
                        <div class="form-group">
                            <label for="email">Email Address *</label>
                            <input type="email" id="email" name="email" 
                                   placeholder="Enter your email" required>
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label for="password">Password *</label>
                        <input type="password" id="password" name="password" 
                               placeholder="Create a secure password (min 6 characters)" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="confirmPassword">Confirm Password *</label>
                        <input type="password" id="confirmPassword" name="confirmPassword" 
                               placeholder="Confirm your password" required>
                    </div>
                    
                    <button type="submit" class="btn-primary">Create Family Account</button>
                </form>
                
                <div class="form-links">
                    <p>Already have a family account? <a href="login.jsp">Sign in here</a></p>
                    <p>Want to join an existing family? <a href="join_family.jsp">Join with family code</a></p>
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