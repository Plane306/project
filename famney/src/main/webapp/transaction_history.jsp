<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*"%>
<%@ page import="model.dao.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.text.SimpleDateFormat"%>

<!-- Include database connection -->
<jsp:include page="/ConnServlet" flush="true"/>

<%
    User user = (User) session.getAttribute("user");
    Family family = (Family) session.getAttribute("family");

    if (user == null || family == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    TransactionManager transactionManager = (TransactionManager) session.getAttribute("transactionManager");
    
    // Get filter parameters
    String categoryFilter = request.getParameter("categoryFilter");
    String typeFilter = request.getParameter("typeFilter");
    String memberFilter = request.getParameter("memberFilter");
    String startDate = request.getParameter("startDate");
    String endDate = request.getParameter("endDate");
    String searchTerm = request.getParameter("searchTerm");
    
    // Pagination
    int page = 1;
    int pageSize = 20;
    try {
        if (request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page"));
        }
    } catch (NumberFormatException e) {
        page = 1;
    }
    
    // Fetch transactions
    List<Map<String, Object>> transactions = transactionManager.getFilteredTransactions(
        family.getFamilyId(), categoryFilter, typeFilter, memberFilter, 
        startDate, endDate, searchTerm, page, pageSize
    );
    
    int totalCount = transactionManager.getTotalTransactionCount(family.getFamilyId());
    int totalPages = (int) Math.ceil(totalCount / (double) pageSize);
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");
%>

<html>
<head>
    <title>Transaction History - Famney</title>
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
        }
        
        .nav-menu a:hover {
            background: rgba(255, 255, 255, 0.2);
        }
        
        .container {
            max-width: 1200px;
            margin: 2rem auto;
            padding: 0 2rem;
        }
        
        .content-box {
            background: white;
            padding: 2rem;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
        }
        
        .page-header {
            margin-bottom: 2rem;
        }
        
        .page-header h1 {
            color: #2c3e50;
            font-size: 2rem;
            margin-bottom: 0.5rem;
        }
        
        .filter-section {
            background: #f8f9fa;
            padding: 1.5rem;
            border-radius: 10px;
            margin-bottom: 2rem;
        }
        
        .filter-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1rem;
            margin-bottom: 1rem;
        }
        
        .filter-group label {
            display: block;
            font-weight: 600;
            color: #2c3e50;
            margin-bottom: 0.5rem;
            font-size: 0.9rem;
        }
        
        .filter-group input, .filter-group select {
            width: 100%;
            padding: 0.6rem;
            border: 2px solid #e0e0e0;
            border-radius: 8px;
            font-size: 0.95rem;
        }
        
        .filter-actions {
            display: flex;
            gap: 1rem;
            margin-top: 1rem;
        }
        
        .btn {
            padding: 0.6rem 1.5rem;
            border: none;
            border-radius: 8px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
        }
        
        .btn-primary {
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: white;
        }
        
        .btn-secondary {
            background: #f0f0f0;
            color: #2c3e50;
        }
        
        .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        }
        
        .transaction-table {
            width: 100%;
            border-collapse: collapse;
        }
        
        .transaction-table th {
            background: #f8f9fa;
            padding: 1rem;
            text-align: left;
            font-weight: 600;
            color: #2c3e50;
            border-bottom: 2px solid #e0e0e0;
        }
        
        .transaction-table td {
            padding: 1rem;
            border-bottom: 1px solid #f0f0f0;
        }
        
        .transaction-table tr:hover {
            background: #f8f9fa;
        }
        
        .type-badge {
            display: inline-block;
            padding: 0.3rem 0.8rem;
            border-radius: 12px;
            font-size: 0.85rem;
            font-weight: 600;
        }
        
        .type-income {
            background: #d4edda;
            color: #155724;
        }
        
        .type-expense {
            background: #f8d7da;
            color: #721c24;
        }
        
        .amount-income {
            color: #28a745;
            font-weight: 600;
        }
        
        .amount-expense {
            color: #dc3545;
            font-weight: 600;
        }
        
        .pagination {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 0.5rem;
            margin-top: 2rem;
        }
        
        .pagination a, .pagination span {
            padding: 0.5rem 1rem;
            border: 1px solid #e0e0e0;
            border-radius: 6px;
            text-decoration: none;
            color: #2c3e50;
        }
        
        .pagination a:hover {
            background: #667eea;
            color: white;
            border-color: #667eea;
        }
        
        .pagination .active {
            background: #667eea;
            color: white;
            border-color: #667eea;
        }
        
        .empty-state {
            text-align: center;
            padding: 3rem;
            color: #7f8c8d;
        }
        
        .back-link {
            display: inline-block;
            margin-top: 1rem;
            color: #667eea;
            text-decoration: none;
            font-weight: 600;
        }
        
        .back-link:hover {
            text-decoration: underline;
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

    <div class="container">
        <div class="content-box">
            <div class="page-header">
                <h1>Transaction History</h1>
                <p style="color: #7f8c8d;">View and filter all family financial transactions</p>
            </div>

            <form class="filter-section" method="get" action="transaction_history.jsp">
                <div class="filter-grid">
                    <div class="filter-group">
                        <label>Type</label>
                        <select name="typeFilter">
                            <option value="">All Transactions</option>
                            <option value="Income" <%= "Income".equals(typeFilter) ? "selected" : "" %>>Income Only</option>
                            <option value="Expense" <%= "Expense".equals(typeFilter) ? "selected" : "" %>>Expense Only</option>
                        </select>
                    </div>
                    
                    <div class="filter-group">
                        <label>Search</label>
                        <input type="text" name="searchTerm" placeholder="Search description..." 
                               value="<%= searchTerm != null ? searchTerm : "" %>">
                    </div>
                    
                    <div class="filter-group">
                        <label>From Date</label>
                        <input type="date" name="startDate" 
                               value="<%= startDate != null ? startDate : "" %>">
                    </div>
                    
                    <div class="filter-group">
                        <label>To Date</label>
                        <input type="date" name="endDate" 
                               value="<%= endDate != null ? endDate : "" %>">
                    </div>
                </div>
                
                <div class="filter-actions">
                    <button type="submit" class="btn btn-primary">Apply Filters</button>
                    <a href="transaction_history.jsp" class="btn btn-secondary">Clear Filters</a>
                </div>
            </form>

            <% if (transactions.isEmpty()) { %>
                <div class="empty-state">
                    <p>No transactions found. Try adjusting your filters.</p>
                </div>
            <% } else { %>
                <table class="transaction-table">
                    <thead>
                        <tr>
                            <th>Date</th>
                            <th>Type</th>
                            <th>Category</th>
                            <th>Description</th>
                            <th>Member</th>
                            <th>Amount</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Map<String, Object> transaction : transactions) {
                            String type = (String) transaction.get("type");
                            boolean isIncome = "Income".equals(type);
                        %>
                        <tr>
                            <td><%= dateFormat.format((java.util.Date) transaction.get("date")) %></td>
                            <td>
                                <span class="type-badge <%= isIncome ? "type-income" : "type-expense" %>">
                                    <%= type %>
                                </span>
                            </td>
                            <td><%= transaction.get("categoryName") %></td>
                            <td><%= transaction.get("description") != null ? transaction.get("description") : "-" %></td>
                            <td><%= transaction.get("userName") %></td>
                            <td class="<%= isIncome ? "amount-income" : "amount-expense" %>">
                                <%= isIncome ? "+" : "-" %>$<%= String.format("%.2f", (Double) transaction.get("amount")) %>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>

                <% if (totalPages > 1) { %>
                <div class="pagination">
                    <% if (page > 1) { %>
                        <a href="?page=<%= page - 1 %><%= categoryFilter != null ? "&categoryFilter=" + categoryFilter : "" %><%= typeFilter != null ? "&typeFilter=" + typeFilter : "" %>">Previous</a>
                    <% } %>
                    
                    <% for (int i = 1; i <= totalPages; i++) { %>
                        <% if (i == page) { %>
                            <span class="active"><%= i %></span>
                        <% } else { %>
                            <a href="?page=<%= i %><%= categoryFilter != null ? "&categoryFilter=" + categoryFilter : "" %><%= typeFilter != null ? "&typeFilter=" + typeFilter : "" %>"><%= i %></a>
                        <% } %>
                    <% } %>
                    
                    <% if (page < totalPages) { %>
                        <a href="?page=<%= page + 1 %><%= categoryFilter != null ? "&categoryFilter=" + categoryFilter : "" %><%= typeFilter != null ? "&typeFilter=" + typeFilter : "" %>">Next</a>
                    <% } %>
                </div>
                <% } %>
            <% } %>

            <a href="main.jsp" class="back-link">← Back to Dashboard</a>
        </div>
    </div>
</body>
</html>