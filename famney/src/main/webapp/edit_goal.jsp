<%@ page import="model.SavingsGoal" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%
    SavingsGoal goal = (SavingsGoal) request.getAttribute("editGoal");
    int index = (request.getAttribute("editIndex") != null) ? (Integer) request.getAttribute("editIndex") : -1;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String targetDate = (goal.getTargetDate() != null) ? sdf.format(goal.getTargetDate()) : "";
%>

<html>
<head>
    <title>Edit Savings Goal</title>
    <style>
        body { background: #f8f9fa; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
        .edit-form { max-width: 500px; margin: 40px auto; background: white; padding: 2rem; border-radius: 15px; box-shadow: 0 10px 30px rgba(0,0,0,0.1); }
        h2 { color: #2c3e50; margin-bottom: 1.5rem; }
        label { display: block; margin-bottom: 0.5rem; color: #7f8c8d; }
        input, textarea { width: 100%; padding: 0.7rem; margin-bottom: 1.2rem; border-radius: 8px; border: 1px solid #e9ecef; }
        textarea { resize: vertical; min-height: 80px; }
        .btn-primary { background: linear-gradient(135deg, #667eea, #764ba2); color: white; border: none; padding: 1rem 2rem; border-radius: 10px; font-size: 1.1rem; font-weight: 600; cursor: pointer; transition: all 0.3s ease; }
        .btn-primary:hover { background: #764ba2; }
    </style>
</head>
<body>
    <form class="edit-form" action="EditGoalServlet" method="post">
        <h2>Edit Savings Goal</h2>
        <input type="hidden" name="index" value="<%= index %>" />

        <label for="goalName">Goal Name</label>
        <input type="text" id="goalName" name="goalName" value="<%= goal.getGoalName() %>" required />

        <label for="targetAmount">Target Amount</label>
        <input type="number" id="targetAmount" name="targetAmount" step="0.01" min="0" value="<%= goal.getTargetAmount() %>" required />

        <label for="description">Description</label>
        <textarea id="description" name="description"><%= goal.getDescription() != null ? goal.getDescription() : "" %></textarea>

        <label for="targetDate">Target Date</label>
        <input type="date" id="targetDate" name="targetDate" value="<%= targetDate %>" />

        <button type="submit" class="btn-primary">Update Goal</button>
    </form>
</body>
</html>
