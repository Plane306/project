<%@ page import="model.*"%>
<%@ page import="model.dao.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.text.*"%>

<%
    User user = (User) session.getAttribute("user");
    Family family = (Family) session.getAttribute("family");

    if (user == null || family == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    List<SavingsGoal> allGoals = (List<SavingsGoal>) request.getAttribute("allGoals");
    SavingsGoalManager goalManager = (SavingsGoalManager) session.getAttribute("savingsGoalManager");

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
%>

<html>
<head>
    <title>View Savings Goals - Famney</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
        body { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); min-height: 100vh; display: flex; flex-direction: column; }
        .header { background: #2c3e50; padding: 1rem 0; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .nav-container { max-width: 1200px; margin: 0 auto; display: flex; justify-content: space-between; align-items: center; padding: 0 2rem; }
        .logo { font-size: 2rem; font-weight: 700; color: white; text-decoration: none; }
        .nav-menu { display: flex; gap: 2rem; }
        .nav-menu a, .nav-menu span { color: white; text-decoration: none; padding: 0.5rem 1rem; border-radius: 25px; transition: all 0.3s ease; border: 2px solid transparent; }
        .nav-menu a:hover { background: rgba(255, 255, 255, 0.2); border-color: rgba(255, 255, 255, 0.3); }
        .main-container { flex: 1; display: flex; justify-content: center; align-items: flex-start; padding: 2rem; }
        .content-box { background: white; padding: 3rem; border-radius: 20px; box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15); max-width: 800px; width: 100%; }
        .content-header { text-align: center; margin-bottom: 2rem; }
        .content-header h1 { color: #2c3e50; font-size: 2rem; margin-bottom: 0.5rem; }
        .content-header p { color: #7f8c8d; font-size: 1rem; }
        .goal-info { background: #f8f9fa; border-radius: 15px; padding: 2rem; margin-top: 2rem; }
        .goal-detail { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 1.5rem; margin-bottom: 1.5rem; }
        .detail-item { background: white; padding: 1.5rem; border-radius: 10px; border-left: 4px solid #667eea; }
        .detail-item h4 { color: #7f8c8d; font-size: 0.9rem; margin-bottom: 0.5rem; text-transform: uppercase; }
        .detail-item p { color: #2c3e50; font-size: 1.2rem; font-weight: 600; }
        .bar { height: 10px; background: #eee; border-radius: 999px; overflow: hidden; margin-top: 8px; }
        .fill { height: 100%; background: linear-gradient(90deg,#667eea,#764ba2); }
        .btn-primary { display: inline-block; text-align: center; background: linear-gradient(135deg, #667eea, #764ba2); color: white; padding: 0.6rem 1.2rem; border: none; border-radius: 8px; font-size: 0.95rem; font-weight: 600; text-decoration: none; transition: all 0.3s ease; margin: 0.3rem; }
        .btn-primary:hover { transform: translateY(-2px); box-shadow: 0 6px 15px rgba(102,126,234,0.3); }
        .footer { background: #2c3e50; color: white; padding: 2rem; text-align: center; }
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
            <h1>🎯 Savings Goals Overview</h1>
            <p>View and manage your family's savings goals</p>
            <a href="goal_form.jsp" class="btn-primary">+ Create New Goal</a>
        </div>

        <%
            if (allGoals != null && !allGoals.isEmpty()) {
                int idx = 0;
                for (SavingsGoal g : allGoals) {
                    double pct = g.getProgressPercentage();
        %>
            <div class="goal-info">
                <div class="goal-detail">
                    <div class="detail-item">
                        <h4>Goal Name</h4>
                        <p><%= g.getGoalName() %></p>
                    </div>
                    <div class="detail-item">
                        <h4>Target</h4>
                        <p>$<%= String.format("%,.2f", g.getTargetAmount()) %></p>
                    </div>
                    <div class="detail-item">
                        <h4>Saved</h4>
                        <p>$<%= String.format("%,.2f", g.getCurrentAmount()) %></p>
                        <div class="bar"><div class="fill" style="width:<%= pct %>%"></div></div>
                    </div>
                    <div class="detail-item">
                        <h4>Due Date</h4>
                        <p><%= g.getTargetDate() != null ? sdf.format(g.getTargetDate()) : "—" %></p>
                    </div>
                    <div class="detail-item">
                        <h4>Status</h4>
                        <p><%= g.isCompleted() ? "✅ Completed" : "In Progress" %></p>
                    </div>
                </div>
                <div style="text-align:right; margin-top:10px;">
                    <form action="EditGoalServlet" method="get" style="display:inline;">
                        <input type="hidden" name="index" value="<%= idx %>" />
                        <button type="submit" class="btn-primary" style="background:#f1c40f; color:#2c3e50;">Edit</button>
                    </form>
                    <form action="DeleteGoalServlet" method="post" style="display:inline;">
                        <input type="hidden" name="index" value="<%= idx %>" />
                        <button type="submit" class="btn-primary" style="background:#e74c3c; color:white;">Delete</button>
                    </form>
                </div>
            </div>
        <%
                idx++;
                }
            } else {
        %>
            <div class="goal-info" style="text-align: center;">
                <h3>No Savings Goals Found</h3>
                <p>You have not created a goal yet.</p>
            </div>
        <%
            }
        %>
    </div>
</div>

<footer class="footer">
    <div class="container">
        <p>&copy; 2025 Famney - Family Financial Management System</p>
    </div>
</footer>
</body>
</html>
