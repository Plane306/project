package controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Budget;
import model.User;
import model.Family;
import model.dao.BudgetManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@WebServlet("/BudgetServlet")
public class BudgetServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        Family family = (Family) request.getSession().getAttribute("family");
        if (user == null || family == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String budgetName = request.getParameter("name");
        int month = Integer.parseInt(request.getParameter("month"));
        double amount = Double.parseDouble(request.getParameter("budget"));
        String categoryId = request.getParameter("category");

        Budget budget = new Budget(
            family.getFamilyId(),
            budgetName,
            month,
            2025,
            amount,
            user.getUserId()
        );

        // Prepare category allocations (single category for now)
        Map<String, Double> categoryAllocations = new HashMap<>();
        categoryAllocations.put(categoryId, amount);

        BudgetManager budgetManager = (BudgetManager) request.getSession().getAttribute("budgetManager");
        boolean success = budgetManager.createBudgetWithAllocations(budget, categoryAllocations);
        if (!success) {
            request.setAttribute("error", "Failed to create budget. Please try again.");
            request.getRequestDispatcher("create_budget.jsp").forward(request, response);
            return;
        }

        // After creation, always fetch all budgets from DB for session and display
        BudgetManager bm = (BudgetManager) request.getSession().getAttribute("budgetManager");
        List<Budget> allBudgets = new ArrayList<>();
        try {
            allBudgets = bm.getBudgetsForFamily(family.getFamilyId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        request.getSession().setAttribute("allBudgets", allBudgets);
        // Optionally, set currentBudget to the most recently created budget
        if (!allBudgets.isEmpty()) {
            request.getSession().setAttribute("currentBudget", allBudgets.get(0));
        }
        request.getSession().setAttribute("selectedCategory", categoryId);
        response.sendRedirect("BudgetServlet");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        Family family = (Family) request.getSession().getAttribute("family");
        if (user == null || family == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        BudgetManager budgetManager = (BudgetManager) request.getSession().getAttribute("budgetManager");
        if (budgetManager == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "BudgetManager not initialized");
            return;
        }

        // Fetch all budgets for this family from DB
        List<Budget> allBudgets = new ArrayList<>();
        try {
            // You need to implement this method in BudgetManager:
            // public List<Budget> getBudgetsForFamily(String familyId)
            allBudgets = budgetManager.getBudgetsForFamily(family.getFamilyId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    request.setAttribute("allBudgets", allBudgets);
    // Also set in session for Edit/Delete servlets
    request.getSession().setAttribute("allBudgets", allBudgets);
        request.getRequestDispatcher("view_budget.jsp").forward(request, response);
    }
}
