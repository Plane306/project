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
import model.dao.FamilyManager;

// Handles family account closure
// Only Family Head can close the entire family
// Requires password confirmation for security
@WebServlet("/CloseFamilyServlet")
public class CloseFamilyServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // Check if user is logged in and is Family Head
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        if (!"Family Head".equals(currentUser.getRole())) {
            session.setAttribute("errorMessage", "Only Family Head can close the family account");
            response.sendRedirect("main.jsp");
            return;
        }
        
        // Get password confirmation
        String password = request.getParameter("password");
        
        if (password == null || password.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Password is required to confirm family closure");
            response.sendRedirect("close_family.jsp");
            return;
        }
        
        try {
            // Get DAO managers from session
            UserManager userManager = (UserManager) session.getAttribute("userManager");
            FamilyManager familyManager = (FamilyManager) session.getAttribute("familyManager");
            
            if (userManager == null || familyManager == null) {
                session.setAttribute("errorMessage", "System error. Please try again");
                response.sendRedirect("close_family.jsp");
                return;
            }
            
            // Verify password is correct
            User authenticatedUser = userManager.authenticate(currentUser.getEmail(), password);
            
            if (authenticatedUser == null) {
                // Password verification failed
                session.setAttribute("errorMessage", "Incorrect password. Family closure cancelled");
                response.sendRedirect("close_family.jsp");
                return;
            }
            
            String familyId = currentUser.getFamilyId();
            
            // Deactivate all family members first
            boolean usersDeactivated = userManager.deactivateAllFamilyUsers(familyId);
            
            if (!usersDeactivated) {
                session.setAttribute("errorMessage", "Failed to deactivate family members. Please try again");
                response.sendRedirect("close_family.jsp");
                return;
            }
            
            // Deactivate the family itself
            boolean familyDeactivated = familyManager.deleteFamily(familyId);
            
            if (!familyDeactivated) {
                session.setAttribute("errorMessage", "Failed to close family. Please try again");
                response.sendRedirect("close_family.jsp");
                return;
            }
            
            // Family closed successfully - logout and redirect to login
            session.invalidate();
            
            HttpSession newSession = request.getSession(true);
            newSession.setAttribute("successMessage", 
                "Family account closed successfully. All members have been logged out.");
            
            response.sendRedirect("login.jsp");
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "An error occurred. Please try again");
            response.sendRedirect("close_family.jsp");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect GET requests to close family page
        response.sendRedirect("close_family.jsp");
    }
}