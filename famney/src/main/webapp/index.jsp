<%@ page import="model.User"%>
<%@ page import="model.Family"%>

<!-- Initialise database connection -->
<jsp:include page="/ConnServlet" flush="true"/>

<html>
    <head>
        <title>Famney - Family Financial Management</title>
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
                position: sticky;
                top: 0;
                z-index: 100;
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
            
            .nav-menu a {
                color: white;
                text-decoration: none;
                padding: 0.5rem 1rem;
                border-radius: 25px;
                transition: all 0.3s ease;
                border: 2px solid transparent;
            }
            
            .nav-menu span {
                color: white;
                padding: 0.5rem 1rem;
                font-weight: 600;
                opacity: 0.9;
            }
            
            .nav-menu a:hover {
                background: rgba(255, 255, 255, 0.2);
                border-color: rgba(255, 255, 255, 0.3);
            }
            
            .hero {
                text-align: center;
                padding: 6rem 2rem 4rem;
                color: white;
            }
            
            .hero h1 {
                font-size: 3.5rem;
                margin-bottom: 1rem;
                font-weight: 300;
            }
            
            .hero-subtitle {
                font-size: 1.3rem;
                margin-bottom: 2rem;
                opacity: 0.9;
                max-width: 600px;
                margin-left: auto;
                margin-right: auto;
            }
            
            .cta-buttons {
                margin-top: 3rem;
                display: flex;
                justify-content: center;
                gap: 1rem;
                flex-wrap: wrap;
            }
            
            .btn-primary {
                background: #ff6b6b;
                color: white;
                padding: 1rem 2rem;
                border: none;
                border-radius: 50px;
                font-size: 1.1rem;
                font-weight: 600;
                text-decoration: none;
                display: inline-block;
                transition: all 0.3s ease;
                cursor: pointer;
            }
            
            .btn-primary:hover {
                transform: translateY(-2px);
                box-shadow: 0 10px 25px rgba(255, 107, 107, 0.3);
            }
            
            .btn-secondary {
                background: transparent;
                color: white;
                padding: 1rem 2rem;
                border: 2px solid white;
                border-radius: 50px;
                font-size: 1.1rem;
                font-weight: 600;
                text-decoration: none;
                display: inline-block;
                transition: all 0.3s ease;
            }
            
            .btn-secondary:hover {
                background: white;
                color: #764ba2;
            }
            
            .features {
                background: white;
                padding: 5rem 2rem;
            }
            
            .container {
                max-width: 1200px;
                margin: 0 auto;
            }
            
            .section-title {
                text-align: center;
                font-size: 2.5rem;
                color: #2c3e50;
                margin-bottom: 3rem;
                font-weight: 300;
            }
            
            .features-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
                gap: 2rem;
                margin-top: 3rem;
            }
            
            .feature-card {
                background: #f8f9fa;
                padding: 2rem;
                border-radius: 15px;
                text-align: center;
                transition: all 0.3s ease;
                position: relative;
                overflow: hidden;
            }
            
            .feature-card:hover {
                transform: translateY(-5px);
                box-shadow: 0 15px 35px rgba(0,0,0,0.1);
            }
            
            .feature-card::before {
                content: '';
                position: absolute;
                top: 0;
                left: 0;
                width: 100%;
                height: 4px;
                background: linear-gradient(90deg, #667eea, #764ba2);
            }
            
            .feature-icon {
                width: 80px;
                height: 80px;
                margin: 0 auto 1.5rem;
                background: linear-gradient(135deg, #667eea, #764ba2);
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 2rem;
                color: white;
                font-weight: bold;
            }
            
            .feature-card h3 {
                font-size: 1.4rem;
                margin-bottom: 1rem;
                color: #2c3e50;
            }
            
            .feature-card p {
                color: #666;
                line-height: 1.6;
            }
            
            .how-it-works {
                background: #f8f9fa;
                padding: 5rem 2rem;
            }
            
            .steps-container {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
                gap: 2rem;
                margin-top: 3rem;
            }
            
            .step {
                text-align: center;
                position: relative;
            }
            
            .step-number {
                width: 60px;
                height: 60px;
                background: #ff6b6b;
                color: white;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 1.5rem;
                font-weight: bold;
                margin: 0 auto 1.5rem;
            }
            
            .step h3 {
                font-size: 1.3rem;
                margin-bottom: 1rem;
                color: #2c3e50;
            }
            
            .step p {
                color: #666;
                line-height: 1.6;
            }
            
            .footer {
                background: #2c3e50;
                color: white;
                padding: 2rem;
                text-align: center;
            }
            
            @media (max-width: 768px) {
                .hero h1 {
                    font-size: 2.5rem;
                }
                
                .nav-menu {
                    gap: 1rem;
                }
                
                .cta-buttons {
                    flex-direction: column;
                    align-items: center;
                }
                
                .features-grid {
                    grid-template-columns: 1fr;
                }
            }
        </style>
    </head>
    
    <body>
        <%
            // Check if user is already logged in
            User user = (User) session.getAttribute("user");
            Family family = (Family) session.getAttribute("family");
        %>
        
        <header class="header">
            <div class="nav-container">
                <a href="index.jsp" class="logo">Famney</a>
                <nav class="nav-menu">
                    <% if (user == null) { %>
                        <a href="login.jsp">Sign In</a>
                        <a href="register_family.jsp">Get Started</a>
                    <% } else { %>
                        <span>Welcome, <%= user.getFullName() %></span>
                        <a href="main.jsp">Dashboard</a>
                        <a href="LogoutServlet">Logout</a>
                    <% } %>
                </nav>
            </div>
        </header>
        
        <section class="hero">
            <% if (user == null) { %>
                <h1>Smart Family Financial Management</h1>
                <p class="hero-subtitle">Track expenses, manage budgets, and achieve savings goals together. Simple financial planning that brings families closer.</p>
                
                <div class="cta-buttons">
                    <a href="register_family.jsp" class="btn-primary">Start Your Family Fund</a>
                    <a href="login.jsp" class="btn-secondary">Sign In</a>
                </div>
            <% } else { %>
                <h1>Welcome Back, <%= family.getFamilyName() %> Family!</h1>
                <p class="hero-subtitle">Continue managing your family's financial journey. Check your dashboard or explore the features below.</p>
                
                <div class="cta-buttons">
                    <a href="main.jsp" class="btn-primary">Go to Dashboard</a>
                    <% if ("Family Head".equals(user.getRole())) { %>
                        <a href="family_management.jsp" class="btn-secondary">Manage Family</a>
                    <% } else { %>
                        <a href="edit_profile.jsp" class="btn-secondary">My Profile</a>
                    <% } %>
                </div>
            <% } %>
        </section>
        
        <section class="features">
            <div class="container">
                <h2 class="section-title">Everything Your Family Needs</h2>
                
                <div class="features-grid">
                    <div class="feature-card">
                        <div class="feature-icon">F1</div>
                        <h3>User & Family Management</h3>
                        <p>Create your family account with secure role-based access. Invite family members as Family Head, Adult, Teen, or Kid with appropriate permissions.</p>
                    </div>
                    
                    <div class="feature-card">
                        <div class="feature-icon">F2</div>
                        <h3>Smart Category System</h3>
                        <p>Organise your finances with custom categories for food, transport, utilities, entertainment, and more. Pre-loaded defaults get you started quickly.</p>
                    </div>
                    
                    <div class="feature-card">
                        <div class="feature-icon">F3</div>
                        <h3>Budget Planning</h3>
                        <p>Set monthly budgets by category, allocate spending limits, and track your family's financial health with visual progress indicators.</p>
                    </div>
                    
                    <div class="feature-card">
                        <div class="feature-icon">F4</div>
                        <h3>Expense Tracking</h3>
                        <p>Record daily expenses quickly with family member attribution. Categorise spending automatically and see where your money goes.</p>
                    </div>
                    
                    <div class="feature-card">
                        <div class="feature-icon">F5</div>
                        <h3>Income Management</h3>
                        <p>Track all income sources including salaries, allowances, and irregular earnings. Set up recurring income for automated tracking.</p>
                    </div>
                    
                    <div class="feature-card">
                        <div class="feature-icon">F6</div>
                        <h3>Financial Dashboard</h3>
                        <p>Get comprehensive overview with charts showing income vs expenses, budget performance, and monthly financial summaries at a glance.</p>
                    </div>
                    
                    <div class="feature-card">
                        <div class="feature-icon">F7</div>
                        <h3>Savings Goals</h3>
                        <p>Set multiple family savings targets for vacations, emergency funds, education, and more. Track progress and celebrate milestones together.</p>
                    </div>
                    
                    <div class="feature-card">
                        <div class="feature-icon">F8</div>
                        <h3>Transaction History</h3>
                        <p>Complete view of all family financial activities with powerful search and filtering by date, category, family member, and amount.</p>
                    </div>
                </div>
            </div>
        </section>
        
        <section class="how-it-works">
            <div class="container">
                <h2 class="section-title">How Famney Works</h2>
                
                <div class="steps-container">
                    <div class="step">
                        <div class="step-number">1</div>
                        <h3>Create Family Account</h3>
                        <p>Sign up as Family Head and get your unique family code to share with other members.</p>
                    </div>
                    
                    <div class="step">
                        <div class="step-number">2</div>
                        <h3>Invite Family Members</h3>
                        <p>Share your family code with spouse, children, and family members to join your financial management system.</p>
                    </div>
                    
                    <div class="step">
                        <div class="step-number">3</div>
                        <h3>Set Up Categories & Budgets</h3>
                        <p>Organise finances with custom categories and set monthly budgets that work for your family's lifestyle.</p>
                    </div>
                    
                    <div class="step">
                        <div class="step-number">4</div>
                        <h3>Track & Achieve Together</h3>
                        <p>Everyone contributes by recording expenses and income, building financial awareness and responsibility as a family.</p>
                    </div>
                </div>
            </div>
        </section>
        
        <footer class="footer">
            <div class="container">
                <p>&copy; 2025 Famney - Family Financial Management System</p>
                <p>S2-Group #11 - Advanced Software Development</p>
            </div>
        </footer>
    </body>
</html>