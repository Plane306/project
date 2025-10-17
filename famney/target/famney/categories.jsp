<%@ page import="model.User"%>
<%@ page import="model.Family"%>
<%@ page import="model.Category"%>
<%@ page import="model.dao.CategoryManager"%>
<%@ page import="java.util.*"%>

<!-- Initialise database connection -->
<jsp:include page="/ConnServlet" flush="true"/>

<html>
    <head>
        <title>Manage Categories - Famney</title>
        <style>
            /* Same CSS as before - tidak berubah */
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
                max-width: 1200px;
                margin: 0 auto;
                padding: 2rem;
                width: 100%;
            }
            
            .management-card {
                background: white;
                padding: 3rem;
                border-radius: 20px;
                box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
                margin-bottom: 2rem;
            }
            
            .page-header {
                text-align: center;
                margin-bottom: 2rem;
            }
            
            .page-header h1 {
                color: #2c3e50;
                font-size: 2.5rem;
                margin-bottom: 0.5rem;
            }
            
            .page-header p {
                color: #7f8c8d;
                font-size: 1.1rem;
            }
            
            .btn-add {
                background: linear-gradient(135deg, #667eea, #764ba2);
                color: white;
                padding: 0.8rem 1.5rem;
                border: none;
                border-radius: 8px;
                font-size: 1rem;
                font-weight: 600;
                cursor: pointer;
                text-decoration: none;
                display: inline-block;
                transition: all 0.3s ease;
            }
            
            .btn-add:hover {
                transform: translateY(-2px);
                box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
            }
            
            .category-stats {
                display: grid;
                grid-template-columns: repeat(4, 1fr);
                gap: 1rem;
                margin-bottom: 2rem;
            }
            
            .stat-card {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                padding: 1.5rem;
                border-radius: 10px;
                text-align: center;
            }
            
            .stat-number {
                font-size: 2rem;
                font-weight: 700;
                margin-bottom: 0.5rem;
            }
            
            .stat-label {
                font-size: 0.9rem;
                opacity: 0.9;
            }
            
            .category-filters {
                display: flex;
                gap: 1rem;
                margin-bottom: 2rem;
            }
            
            .filter-btn {
                padding: 0.5rem 1rem;
                border: 2px solid #667eea;
                background: transparent;
                color: #667eea;
                border-radius: 25px;
                cursor: pointer;
                transition: all 0.3s ease;
                font-weight: 600;
            }
            
            .filter-btn.active, .filter-btn:hover {
                background: #667eea;
                color: white;
            }
            
            .categories-table {
                width: 100%;
                border-collapse: collapse;
                background: white;
                border-radius: 15px;
                overflow: hidden;
                box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
            }
            
            .categories-table thead {
                background: linear-gradient(135deg, #667eea, #764ba2);
                color: white;
            }
            
            .categories-table th,
            .categories-table td {
                padding: 1.2rem;
                text-align: left;
                border-bottom: 1px solid #e9ecef;
            }
            
            .categories-table th {
                font-weight: 600;
                font-size: 0.9rem;
                text-transform: uppercase;
                letter-spacing: 0.5px;
            }
            
            .categories-table tbody tr:hover {
                background: #f8f9fa;
                transition: background 0.3s ease;
            }
            
            .category-name {
                font-weight: 600;
                color: #2c3e50;
            }
            
            .category-type-badge {
                display: inline-block;
                padding: 0.3rem 0.8rem;
                border-radius: 20px;
                font-size: 0.8rem;
                font-weight: 600;
                text-transform: uppercase;
            }
            
            .expense-badge {
                background: #f8d7da;
                color: #721c24;
            }
            
            .income-badge {
                background: #d4edda;
                color: #155724;
            }
            
            .default-badge {
                background: #d1ecf1;
                color: #0c5460;
                font-size: 0.65rem;
                padding: 0.2rem 0.5rem;
                margin-left: 0.5rem;
                border-radius: 15px;
                display: inline-block;
                vertical-align: middle;
                font-weight: 500;
                text-transform: uppercase;
            }
            
            .action-buttons {
                display: flex;
                gap: 0.5rem;
                align-items: center;
            }
            
            .btn-small {
                padding: 0.4rem 0.8rem;
                border: none;
                border-radius: 8px;
                font-size: 0.8rem;
                font-weight: 600;
                cursor: pointer;
                transition: all 0.3s ease;
                text-decoration: none;
                display: inline-block;
                text-align: center;
            }
            
            .btn-edit {
                background: #ffc107;
                color: #212529;
            }
            
            .btn-edit:hover {
                background: #ffb300;
                transform: translateY(-1px);
            }
            
            .btn-delete {
                background: #dc3545;
                color: white;
            }
            
            .btn-delete:hover {
                background: #c82333;
                transform: translateY(-1px);
            }
            
            .btn-delete:disabled {
                background: #6c757d;
                cursor: not-allowed;
                transform: none;
            }
            
            .btn-back {
                display: inline-block;
                text-align: center;
                background: transparent;
                color: #667eea;
                padding: 1rem 2rem;
                border: 2px solid #667eea;
                border-radius: 10px;
                font-size: 1rem;
                font-weight: 600;
                text-decoration: none;
                transition: all 0.3s ease;
                margin-top: 2rem;
            }
            
            .btn-back:hover {
                background: #667eea;
                color: white;
            }
            
            .empty-state {
                text-align: center;
                padding: 3rem;
                color: #7f8c8d;
            }
            
            .empty-state h3 {
                font-size: 1.5rem;
                margin-bottom: 1rem;
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
            
            .footer {
                background: #2c3e50;
                color: white;
                padding: 2rem;
                text-align: center;
            }
            
            @media (max-width: 768px) {
                .main-container {
                    padding: 1rem;
                }
                
                .management-card {
                    padding: 2rem;
                }
                
                .categories-table th,
                .categories-table td {
                    padding: 0.5rem;
                }
                
                .category-stats {
                    grid-template-columns: 1fr 1fr;
                }
            }
        </style>
    </head>
    
    <body>
        <%
            // Check authentication
            User user = (User) session.getAttribute("user");
            Family family = (Family) session.getAttribute("family");
            
            if (user == null || family == null) {
                response.sendRedirect("login.jsp");
                return;
            }
            
            // Get flash messages
            String successMessage = (String) session.getAttribute("successMessage");
            String errorMessage = (String) session.getAttribute("errorMessage");
            
            if (successMessage != null) {
                session.removeAttribute("successMessage");
            }
            if (errorMessage != null) {
                session.removeAttribute("errorMessage");
            }
            
            // Get CategoryManager from session (initialised by ConnServlet)
            CategoryManager categoryManager = (CategoryManager) session.getAttribute("categoryManager");
            
            // Get categories from database
            List<Category> categories = null;
            int totalCount = 0;
            int expenseCount = 0;
            int incomeCount = 0;
            int customCount = 0;
            
            try {
                if (categoryManager != null) {
                    // Get all categories for this family
                    categories = categoryManager.getFamilyCategories(family.getFamilyId());
                    
                    // Get statistics
                    totalCount = categoryManager.getCategoryCount(family.getFamilyId());
                    expenseCount = categoryManager.getCategoryCountByType(family.getFamilyId(), "Expense");
                    incomeCount = categoryManager.getCategoryCountByType(family.getFamilyId(), "Income");
                    customCount = categoryManager.getCustomCategoryCount(family.getFamilyId());
                } else {
                    categories = new ArrayList<>();
                }
            } catch (Exception e) {
                e.printStackTrace();
                categories = new ArrayList<>();
            }
            
            // Filter categories based on type parameter
            String filterType = request.getParameter("type");
            List<Category> filteredCategories = categories;
            
            if ("expense".equals(filterType)) {
                filteredCategories = new ArrayList<>();
                for (Category cat : categories) {
                    if ("Expense".equals(cat.getCategoryType())) {
                        filteredCategories.add(cat);
                    }
                }
            } else if ("income".equals(filterType)) {
                filteredCategories = new ArrayList<>();
                for (Category cat : categories) {
                    if ("Income".equals(cat.getCategoryType())) {
                        filteredCategories.add(cat);
                    }
                }
            }
        %>
        
        <header class="header">
            <div class="nav-container">
                <a href="main.jsp" class="logo">Famney</a>
                <nav class="nav-menu">
                    <span>Family: <%= family.getFamilyName() %></span>
                    <span><%= user.getFullName() %> (<%= user.getRole() %>)</span>
                    <a href="main.jsp">Dashboard</a>
                    <a href="LogoutServlet">Logout</a>
                </nav>
            </div>
        </header>
        
        <div class="main-container">
            <div class="management-card">
                <div class="page-header">
                    <h1>&#128295; Category Management</h1>
                    <p>Organise your family's expenses and income with custom categories</p>
                </div>
                
                <% if (successMessage != null) { %>
                    <div class="success-message">
                        <%= successMessage %>
                    </div>
                <% } %>
                
                <% if (errorMessage != null) { %>
                    <div class="error-message">
                        <%= errorMessage %>
                    </div>
                <% } %>
                
                <div class="category-stats">
                    <div class="stat-card">
                        <div class="stat-number"><%= totalCount %></div>
                        <div class="stat-label">Total Categories</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number"><%= expenseCount %></div>
                        <div class="stat-label">Expense Types</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number"><%= incomeCount %></div>
                        <div class="stat-label">Income Types</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-number"><%= customCount %></div>
                        <div class="stat-label">Custom Categories</div>
                    </div>
                </div>
                
                <% if ("Family Head".equals(user.getRole()) || "Adult".equals(user.getRole())) { %>
                    <div style="text-align: center; margin-bottom: 2rem;">
                        <a href="category_form.jsp" class="btn-add">&#10133; Add New Category</a>
                    </div>
                <% } %>
                
                <div class="category-filters">
                    <button class="filter-btn <%= (filterType == null || "all".equals(filterType)) ? "active" : "" %>" 
                            onclick="location.href='categories.jsp?type=all'">All Categories</button>
                    <button class="filter-btn <%= "expense".equals(filterType) ? "active" : "" %>" 
                            onclick="location.href='categories.jsp?type=expense'">&#128179; Expenses</button>
                    <button class="filter-btn <%= "income".equals(filterType) ? "active" : "" %>" 
                            onclick="location.href='categories.jsp?type=income'">&#128176; Income</button>
                </div>
                
                <% if (filteredCategories.isEmpty()) { %>
                    <div class="empty-state">
                        <% if (categoryManager == null) { %>
                            <h3>&#9888 System Error</h3>
                            <p style="color: #dc3545; font-weight: 600;">Database connection error. Please refresh the page or contact support.</p>
                        <% } else { %>
                            <h3>No categories found</h3>
                            <p>This is unusual - every family should have default categories.</p>
                            <% if ("Family Head".equals(user.getRole())) { %>
                                <p style="margin-top: 1rem;">
                                    <a href="CategoryServlet?action=reinitialise" class="btn-add">Initialise Default Categories</a>
                                </p>
                            <% } %>
                            <p style="font-size: 0.9rem; color: #6c757d; margin-top: 1rem;">
                                Or create your first custom category:
                            </p>
                            <% if ("Family Head".equals(user.getRole()) || "Adult".equals(user.getRole())) { %>
                                <a href="category_form.jsp" class="btn-add" style="margin-top: 0.5rem;">Create Custom Category</a>
                            <% } %>
                        <% } %>
                    </div>
                <% } else { %>
                    <table class="categories-table">
                        <thead>
                            <tr>
                                <th>Category</th>
                                <th>Type</th>
                                <th>Description</th>
                                <th>Status</th>
                                <% if ("Family Head".equals(user.getRole()) || "Adult".equals(user.getRole())) { %>
                                    <th>Actions</th>
                                <% } %>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Category category : filteredCategories) { %>
                                <tr>
                                    <td>
                                        <span class="category-name">
                                            <%= category.getCategoryName() %>
                                            <% if (category.isDefault()) { %>
                                                <span class="default-badge">DEFAULT</span>
                                            <% } %>
                                        </span>
                                    </td>
                                    <td>
                                        <span class="category-type-badge <%= "Expense".equals(category.getCategoryType()) ? "expense-badge" : "income-badge" %>">
                                            <%= category.getCategoryType() %>
                                        </span>
                                    </td>
                                    <td>
                                        <%= category.getDescription() != null && !category.getDescription().isEmpty() ? category.getDescription() : "No description" %>
                                    </td>
                                    <td>
                                        <%= category.isActive() ? "Active" : "Inactive" %>
                                    </td>
                                    <% if ("Family Head".equals(user.getRole()) || "Adult".equals(user.getRole())) { %>
                                        <td>
                                            <div class="action-buttons">
                                                <% if (!category.isDefault()) { %>
                                                    <a href="category_form.jsp?action=edit&id=<%= category.getCategoryId() %>" class="btn-small btn-edit">
                                                        Edit
                                                    </a>
                                                <% } %>
                                                
                                                <% if ("Family Head".equals(user.getRole())) { %>
                                                    <% if (!category.isDefault()) { %>
                                                        <form action="CategoryServlet" method="post" style="display: inline;" 
                                                              onsubmit="return confirm('Are you sure you want to delete <%= category.getCategoryName() %>?')">
                                                            <input type="hidden" name="action" value="delete">
                                                            <input type="hidden" name="categoryId" value="<%= category.getCategoryId() %>">
                                                            <button type="submit" class="btn-small btn-delete">Delete</button>
                                                        </form>
                                                    <% } else { %>
                                                        <button class="btn-small btn-delete" disabled title="Cannot delete default categories">
                                                            Delete
                                                        </button>
                                                    <% } %>
                                                <% } %>
                                            </div>
                                        </td>
                                    <% } %>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                <% } %>
                
                <div style="text-align: center;">
                    <a href="main.jsp" class="btn-back">&#127968; Back to Dashboard</a>
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