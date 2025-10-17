package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.User;
import model.Family;
import model.dao.UserManager;
import model.dao.FamilyManager;

// Handles user login authentication
// Validates credentials and checks role assignment status
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // Get form inputs
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        // Basic validation
        if (email == null || email.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Please fill in all fields");
            response.sendRedirect("login.jsp");
            return;
        }
        
        try {
            // Get DAO managers from session (initialised by ConnServlet)
            UserManager userManager = (UserManager) session.getAttribute("userManager");
            FamilyManager familyManager = (FamilyManager) session.getAttribute("familyManager");
            
            if (userManager == null || familyManager == null) {
                session.setAttribute("errorMessage", "System error. Please try again");
                response.sendRedirect("login.jsp");
                return;
            }
            
            // Authenticate user
            User user = userManager.authenticate(email.trim(), password);
            
            if (user == null) {
                // Login failed - invalid credentials
                session.setAttribute("errorMessage", "Invalid email or password");
                response.sendRedirect("login.jsp");
                return;
            }
            
            // Check if user role is still pending (NULL)
            if (user.getRole() == null) {
                session.setAttribute("errorMessage", 
                    "Your account is pending role assignment. Please ask your Family Head to assign your role.");
                response.sendRedirect("login.jsp");
                return;
            }
            
            // Get user's family details
            Family family = familyManager.findByFamilyId(user.getFamilyId());
            
            if (family == null) {
                session.setAttribute("errorMessage", "Family not found. Please contact support");
                response.sendRedirect("login.jsp");
                return;
            }
            
            // Check if family is still active
            if (!family.isActive()) {
                session.setAttribute("errorMessage", "This family account has been closed. Please contact your Family Head");
                response.sendRedirect("login.jsp");
                return;
            }
            
            // Login successful - create session
            session.setAttribute("user", user);
            session.setAttribute("family", family);
            session.setAttribute("successMessage", "Welcome back, " + user.getFullName() + "!");
            
            // Redirect to main dashboard
            response.sendRedirect("main.jsp");
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "An error occurred. Please try again");
            response.sendRedirect("login.jsp");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect GET requests to login page
        response.sendRedirect("login.jsp");
    }
}