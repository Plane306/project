package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.User;
import model.dao.UserManager;

// Handles user profile updates including name, email, and password changes
// Validates input and updates database with new information
@WebServlet("/UpdateProfileServlet")
public class UpdateProfileServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        UserValidator validator = new UserValidator();
        
        // Check if user is logged in
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        // Get form inputs
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String newPassword = request.getParameter("password");
        
        // Validate required fields
        if (fullName == null || fullName.trim().isEmpty() ||
            email == null || email.trim().isEmpty()) {
            
            session.setAttribute("errorMessage", "Name and email are required");
            response.sendRedirect("edit_profile.jsp");
            return;
        }
        
        // Validate full name
        if (!validator.validateFullName(fullName)) {
            session.setAttribute("errorMessage", "Name must be 2-100 characters, letters only");
            response.sendRedirect("edit_profile.jsp");
            return;
        }
        
        // Validate email format
        if (!validator.validateEmail(email)) {
            session.setAttribute("errorMessage", "Please enter a valid email address");
            response.sendRedirect("edit_profile.jsp");
            return;
        }
        
        // Validate password if provided
        if (newPassword != null && !newPassword.isEmpty()) {
            if (!validator.validatePassword(newPassword)) {
                session.setAttribute("errorMessage", "Password must be at least 6 characters");
                response.sendRedirect("edit_profile.jsp");
                return;
            }
        }
        
        try {
            // Get UserManager from session
            UserManager userManager = (UserManager) session.getAttribute("userManager");
            
            if (userManager == null) {
                session.setAttribute("errorMessage", "System error. Please try again");
                response.sendRedirect("edit_profile.jsp");
                return;
            }
            
            // Check if email changed and already exists for another ACTIVE user
            // Email can be reused if it only belongs to inactive (soft deleted) users
            if (!email.trim().equalsIgnoreCase(currentUser.getEmail())) {
                if (userManager.emailExists(email.trim())) {
                    session.setAttribute("errorMessage", "Email already in use by another active account");
                    response.sendRedirect("edit_profile.jsp");
                    return;
                }
            }
            
            // Update user object
            currentUser.setFullName(fullName.trim());
            currentUser.setEmail(email.trim());
            
            // Update in database
            boolean updated = userManager.updateUser(currentUser);
            
            if (!updated) {
                session.setAttribute("errorMessage", "Failed to update profile. Please try again");
                response.sendRedirect("edit_profile.jsp");
                return;
            }
            
            // Update password if provided
            if (newPassword != null && !newPassword.isEmpty()) {
                boolean passwordUpdated = userManager.updatePassword(currentUser.getUserId(), newPassword);
                
                if (!passwordUpdated) {
                    session.setAttribute("errorMessage", "Profile updated but password change failed");
                    response.sendRedirect("edit_profile.jsp");
                    return;
                }
            }
            
            // Update session with new user data
            session.setAttribute("user", currentUser);
            session.setAttribute("successMessage", "Profile updated successfully!");
            
            response.sendRedirect("edit_profile.jsp");
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "An error occurred. Please try again");
            response.sendRedirect("edit_profile.jsp");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect GET requests to profile page
        response.sendRedirect("edit_profile.jsp");
    }
}