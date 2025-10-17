<%@ page import="model.Budget" %>
<%@ page import="model.Category" %>
<%@ page import="java.util.*" %>
<%
    Budget budget = (Budget) request.getAttribute("editBudget");
    String categoryId = (String) request.getAttribute("editCategoryId");
    int index = (request.getAttribute("editIndex") != null) ? (Integer) request.getAttribute("editIndex") : -1;
    // Prepopulated categories (same as view_budget.jsp)
        List<Category> categories = (List<Category>) session.getAttribute("categories");

%>
<html>
<head>
    <title>Edit Budget</title>
    <style>
        body { background: #f8f9fa; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
        .edit-form { max-width: 500px; margin: 40px auto; background: white; padding: 2rem; border-radius: 15px; box-shadow: 0 10px 30px rgba(0,0,0,0.1); }
        h2 { color: #2c3e50; margin-bottom: 1.5rem; }
        label { display: block; margin-bottom: 0.5rem; color: #7f8c8d; }
        input, select { width: 100%; padding: 0.7rem; margin-bottom: 1.2rem; border-radius: 8px; border: 1px solid #e9ecef; }
        .btn-primary { background: linear-gradient(135deg, #667eea, #764ba2); color: white; border: none; padding: 1rem 2rem; border-radius: 10px; font-size: 1.1rem; font-weight: 600; cursor: pointer; transition: all 0.3s ease; }
        .btn-primary:hover { background: #764ba2; }
    </style>
</head>
<body>
    <form class="edit-form" action="EditBudgetServlet" method="post">
        <h2>Edit Budget</h2>
        <input type="hidden" name="index" value="<%= index %>" />
        <label for="name">Budget Name</label>
        <input type="text" id="name" name="name" value="<%= budget.getBudgetName() %>" required />
        <label for="month">Month</label>
        <input type="number" id="month" name="month" min="1" max="12" value="<%= budget.getMonth() %>" required />
        <label for="budget">Total Amount</label>
        <input type="number" id="budget" name="budget" min="0" step="0.01" value="<%= budget.getTotalAmount() %>" required />
                <label for="category">Category</label>
                <select id="category" name="category" required>
                    <option value="">--Select Category--</option>
                    <% for (model.Category cat : categories) { %>
                        <option value="<%= cat.getCategoryId() %>"><%= cat.getCategoryName() %> </option>
                    <% } %>
                </select>
        <button type="submit" class="btn-primary">Update Budget</button>
    </form>
</body>
</html>
