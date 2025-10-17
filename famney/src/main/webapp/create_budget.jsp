<%@ page import="model.*"%>
<%@ page import="model.dao.*"%>
<%@ page import="java.util.*"%>


<%
    // Get current user and family info from session
    User user = (User) session.getAttribute("user");
    Family family = (Family) session.getAttribute("family");
    
    // Redirect if not logged in
    if (user == null || family == null) {
        response.sendRedirect("login.jsp");
        return;
    }


    // Show error if present
    String error = (String) request.getAttribute("error");

    // --- Begin: Copy categories logic from categories.jsp ---
    List<Category> categories = (List<Category>) session.getAttribute("categories");


%>
<html>
    <head>
        <title>Famney - Family Financial Management</title>
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
            }
            
            /* Header */
            .header {
                background: #2c3e50;
                padding: 1rem 0;
                position: sticky;
                top: 0;
                z-index: 100;
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
            
            .nav-menu span {
                color: white;
                padding: 0.5rem 1rem;
                font-weight: 600;
                opacity: 0.9;
            }
            
            .nav-menu a:hover {
                background: rgba(255, 255, 255, 0.2);
                border-color: rgba(255, 255, 255, 0.3);
            }
            
            /* Hero Section */
            .hero {
                text-align: center;
                padding: 6rem 2rem 4rem;
                color: white;
            }
            
            .hero h1 {
                font-size: 3.5rem;
                margin-bottom: 1rem;
                font-weight: 300;
            }
            
            .hero-subtitle {
                font-size: 1.3rem;
                margin-bottom: 2rem;
                opacity: 0.9;
                max-width: 600px;
                margin-left: auto;
                margin-right: auto;
            }
            
            .cta-buttons {
                margin-top: 3rem;
                display: flex;
                justify-content: center;
                gap: 1rem;
                flex-wrap: wrap;
            }
            
            .btn-primary {
                background: #ff6b6b;
                color: white;
                padding: 1rem 2rem;
                border: none;
                border-radius: 50px;
                font-size: 1.1rem;
                font-weight: 600;
                text-decoration: none;
                display: inline-block;
                transition: all 0.3s ease;
                cursor: pointer;
            }
            
            .btn-primary:hover {
                transform: translateY(-2px);
                box-shadow: 0 10px 25px rgba(255, 107, 107, 0.3);
            }
            
            .btn-secondary {
                background: transparent;
                color: white;
                padding: 1rem 2rem;
                border: 2px solid white;
                border-radius: 50px;
                font-size: 1.1rem;
                font-weight: 600;
                text-decoration: none;
                display: inline-block;
                transition: all 0.3s ease;
            }
            
            .btn-secondary:hover {
                background: white;
                color: #764ba2;
            }
            
            /* Features Section */
            .features {
                background: white;
                padding: 5rem 2rem;
            }
            
            .container {
                max-width: 1200px;
                margin: 0 auto;
            }
            
            .section-title {
                text-align: center;
                font-size: 2.5rem;
                color: #2c3e50;
                margin-bottom: 3rem;
                font-weight: 300;
            }
            
            .features-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
                gap: 2rem;
                margin-top: 3rem;
            }
            
            .feature-card {
                background: #f8f9fa;
                padding: 2rem;
                border-radius: 15px;
                text-align: center;
                transition: all 0.3s ease;
                position: relative;
                overflow: hidden;
            }
            
            .feature-card:hover {
                transform: translateY(-5px);
                box-shadow: 0 15px 35px rgba(0,0,0,0.1);
            }
            
            .feature-card::before {
                content: '';
                position: absolute;
                top: 0;
                left: 0;
                width: 100%;
                height: 4px;
                background: linear-gradient(90deg, #667eea, #764ba2);
            }
            
            .feature-icon {
                width: 80px;
                height: 80px;
                margin: 0 auto 1.5rem;
                background: linear-gradient(135deg, #667eea, #764ba2);
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 2rem;
                color: white;
                font-weight: bold;
            }
            
            .feature-card h3 {
                font-size: 1.4rem;
                margin-bottom: 1rem;
                color: #2c3e50;
            }
            
            .feature-card p {
                color: #666;
                line-height: 1.6;
            }
            
            /* How It Works */
            .how-it-works {
                background: #f8f9fa;
                padding: 5rem 2rem;
            }
            
            .steps-container {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
                gap: 2rem;
                margin-top: 3rem;
            }
            
            .step {
                text-align: center;
                position: relative;
            }
            
            .step-number {
                width: 60px;
                height: 60px;
                background: #ff6b6b;
                color: white;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 1.5rem;
                font-weight: bold;
                margin: 0 auto 1.5rem;
            }
            
            .step h3 {
                font-size: 1.3rem;
                margin-bottom: 1rem;
                color: #2c3e50;
            }
            
            .step p {
                color: #666;
                line-height: 1.6;
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
                .hero h1 {
                    font-size: 2.5rem;
                }
                
                .nav-menu {
                    gap: 1rem;
                }
                
                .cta-buttons {
                    flex-direction: column;
                    align-items: center;
                }
                
                .features-grid {
                    grid-template-columns: 1fr;
                }
            }

            /* Budget Form Styling */
            .budget-container {
                max-width: 600px;
                margin: 2rem auto;
                padding: 2.5rem;
                background: white;
                border-radius: 15px;
                box-shadow: 0 10px 30px rgba(0,0,0,0.1);
            }

            .budget-title {
                color: #2c3e50;
                font-size: 2rem;
                margin-bottom: 2rem;
                text-align: center;
                font-weight: 500;
            }

            .budget-form {
                display: flex;
                flex-direction: column;
                gap: 1.5rem;
            }

            .form-group {
                margin-bottom: 1.5rem;
            }

            .form-group label {
                display: block;
                margin-bottom: 0.5rem;
                color: #2c3e50;
                font-weight: 500;
                font-size: 0.95rem;
            }

            .form-group input,
            .form-group select {
                width: 100%;
                padding: 0.8rem;
                border: 2px solid #e1e8ed;
                border-radius: 10px;
                font-size: 1rem;
                transition: all 0.3s ease;
            }

            .form-group input:focus,
            .form-group select:focus {
                border-color: #667eea;
                outline: none;
                box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
            }

            .form-group input::placeholder {
                color: #a0aec0;
            }

            .form-submit {
                display: flex;
                gap: 1rem;
                justify-content: center;
                margin-top: 1rem;
            }

            .btn-submit {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                padding: 1rem 2.5rem;
                border: none;
                border-radius: 50px;
                font-size: 1.1rem;
                font-weight: 600;
                cursor: pointer;
                transition: all 0.3s ease;
            }

            .btn-submit:hover {
                transform: translateY(-2px);
                box-shadow: 0 10px 25px rgba(102, 126, 234, 0.3);
            }

            .btn-cancel {
                background: #e2e8f0;
                color: #2d3748;
                padding: 1rem 2.5rem;
                border: none;
                border-radius: 50px;
                font-size: 1.1rem;
                font-weight: 600;
                text-decoration: none;
                transition: all 0.3s ease;
            }

            .btn-cancel:hover {
                background: #cbd5e0;
                transform: translateY(-2px);
            }
        </style>
    </head>
    


<body>
    <div class="header">
        <div class="nav-container">
            <a href="index.jsp" class="logo">Famney</a>
            <div class="nav-menu">
                <a href="main.jsp">Dashboard</a>
                <a href="logout.jsp" class="active">Logout</a>
            </div>
        </div>
    </div>

    <div class="budget-container">
        <h2 class="budget-title">Create New Budget</h2>
        <% if (error != null) { %>
            <div class="error-message"><%= error %></div>
        <% } %>
    <form action="BudgetServlet" method="POST" class="budget-form">
            <div class="form-group">
                <label for="name">Budget Name</label>
                <input type="text" id="name" name="name" placeholder="Enter budget name" required>
            </div>

            <div class="form-group" id="monthSelect">
                <label for="month">Month</label>
                <select id="month" name="month" required>
                    <option value="1">January</option>
                    <option value="2">February</option>
                    <option value="3">March</option>
                    <option value="4">April</option>
                    <option value="5">May</option>
                    <option value="6">June</option>
                    <option value="7">July</option>
                    <option value="8">August</option>
                    <option value="9">September</option>
                    <option value="10">October</option>
                    <option value="11">November</option>
                    <option value="12">December</option>
                </select>
            </div>

            <div class="form-group" id="categorySelect">
                <label for="category">Category</label>
                <select id="category" name="category" required>
                    <option value="">--Select Category--</option>
                    <% for (Category cat : categories) { %>
                        <option value="<%= cat.getCategoryId() %>"><%= cat.getCategoryName() %> </option>
                    <% } %>
                </select>
            </div>
            <div class="form-group">
                <label for="budget">Budget Amount ($)</label>
                <input type="number" id="budget" name="budget" placeholder="0.00" step="0.01" required>
            </div>

            <div class="form-submit">
                <button type="submit" class="btn-submit">Create Budget</button>
                <a href="BudgetServlet" class="btn-cancel">Cancel</a>
            </div>
        </form>
    </div>
</body>
</html>