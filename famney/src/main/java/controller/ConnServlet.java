package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.dao.*;

// Central servlet for database connectivity and DAO manager initialisation
// All JSP pages should include this servlet at the top to access database
// Usage in JSP: <jsp:include page="/ConnServlet" flush="true"/>
@WebServlet("/ConnServlet")
public class ConnServlet extends HttpServlet {

    private DBConnector db;
    private Connection conn;

    // DAO Managers for F101 & F102
    private UserManager userManager;
    private FamilyManager familyManager;
    private CategoryManager categoryManager;
    private BudgetManager budgetManager;
    private ExpenseManager expenseManager;
    private SavingsGoalManager savingsGoalManager;
    private TransactionManager transactionManager;

    // Initialise database connector when servlet starts
    // This runs once when the application starts
    @Override
    public void init() {
        try {
            db = new DBConnector();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ConnServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Initialise all DAO managers and store them in session
    // This runs every time a JSP includes this servlet
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();

        // Open database connection
        conn = db.openConnection();

        try {
            // Initialise DAO managers for F101 & F102
            userManager = new UserManager(conn);
            familyManager = new FamilyManager(conn);
            categoryManager = new CategoryManager(conn);
            budgetManager = new BudgetManager(conn);
            expenseManager = new ExpenseManager(conn);
            savingsGoalManager = new SavingsGoalManager(conn);
            transactionManager = new TransactionManager(conn);

        } catch (SQLException ex) {
            Logger.getLogger(ConnServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Store DAO managers in session so JSP pages can access them
        session.setAttribute("userManager", userManager);
        session.setAttribute("familyManager", familyManager);
        session.setAttribute("categoryManager", categoryManager);
        session.setAttribute("budgetManager", budgetManager);
        session.setAttribute("expenseManager", expenseManager);
        session.setAttribute("savingsGoalManager", savingsGoalManager);
        session.setAttribute("transactionManager", transactionManager);
    }

    // Close database connection when servlet is destroyed
    // This runs when the application shuts down
    @Override
    public void destroy() {
        try {
            db.closeConnection();
        } catch (SQLException ex) {
            Logger.getLogger(ConnServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}