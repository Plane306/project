package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// Handles user logout by clearing session data
// Redirects to logout.jsp to display success message
@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        String userName = "User";
        
        // Get user name before invalidating session
        if (session != null) {
            model.User user = (model.User) session.getAttribute("user");
            if (user != null) {
                userName = user.getFullName();
            }
            
            // Clear all session attributes
            session.invalidate();
        }
        
        // Create new session and store userName for logout page display
        HttpSession newSession = request.getSession(true);
        newSession.setAttribute("logoutUserName", userName);
        
        // Redirect to logout page with success message
        response.sendRedirect("logout.jsp");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Handle POST same as GET
        doGet(request, response);
    }
}