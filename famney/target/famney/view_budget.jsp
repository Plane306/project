<%@ page import="model.*"%>
<%@ page import="model.dao.*"%>
<%@ page import="java.util.*"%>


<%
    // --- Begin: Copy categories logic from categories.jsp ---
    User user = (User) session.getAttribute("user");
    Family family = (Family) session.getAttribute("family");
    if (user == null || family == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    // Use categories from session (initialized in main.jsp)
    List<Category> categories = (List<Category>) session.getAttribute("categories");
%>
<html>
    <head>
        <title>View Budget - Famney</title>
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
                align-items: flex-start;
                padding: 2rem;
            }
            
            .content-box {
                background: white;
                padding: 3rem;
                border-radius: 20px;
                box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
                max-width: 800px;
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

            .budget-info {
                background: #f8f9fa;
                border-radius: 15px;
                padding: 2rem;
                margin-top: 2rem;
            }

            .budget-info h3 {
                color: #2c3e50;
                font-size: 1.5rem;
                margin-bottom: 1.5rem;
                padding-bottom: 1rem;
                border-bottom: 2px solid #e9ecef;
            }

            .budget-detail {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
                gap: 1.5rem;
                margin-bottom: 2rem;
            }

            .detail-item {
                background: white;
                padding: 1.5rem;
                border-radius: 10px;
                border-left: 4px solid #667eea;
            }

            .detail-item h4 {
                color: #7f8c8d;
                font-size: 0.9rem;
                margin-bottom: 0.5rem;
                text-transform: uppercase;
            }

            .detail-item p {
                color: #2c3e50;
                font-size: 1.5rem;
                font-weight: 600;
            }

            .btn-primary {
                display: inline-block;
                text-align: center;
                background: linear-gradient(135deg, #667eea, #764ba2);
                color: white;
                padding: 1rem 2rem;
                border: none;
                border-radius: 10px;
                font-size: 1.1rem;
                font-weight: 600;
                text-decoration: none;
                transition: all 0.3s ease;
                margin-top: 1rem;
            }
            
            .btn-primary:hover {
                transform: translateY(-2px);
                box-shadow: 0 10px 25px rgba(102, 126, 234, 0.3);
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

                .budget-detail {
                    grid-template-columns: 1fr;
                }
            }
        </style>
    </head>
    
    <body>

        <%
            String category = (String) session.getAttribute("selectedCategory");
            Budget budget = (Budget) session.getAttribute("currentBudget");
            
            if (user == null || family == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            // Prepopulated categories (copied from categories.jsp)

            // Find category name by ID
            String categoryName = category;
            for (Category cat : categories) {
                if (cat.getCategoryId().equals(category)) {
                    categoryName = cat.getCategoryName();
                    break;
                }
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
                    <h1>Budget Overview</h1>
                    <p>View and manage your family budget details</p>
                    <a href="create_budget.jsp" class="btn-primary">Create New Budget</a>

                </div>

                <% 
                    List<Budget> allBudgets = (List<Budget>) request.getAttribute("allBudgets");
                    BudgetManager budgetManager = (BudgetManager) session.getAttribute("budgetManager");
                    if (allBudgets != null && allBudgets.size() > 0) {
                        int idx = 0;
                        for (Budget b : allBudgets) {
                            String monthName = b.getMonthName();
                            // Fetch category allocations for this budget
                            Map<String, Object> perf = budgetManager.getBudgetPerformance(b.getBudgetId());
                            List<Map<String, Object>> cats = (List<Map<String, Object>>) perf.get("categories");
                %>
                    <div class="budget-info">
                        <div class="budget-detail">
                            <div class="detail-item">
                                <h4>Budget Name</h4>
                                <p><%= b.getBudgetName() %></p>
                            </div>
                            <div class="detail-item">
                                <h4>Total Amount</h4>
                                <p>$<%= String.format("%,.2f", b.getTotalAmount()) %></p>
                            </div>
                            <div class="detail-item">
                                <h4>Month</h4>
                                <p><%= monthName %></p>
                            </div>
                            <div class="detail-item">
                                <h4>Category</h4>
                                <% if (cats != null && cats.size() > 0) {
                                    for (Map<String, Object> cat : cats) { %>
                                        <p><%= cat.get("categoryName") %> </p>
                                <%  } 
                                   } else { %>
                                    <p>No categories</p>
                                <% } %>
                            </div>
                               <div class="detail-item">
                                   <h4>Created By</h4>
                                   <p><%= b.getCreatedBy() %></p>
                               </div>
                        </div>
                        <% if (user.isAdult() || user.isFamilyHead()) { %>
                        <div style="text-align:right; margin-top:10px;">
                            <form action="EditBudgetServlet" method="get" style="display:inline;">
                                <input type="hidden" name="index" value="<%= idx %>" />
                                <button type="submit" class="btn-primary" style="background:#f1c40f; color:#2c3e50;">Edit</button>
                            </form>
                            <form action="DeleteBudgetServlet" method="post" style="display:inline;">
                                <input type="hidden" name="index" value="<%= idx %>" />
                                <button type="submit" class="btn-primary" style="background:#e74c3c; color:white;">Delete</button>
                            </form>
                        </div>
                        <% } %>
                    </div>
                        <% idx++;
                        } // end for
                    } else { %>
                        <div class="budget-info" style="text-align: center;">
                            <h3>No Budget Found</h3>
                            <p>You have not created a budget yet.</p>
                        </div>
                    <% } %>
            </div>
        </div>
        
        <footer class="footer">
            <div class="container">
                <p>&copy; 2025 Famney - Family Financial Management System</p>
            </div>
        </footer>
    </body>
</html>