
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*"%>
<%@ page import="model.dao.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.sql.SQLException"%>
<jsp:include page="/ConnServlet" flush="true" />
<%
    // Load user/family and categories from DB via CategoryManager (ConnServlet)
    User user = (User) session.getAttribute("user");
    Family family = (Family) session.getAttribute("family");
    if (user == null || family == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    if (user.isKid()) {
        response.sendRedirect("ExpenseServlet");
        return;
    }

    List<Category> categories = null;
    model.dao.CategoryManager cm = (model.dao.CategoryManager) session.getAttribute("categoryManager");
    if (cm != null) {
        try {
            categories = cm.getCategoriesByType(family.getFamilyId(), "Expense");
        } catch (SQLException e) {
            categories = (List<Category>) session.getAttribute("categories");
            if (categories == null) categories = new ArrayList<>();
        }
    } else {
        categories = (List<Category>) session.getAttribute("categories");
        if (categories == null) categories = new ArrayList<>();
    }
%>
<html>
    <head>
        <title>Record Expense - Famney</title>
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
            .expense-form {
                margin-top: 2rem;
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
            .form-group input, .form-group select {
                width: 100%;
                padding: 1rem;
                border: 2px solid #ecf0f1;
                border-radius: 10px;
                font-size: 1rem;
                transition: all 0.3s ease;
                background: #fafafa;
            }
            .form-group input:focus, .form-group select:focus {
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
            .footer {
                background: #2c3e50;
                color: white;
                padding: 2rem;
                text-align: center;
            }
            @media (max-width: 768px) {
                .content-box {
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
                    <h1>Record Expense</h1>
                    <p>Log a new expense for your family budget</p>
                </div>
                <form action="ExpenseServlet" method="post" class="expense-form">
                    <div class="form-group">
                        <label>User</label>
                        <span><%= user.getFullName() %></span>
                    </div>
                    <div class="form-group">
                        <label for="amount">Amount</label>
                        <input type="number" id="amount" name="amount" step="0.01" required placeholder="e.g. 25.00" />
                    </div>
                    <div class="form-group">
                        <label for="description">Description</label>
                        <input type="text" id="description" name="description" placeholder="e.g. Grocery shopping" />
                    </div>
                    <div class="form-group">
                        <label for="expenseDate">Date</label>
                        <input type="date" id="expenseDate" name="expenseDate" required />
                    </div>
            <div class="form-group" id="categorySelect">
                <label for="category">Category</label>
                <select id="category" name="category" required>
                    <option value="">--Select Category--</option>
                    <% for (model.Category cat : categories) { %>
                        <option value="<%= cat.getCategoryId() %>"><%= cat.getCategoryName() %> </option>
                    <% } %>
                </select>
            </div>
                    <button type="submit" class="btn-primary">Add Expense</button>
                </form>
                <div style="text-align:center; margin-top:2rem;">
                    <form action="ExpenseServlet" method="get" style="display:inline;">
                        <button type="submit" class="btn-secondary">View All Expenses</button>
                    </form>
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