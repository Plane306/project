package controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Budget;
import java.io.IOException;
import java.util.List;

import model.dao.BudgetManager;

@WebServlet("/EditBudgetServlet")
public class EditBudgetServlet extends HttpServlet {
    @Override
    @SuppressWarnings("unchecked")

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String indexStr = request.getParameter("index");
        if (indexStr != null) {
            int index = Integer.parseInt(indexStr);
            HttpSession session = request.getSession();
            List<Budget> allBudgets = (List<Budget>) session.getAttribute("allBudgets");
            List<String> allCategories = (List<String>) session.getAttribute("allCategories");
            if (allBudgets != null && allBudgets.size() > index) {
                Budget budget = allBudgets.get(index);
                String categoryId = (allCategories != null && allCategories.size() > index) ? allCategories.get(index) : null;
                request.setAttribute("editBudget", budget);
                request.setAttribute("editCategoryId", categoryId);
                request.setAttribute("editIndex", index);
                RequestDispatcher rd = request.getRequestDispatcher("edit_budget.jsp");
                rd.forward(request, response);
                return;
            }
        }
    response.sendRedirect("BudgetServlet");
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String indexStr = request.getParameter("index");
        if (indexStr != null) {
            int index = Integer.parseInt(indexStr);
            HttpSession session = request.getSession();
            List<Budget> allBudgets = (List<Budget>) session.getAttribute("allBudgets");
            List<String> allCategories = (List<String>) session.getAttribute("allCategories");
            if (allBudgets != null && allBudgets.size() > index) {
                Budget budget = allBudgets.get(index);
                String budgetName = request.getParameter("name");
                int month = Integer.parseInt(request.getParameter("month"));
                double amount = Double.parseDouble(request.getParameter("budget"));
                String categoryId = request.getParameter("category");
                budget.setBudgetName(budgetName);
                budget.setMonth(month);
                budget.setTotalAmount(amount);
                if (allCategories != null && allCategories.size() > index) {
                    allCategories.set(index, categoryId);
                }
                // Persist to DB
                System.out.println("[DEBUG] EditBudgetServlet: budgetId=" + budget.getBudgetId() + ", budget=" + budget);
                BudgetManager budgetManager = (BudgetManager) session.getAttribute("budgetManager");
                if (budgetManager != null) {
                    budgetManager.updateBudget(budget);
                    budgetManager.updateBudgetCategory(budget.getBudgetId(), categoryId, amount);
                }
                session.setAttribute("allBudgets", allBudgets);
                session.setAttribute("allCategories", allCategories);
            }
        }
        response.sendRedirect("BudgetServlet");
    }
}
