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

// Handles family member registration using family code
// Role assignment is done by Family Head after registration
@WebServlet("/JoinFamilyServlet")
public class JoinFamilyServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        UserValidator validator = new UserValidator();
        
        // Get form inputs (role is no longer selected by user)
        String familyCode = request.getParameter("familyCode");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        // Validate all required fields are filled
        if (familyCode == null || familyCode.trim().isEmpty() ||
            fullName == null || fullName.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.isEmpty()) {
            
            session.setAttribute("errorMessage", "Please fill in all required fields");
            response.sendRedirect("join_family.jsp");
            return;
        }
        
        // Validate family code format
        if (!validator.validateFamilyCode(familyCode)) {
            session.setAttribute("errorMessage", "Invalid family code format. Use format: FAMNEY-XXXX");
            response.sendRedirect("join_family.jsp");
            return;
        }
        
        // Validate full name
        if (!validator.validateFullName(fullName)) {
            session.setAttribute("errorMessage", "Name must be 2-100 characters, letters only");
            response.sendRedirect("join_family.jsp");
            return;
        }
        
        // Validate email format
        if (!validator.validateEmail(email)) {
            session.setAttribute("errorMessage", "Please enter a valid email address");
            response.sendRedirect("join_family.jsp");
            return;
        }
        
        // Validate password length
        if (!validator.validatePassword(password)) {
            session.setAttribute("errorMessage", "Password must be at least 6 characters");
            response.sendRedirect("join_family.jsp");
            return;
        }
        
        try {
            // Get DAO managers from session
            UserManager userManager = (UserManager) session.getAttribute("userManager");
            FamilyManager familyManager = (FamilyManager) session.getAttribute("familyManager");
            
            if (userManager == null || familyManager == null) {
                session.setAttribute("errorMessage", "System error. Please try again");
                response.sendRedirect("join_family.jsp");
                return;
            }
            
            // Check if family code exists
            Family family = familyManager.findByFamilyCode(familyCode.trim().toUpperCase());
            
            if (family == null) {
                session.setAttribute("errorMessage", "Family code not found. Please check and try again");
                response.sendRedirect("join_family.jsp");
                return;
            }
            
            // Check if email already exists for ACTIVE users only
            // This allows email reuse if previous user account is inactive (from closed family)
            if (userManager.emailExists(email)) {
                session.setAttribute("errorMessage", "Email already registered. Please login instead");
                response.sendRedirect("join_family.jsp");
                return;
            }
            
            // Create new user account with NULL role (pending Family Head approval)
            User user = new User();
            user.setFullName(fullName.trim());
            user.setEmail(email.trim());
            user.setPassword(password); // Will be hashed by UserManager
            user.setRole(null); // Role is NULL until Family Head assigns it
            user.setFamilyId(family.getFamilyId());
            
            boolean userCreated = userManager.createUser(user);
            
            if (!userCreated) {
                session.setAttribute("errorMessage", "Failed to create account. Please try again");
                response.sendRedirect("join_family.jsp");
                return;
            }
            
            // Increment family member count
            boolean countUpdated = familyManager.incrementMemberCount(family.getFamilyId());
            
            if (!countUpdated) {
                // Count update failed but user created - not critical error
                System.out.println("Warning: Member count not updated for family " + family.getFamilyId());
            }
            
            // Registration successful - redirect to pending approval page
            session.setAttribute("successMessage", 
                "Registration successful! Please wait for Family Head to assign your role before logging in.");
            
            // Redirect to login with success message
            response.sendRedirect("login.jsp");
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "An error occurred during registration. Please try again");
            response.sendRedirect("join_family.jsp");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect GET requests to join page
        response.sendRedirect("join_family.jsp");
    }
}