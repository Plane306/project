<%@ page import="model.User"%>
<%@ page import="model.Family"%>
<%@ page import="model.dao.UserManager"%>
<%@ page import="java.util.List"%>

<!-- Initialise database connection -->
<jsp:include page="/ConnServlet" flush="true"/>

<html>
    <head>
        <title>Manage Family - Famney</title>
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
            
            .family-info {
                background: #f8f9fa;
                padding: 1.5rem;
                border-radius: 15px;
                margin-bottom: 2rem;
                border-left: 5px solid #667eea;
            }
            
            .family-info h3 {
                color: #2c3e50;
                margin-bottom: 0.5rem;
            }
            
            .family-info p {
                color: #6c757d;
                margin-bottom: 0.3rem;
            }
            
            .section-title {
                color: #2c3e50;
                margin-bottom: 1.5rem;
                font-size: 1.8rem;
                display: flex;
                align-items: center;
                gap: 0.5rem;
            }
            
            .pending-badge {
                background: #ffc107;
                color: #212529;
                padding: 0.3rem 0.8rem;
                border-radius: 15px;
                font-size: 0.8rem;
                font-weight: 600;
            }
            
            .members-table {
                width: 100%;
                border-collapse: collapse;
                background: white;
                border-radius: 15px;
                overflow: hidden;
                box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
                margin-bottom: 2rem;
            }
            
            .members-table thead {
                background: linear-gradient(135deg, #667eea, #764ba2);
                color: white;
            }
            
            .members-table th,
            .members-table td {
                padding: 1.2rem;
                text-align: left;
                border-bottom: 1px solid #e9ecef;
            }
            
            .members-table th {
                font-weight: 600;
                font-size: 0.9rem;
                text-transform: uppercase;
                letter-spacing: 0.5px;
            }
            
            .members-table tbody tr:hover {
                background: #f8f9fa;
                transition: background 0.3s ease;
            }
            
            .member-name {
                font-weight: 600;
                color: #2c3e50;
            }
            
            .member-role {
                display: inline-block;
                padding: 0.3rem 0.8rem;
                border-radius: 20px;
                font-size: 0.8rem;
                font-weight: 600;
                text-transform: uppercase;
            }
            
            .role-family-head { background: #d1ecf1; color: #0c5460; }
            .role-adult { background: #d4edda; color: #155724; }
            .role-teen { background: #fff3cd; color: #856404; }
            .role-kid { background: #f8d7da; color: #721c24; }
            .role-pending { background: #ffc107; color: #212529; }
            
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
            
            .btn-remove {
                background: #dc3545;
                color: white;
            }
            
            .btn-remove:hover {
                background: #c82333;
                transform: translateY(-1px);
            }
            
            .btn-assign {
                background: #28a745;
                color: white;
            }
            
            .btn-assign:hover {
                background: #218838;
                transform: translateY(-1px);
            }
            
            .role-select {
                padding: 0.3rem 0.5rem;
                border: 2px solid #e9ecef;
                border-radius: 8px;
                font-size: 0.8rem;
                margin-right: 0.5rem;
            }
            
            .role-select:focus {
                outline: none;
                border-color: #667eea;
            }
            
            .btn-back {
                display: inline-block;
                background: linear-gradient(135deg, #667eea, #764ba2);
                color: white;
                padding: 1rem 2rem;
                border: none;
                border-radius: 10px;
                font-size: 1rem;
                font-weight: 600;
                text-decoration: none;
                transition: all 0.3s ease;
                margin-top: 2rem;
                margin-right: 1rem;
            }
            
            .btn-back:hover {
                transform: translateY(-2px);
                box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
            }
            
            .btn-danger {
                display: inline-block;
                background: #dc3545;
                color: white;
                padding: 1rem 2rem;
                border: none;
                border-radius: 10px;
                font-size: 1rem;
                font-weight: 600;
                text-decoration: none;
                transition: all 0.3s ease;
                margin-top: 2rem;
                cursor: pointer;
            }
            
            .btn-danger:hover {
                background: #c82333;
                transform: translateY(-2px);
                box-shadow: 0 8px 20px rgba(220, 53, 69, 0.3);
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
            
            .empty-state {
                text-align: center;
                padding: 3rem 1rem;
                color: #6c757d;
            }
            
            .empty-state h3 {
                margin-bottom: 1rem;
                color: #495057;
            }
            
            .danger-zone {
                background: #fff3f3;
                border: 2px solid #dc3545;
                padding: 2rem;
                border-radius: 15px;
                margin-top: 3rem;
            }
            
            .danger-zone h3 {
                color: #dc3545;
                margin-bottom: 1rem;
            }
            
            .danger-zone p {
                color: #721c24;
                margin-bottom: 1.5rem;
                line-height: 1.6;
            }
            
            .close-family-form {
                display: flex;
                gap: 1rem;
                align-items: center;
            }
            
            .close-family-form input {
                padding: 0.8rem;
                border: 2px solid #dc3545;
                border-radius: 8px;
                font-size: 1rem;
                width: 200px;
            }
            
            .footer {
                background: #2c3e50;
                color: white;
                padding: 2rem;
                text-align: center;
            }
            
            @media (max-width: 768px) {
                .management-card {
                    margin: 1rem;
                    padding: 2rem;
                }
                
                .members-table th,
                .members-table td {
                    padding: 0.8rem 0.5rem;
                    font-size: 0.9rem;
                }
                
                .action-buttons {
                    flex-direction: column;
                    gap: 0.3rem;
                }
                
                .btn-small {
                    width: 100%;
                }
                
                .nav-menu {
                    gap: 1rem;
                }
                
                .close-family-form {
                    flex-direction: column;
                }
                
                .close-family-form input {
                    width: 100%;
                }
            }
        </style>
    </head>
    
    <body>
        <%
            // Check if user is logged in and is Family Head
            User user = (User) session.getAttribute("user");
            Family family = (Family) session.getAttribute("family");
            
            if (user == null || family == null) {
                response.sendRedirect("login.jsp");
                return;
            }
            
            // Only Family Head can access this page
            if (!"Family Head".equals(user.getRole())) {
                response.sendRedirect("main.jsp");
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
            
            // Get UserManager from session
            UserManager userManager = (UserManager) session.getAttribute("userManager");
            
            // Get all family members and pending users from database
            List<User> familyMembers = null;
            List<User> pendingUsers = null;
            
            try {
                if (userManager != null) {
                    familyMembers = userManager.getUsersByFamily(family.getFamilyId());
                    pendingUsers = userManager.getPendingUsers(family.getFamilyId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        %>
        
        <header class="header">
            <div class="nav-container">
                <a href="index.jsp" class="logo">Famney</a>
                <nav class="nav-menu">
                    <span>Family Head: <%= user.getFullName() %></span>
                    <a href="main.jsp">Dashboard</a>
                    <a href="LogoutServlet">Logout</a>
                </nav>
            </div>
        </header>
        
        <div class="main-container">
            <div class="management-card">
                <div class="page-header">
                    <h1>Family Management</h1>
                    <p>Manage your family members and their roles</p>
                </div>
                
                <div class="family-info">
                    <h3><%= family.getFamilyName() %> Family</h3>
                    <p><strong>Family Code:</strong> <%= family.getFamilyCode() %></p>
                    <p><strong>Total Members:</strong> <%= familyMembers != null ? familyMembers.size() : 0 %></p>
                    <p><strong>Created:</strong> <%= family.getCreatedDate() != null ? 
                        new java.text.SimpleDateFormat("dd MMM yyyy").format(family.getCreatedDate()) : "Recently" %></p>
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
                
                <!-- Pending Users Section (Role Assignment) -->
                <% if (pendingUsers != null && !pendingUsers.isEmpty()) { %>
                    <div class="members-section">
                        <h2 class="section-title">
                            Pending Approval 
                            <span class="pending-badge"><%= pendingUsers.size() %> Waiting</span>
                        </h2>
                        
                        <table class="members-table">
                            <thead>
                                <tr>
                                    <th>Name</th>
                                    <th>Email</th>
                                    <th>Joined</th>
                                    <th>Assign Role</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (User member : pendingUsers) { %>
                                    <tr>
                                        <td class="member-name"><%= member.getFullName() %></td>
                                        <td><%= member.getEmail() %></td>
                                        <td>
                                            <%= member.getJoinDate() != null ? 
                                                new java.text.SimpleDateFormat("dd MMM yyyy").format(member.getJoinDate()) : "Recently" %>
                                        </td>
                                        <td>
                                            <form action="ManageFamilyServlet" method="post" style="display: inline-flex; align-items: center; gap: 0.5rem;">
                                                <input type="hidden" name="action" value="assign_role">
                                                <input type="hidden" name="memberId" value="<%= member.getUserId() %>">
                                                <select name="newRole" class="role-select" required>
                                                    <option value="">Select role...</option>
                                                    <option value="Adult">Adult</option>
                                                    <option value="Teen">Teen</option>
                                                    <option value="Kid">Kid</option>
                                                </select>
                                                <button type="submit" class="btn-small btn-assign">Assign</button>
                                            </form>
                                        </td>
                                        <td>
                                            <form action="ManageFamilyServlet" method="post" style="display: inline;" 
                                                  onsubmit="return confirm('Are you sure you want to remove <%= member.getFullName() %>?')">
                                                <input type="hidden" name="action" value="remove_member">
                                                <input type="hidden" name="memberId" value="<%= member.getUserId() %>">
                                                <button type="submit" class="btn-small btn-remove">Remove</button>
                                            </form>
                                        </td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                <% } %>
                
                <!-- Active Family Members Section -->
                <div class="members-section">
                    <h2 class="section-title">Active Family Members</h2>
                    
                    <% if (familyMembers == null || familyMembers.isEmpty()) { %>
                        <div class="empty-state">
                            <h3>No family members found</h3>
                            <p>Invite family members using your family code: <strong><%= family.getFamilyCode() %></strong></p>
                        </div>
                    <% } else { %>
                        <table class="members-table">
                            <thead>
                                <tr>
                                    <th>Name</th>
                                    <th>Email</th>
                                    <th>Role</th>
                                    <th>Joined</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (User member : familyMembers) { %>
                                    <% if (member.getRole() != null) { // Only show members with assigned roles %>
                                        <tr>
                                            <td class="member-name"><%= member.getFullName() %></td>
                                            <td><%= member.getEmail() %></td>
                                            <td>
                                                <span class="member-role role-<%= member.getRole().toLowerCase().replace(" ", "-") %>">
                                                    <%= member.getRole() %>
                                                </span>
                                            </td>
                                            <td>
                                                <%= member.getJoinDate() != null ? 
                                                    new java.text.SimpleDateFormat("dd MMM yyyy").format(member.getJoinDate()) : "Recently" %>
                                            </td>
                                            <td>
                                                <% if (member.getUserId().equals(user.getUserId())) { %>
                                                    <span style="color: #6c757d; font-style: italic;">You</span>
                                                <% } else { %>
                                                    <div class="action-buttons">
                                                        <form action="ManageFamilyServlet" method="post" style="display: inline-flex; align-items: center; gap: 0.5rem;">
                                                            <input type="hidden" name="action" value="change_role">
                                                            <input type="hidden" name="memberId" value="<%= member.getUserId() %>">
                                                            <select name="newRole" class="role-select">
                                                                <option value="Adult" <%= "Adult".equals(member.getRole()) ? "selected" : "" %>>Adult</option>
                                                                <option value="Teen" <%= "Teen".equals(member.getRole()) ? "selected" : "" %>>Teen</option>
                                                                <option value="Kid" <%= "Kid".equals(member.getRole()) ? "selected" : "" %>>Kid</option>
                                                            </select>
                                                            <button type="submit" class="btn-small btn-edit">Update</button>
                                                        </form>
                                                        
                                                        <form action="ManageFamilyServlet" method="post" style="display: inline;" 
                                                              onsubmit="return confirm('Are you sure you want to remove <%= member.getFullName() %> from the family?')">
                                                            <input type="hidden" name="action" value="remove_member">
                                                            <input type="hidden" name="memberId" value="<%= member.getUserId() %>">
                                                            <button type="submit" class="btn-small btn-remove">Remove</button>
                                                        </form>
                                                    </div>
                                                <% } %>
                                            </td>
                                        </tr>
                                    <% } %>
                                <% } %>
                            </tbody>
                        </table>
                    <% } %>
                </div>
                
                <!-- Danger Zone - Close Family -->
                <div class="danger-zone">
                    <h3>Danger Zone</h3>
                    <p><strong>Close Family Account:</strong> This action will permanently close your family account. All family members will be logged out and will no longer be able to access this family's financial data. This action cannot be undone.</p>
                    
                    <a href="close_family.jsp" class="btn-danger">Close Family Account</a>
                </div>
                
                <div style="text-align: center;">
                    <a href="main.jsp" class="btn-back">Back to Dashboard</a>
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