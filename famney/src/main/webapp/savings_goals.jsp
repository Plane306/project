<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*" %>
<%@ page import="model.dao.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>

<!-- Include database connection -->
<jsp:include page="/ConnServlet" flush="true"/>

<%
    // Check authentication
    User user = (User) session.getAttribute("user");
    Family family = (Family) session.getAttribute("family");

    if (user == null || family == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    // Get SavingsGoalManager from session
    SavingsGoalManager savingsGoalManager = (SavingsGoalManager) session.getAttribute("savingsGoalManager");
    
    // Load goals from database
    List<SavingsGoal> goals = new ArrayList<>();
    try {
        goals = savingsGoalManager.getFamilySavingsGoals(family.getFamilyId());
    } catch (Exception e) {
        e.printStackTrace();
    }

    // Load messages
    String successMessage = (String) session.getAttribute("successMessage");
    String errorMessage = (String) session.getAttribute("errorMessage");
    if (successMessage != null) session.removeAttribute("successMessage");
    if (errorMessage != null) session.removeAttribute("errorMessage");

    SimpleDateFormat dateFmt = new SimpleDateFormat("dd MMM yyyy");
%>

<html>
    <head>
        <title>Savings Goals - Famney</title>
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
                text-align: left;
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
                margin-bottom: 1rem;
            }
            
            .btn-primary {
                display: inline-block;
                background: linear-gradient(135deg, #667eea, #764ba2);
                color: white;
                padding: 0.8rem 1.5rem;
                border: none;
                border-radius: 10px;
                font-size: 1rem;
                font-weight: 600;
                cursor: pointer;
                transition: all 0.3s ease;
                text-align: center;
                text-decoration: none;
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
                margin-top: 1rem;
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

            .goal-card {
                border: 1px solid #eee;
                border-radius: 14px;
                padding: 20px;
                margin: 15px 0;
                box-shadow: 0 2px 8px rgba(0,0,0,.06);
                background: #fafafa;
            }
            
            .goal-card h3 {
                color: #2c3e50;
                margin-bottom: 10px;
                font-size: 1.3rem;
            }
            
            .bar {
                height: 12px;
                background: #e0e0e0;
                border-radius: 999px;
                overflow: hidden;
                margin: 12px 0;
            }
            
            .fill {
                height: 100%;
                background: linear-gradient(90deg, #667eea, #764ba2);
                transition: width 0.3s ease;
            }
            
            .goal-stats {
                color: #555;
                font-size: 0.95rem;
                margin: 8px 0;
            }
            
            .goal-stats strong {
                color: #2c3e50;
            }
            
            .muted {
                color: #999;
                font-size: 0.9rem;
            }
            
            .contribution-form {
                display: flex;
                gap: 10px;
                align-items: center;
                flex-wrap: wrap;
                margin-top: 12px;
                padding-top: 12px;
                border-top: 1px solid #e0e0e0;
            }
            
            .contribution-form input[type="number"] {
                padding: 10px;
                border: 1px solid #ddd;
                border-radius: 8px;
                width: 150px;
                font-size: 0.95rem;
            }
            
            .contribution-form button {
                padding: 10px 20px;
                border-radius: 8px;
                border: none;
                background: #667eea;
                color: white;
                cursor: pointer;
                font-weight: 600;
                transition: all 0.3s ease;
            }
            
            .contribution-form button:hover {
                background: #764ba2;
                transform: translateY(-1px);
            }
            
            .goal-actions {
                display: flex;
                gap: 10px;
                margin-top: 10px;
            }
            
            .btn-edit, .btn-delete {
                padding: 6px 12px;
                border-radius: 6px;
                font-size: 0.85rem;
                text-decoration: none;
                transition: all 0.3s ease;
            }
            
            .btn-edit {
                background: #f0f0f0;
                color: #667eea;
                border: 1px solid #667eea;
            }
            
            .btn-edit:hover {
                background: #667eea;
                color: white;
            }
            
            .btn-delete {
                background: #f8d7da;
                color: #721c24;
                border: 1px solid #f5c6cb;
            }
            
            .btn-delete:hover {
                background: #721c24;
                color: white;
            }
            
            .completed-badge {
                display: inline-block;
                background: #d4edda;
                color: #155724;
                padding: 4px 10px;
                border-radius: 12px;
                font-size: 0.85rem;
                font-weight: 600;
                margin-left: 10px;
            }

            @media (max-width: 768px) {
                .content-box {
                    margin: 1rem;
                    padding: 2rem;
                }
                
                .nav-menu {
                    gap: 1rem;
                }
                
                .contribution-form {
                    flex-direction: column;
                    align-items: stretch;
                }
                
                .contribution-form input[type="number"],
                .contribution-form button {
                    width: 100%;
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
                <h1>Savings Goals</h1>
                <p>Track and update your family's savings targets.</p>
                <a href="goal_form.jsp" class="btn-primary">+ New Goal</a>
            </div>

            <!-- Flash messages -->
            <% if (successMessage != null) { %>
                <div class="success-message"><%= successMessage %></div>
            <% } %>
            <% if (errorMessage != null) { %>
                <div class="error-message"><%= errorMessage %></div>
            <% } %>

            <!-- Goals list -->
            <% if (goals.isEmpty()) { %>
                <div class="goal-card">
                    <p class="muted">No goals yet. Click <strong>+ New Goal</strong> to create one.</p>
                </div>
            <% } else {
                for (SavingsGoal g : goals) {
                    double pct = g.getProgressPercentage();
                    String due = (g.getTargetDate() == null) ? "No deadline" : dateFmt.format(g.getTargetDate());
            %>
                <div class="goal-card">
                    <h3>
                        <%= g.getGoalIcon() %> <%= g.getGoalName() %>
                        <% if (g.isCompleted()) { %>
                            <span class="completed-badge">Completed</span>
                        <% } %>
                    </h3>
                    
                    <div class="bar"><div class="fill" style="width:<%= pct %>%"></div></div>
                    
                    <p class="goal-stats">
                        <strong><%= g.getFormattedCurrentAmount() %></strong> of <%= g.getFormattedTargetAmount() %>
                        &nbsp;•&nbsp; <%= String.format("%.1f", pct) %>% complete
                    </p>
                    
                    <p class="muted">
                        Due: <%= due %> &nbsp;|&nbsp; 
                        Remaining: <%= g.getFormattedRemainingAmount() %>
                        <% if (!g.isCompleted() && g.getDaysRemaining() > 0) { %>
                            &nbsp;|&nbsp; <%= g.getDaysRemaining() %> days left
                        <% } %>
                    </p>

                    <% if (g.getDescription() != null && !g.getDescription().trim().isEmpty()) { %>
                        <p class="muted" style="margin-top: 8px;"><em><%= g.getShortDescription() %></em></p>
                    <% } %>

                    <% if (!g.isCompleted()) { %>
                    <form class="contribution-form" method="post" action="SavingsGoalServlet">
                        <input type="hidden" name="action" value="add_contribution" />
                        <input type="hidden" name="goalId" value="<%= g.getGoalId() %>" />
                        <input type="number" step="0.01" min="0.01" name="amount" placeholder="Add amount" required />
                        <button type="submit">Add Contribution</button>
                    </form>
                    <% } %>

                    <% if ("Family Head".equals(user.getRole())) { %>
                    <div class="goal-actions">
                        <a href="goal_form.jsp?goalId=<%= g.getGoalId() %>" class="btn-edit">Edit</a>
                        <a href="SavingsGoalServlet?action=delete&goalId=<%= g.getGoalId() %>" 
                           class="btn-delete" 
                           onclick="return confirm('Are you sure you want to delete this goal?')">Delete</a>
                    </div>
                    <% } %>
                </div>
            <% 
                }
            } 
            %>

            <a class="btn-secondary" href="main.jsp">Back to Dashboard</a>
        </div>
    </div>

    <footer class="footer">
        <div class="container">
            <p>&copy; 2025 Famney - Family Financial Management System</p>
        </div>
    </footer>
    </body>
</html>