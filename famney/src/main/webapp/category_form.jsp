<%@ page import="model.User"%>
<%@ page import="model.Family"%>
<%@ page import="model.Category"%>
<%@ page import="model.dao.CategoryManager"%>

<!-- Initialise database connection -->
<jsp:include page="/ConnServlet" flush="true"/>

<html>
    <head>
        <title><%= request.getParameter("action") != null && "edit".equals(request.getParameter("action")) ? "Edit" : "Create" %> Category - Famney</title>
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
            
            .category-form {
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
                color: #2c3e50;
                font-size: 2rem;
                margin-bottom: 0.5rem;
            }
            
            .form-header p {
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
            
            .required {
                color: #e74c3c;
            }
            
            .form-group input, .form-group select, .form-group textarea {
                width: 100%;
                padding: 1rem;
                border: 2px solid #ecf0f1;
                border-radius: 10px;
                font-size: 1rem;
                transition: all 0.3s ease;
                background: #fafafa;
            }
            
            .form-group input:focus, .form-group select:focus, .form-group textarea:focus {
                outline: none;
                border-color: #667eea;
                background: white;
                box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
            }
            
            .form-group textarea {
                resize: vertical;
                min-height: 100px;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            }
            
            .type-selection {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 1rem;
                margin-top: 0.5rem;
            }
            
            .type-option input[type="radio"] {
                position: absolute;
                opacity: 0;
                pointer-events: none;
            }
            
            .type-option label {
                display: block;
                padding: 1rem;
                border: 2px solid #ecf0f1;
                border-radius: 10px;
                cursor: pointer;
                transition: all 0.3s ease;
                background: #fafafa;
                font-weight: 600;
                text-align: center;
            }
            
            .type-option input[type="radio"]:checked + label {
                border-color: #667eea;
                background: rgba(102, 126, 234, 0.1);
                color: #667eea;
            }
            
            .type-option label:hover {
                border-color: #667eea;
                background: rgba(102, 126, 234, 0.05);
            }
            
            .form-actions {
                display: flex;
                gap: 1rem;
                margin-top: 2rem;
            }
            
            .btn-primary {
                flex: 1;
                background: linear-gradient(135deg, #667eea, #764ba2);
                color: white;
                padding: 1rem;
                border: none;
                border-radius: 10px;
                font-size: 1.1rem;
                font-weight: 600;
                cursor: pointer;
                transition: all 0.3s ease;
            }
            
            .btn-primary:hover {
                transform: translateY(-2px);
                box-shadow: 0 10px 25px rgba(102, 126, 234, 0.3);
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
            
            .help-text {
                font-size: 0.85rem;
                color: #7f8c8d;
                margin-top: 0.5rem;
            }
            
            .footer {
                background: #2c3e50;
                color: white;
                padding: 2rem;
                text-align: center;
            }
            
            @media (max-width: 768px) {
                .category-form {
                    margin: 1rem;
                    padding: 2rem;
                }
                
                .type-selection {
                    grid-template-columns: 1fr;
                }
                
                .form-actions {
                    flex-direction: column;
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
            
            // Check permissions - only Family Head and Adult can manage categories
            if (!"Family Head".equals(user.getRole()) && !"Adult".equals(user.getRole())) {
                response.sendRedirect("categories.jsp");
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
            
            // Determine if editing or creating
            String action = request.getParameter("action");
            String categoryId = request.getParameter("id");
            boolean isEditing = "edit".equals(action) && categoryId != null;
            
            // Get category data if editing
            Category editCategory = null;
            
            if (isEditing) {
                CategoryManager categoryManager = (CategoryManager) session.getAttribute("categoryManager");
                
                if (categoryManager != null) {
                    try {
                        editCategory = categoryManager.findByCategoryId(categoryId);
                        
                        // If category not found or is default, redirect to categories page
                        if (editCategory == null || editCategory.isDefault()) {
                            session.setAttribute("errorMessage", "Cannot edit this category");
                            response.sendRedirect("categories.jsp");
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        session.setAttribute("errorMessage", "Error loading category");
                        response.sendRedirect("categories.jsp");
                        return;
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
                    <a href="categories.jsp">Categories</a>
                    <a href="main.jsp">Dashboard</a>
                    <a href="LogoutServlet">Logout</a>
                </nav>
            </div>
        </header>
        
        <div class="main-container">
            <div class="category-form">
                <div class="form-header">
                    <h1>&#128295; <%= isEditing ? "Edit Category" : "Create New Category" %></h1>
                    <p><%= isEditing ? "Update the category details below" : "Add a new category to organise your family finances" %></p>
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
                
                <form action="CategoryServlet" method="post">
                    <input type="hidden" name="action" value="<%= isEditing ? "edit" : "create" %>">
                    <% if (isEditing && editCategory != null) { %>
                        <input type="hidden" name="categoryId" value="<%= editCategory.getCategoryId() %>">
                    <% } %>
                    
                    <div class="form-group">
                        <label for="categoryName">Category Name <span class="required">*</span></label>
                        <input type="text" 
                               id="categoryName" 
                               name="categoryName" 
                               value="<%= isEditing && editCategory != null ? editCategory.getCategoryName() : "" %>" 
                               placeholder="e.g. Food & Dining, Transportation, Salary" 
                               required 
                               maxlength="50">
                        <div class="help-text">2-50 characters, can include letters, numbers, and symbols</div>
                    </div>
                    
                    <% if (!isEditing) { %>
                        <div class="form-group">
                            <label>Category Type <span class="required">*</span></label>
                            <div class="type-selection">
                                <div class="type-option">
                                    <input type="radio" 
                                           id="expense" 
                                           name="categoryType" 
                                           value="Expense" 
                                           checked 
                                           required>
                                    <label for="expense">&#128179; Expense Category</label>
                                </div>
                                <div class="type-option">
                                    <input type="radio" 
                                           id="income" 
                                           name="categoryType" 
                                           value="Income" 
                                           required>
                                    <label for="income">&#128176; Income Category</label>
                                </div>
                            </div>
                            <div class="help-text">Category type cannot be changed after creation</div>
                        </div>
                    <% } else if (editCategory != null) { %>
                        <div class="form-group">
                            <label>Category Type</label>
                            <input type="text" 
                                   value="<%= editCategory.getCategoryType() %>" 
                                   disabled
                                   style="background: #e9ecef; cursor: not-allowed;">
                            <div class="help-text">Category type cannot be changed</div>
                        </div>
                    <% } %>
                    
                    <div class="form-group">
                        <label for="description">Description (Optional)</label>
                        <textarea id="description" 
                                  name="description" 
                                  placeholder="Brief description of what this category includes..."
                                  maxlength="200"><%= isEditing && editCategory != null && editCategory.getDescription() != null ? editCategory.getDescription() : "" %></textarea>
                        <div class="help-text">Maximum 200 characters</div>
                    </div>
                    
                    <div class="form-actions">
                        <button type="submit" class="btn-primary">
                            <%= isEditing ? "&#128190; Update Category" : "&#10004; Create Category" %>
                        </button>
                        <a href="categories.jsp" class="btn-secondary">
                            &#8617; Cancel
                        </a>
                    </div>
                </form>
            </div>
        </div>
        
        <footer class="footer">
            <div class="container">
                <p>&copy; 2025 Famney - Family Financial Management System</p>
            </div>
        </footer>
        
        <script>
            // Simple form validation
            function validateForm() {
                const name = document.getElementById('categoryName').value.trim();
                
                if (!name) {
                    alert('Please enter a category name.');
                    return false;
                }
                
                if (name.length < 2 || name.length > 50) {
                    alert('Category name must be between 2 and 50 characters.');
                    return false;
                }
                
                <% if (!isEditing) { %>
                    const typeSelected = document.querySelector('input[name="categoryType"]:checked');
                    if (!typeSelected) {
                        alert('Please select a category type.');
                        return false;
                    }
                <% } %>
                
                return true;
            }
            
            document.querySelector('form').addEventListener('submit', function(e) {
                if (!validateForm()) {
                    e.preventDefault();
                }
            });
        </script>
    </body>
</html>