package controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Expense;
import model.User;
import model.Family;
import model.dao.ExpenseManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@WebServlet("/ExpenseServlet")

public class ExpenseServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            User user = (User) request.getSession().getAttribute("user");
            Family family = (Family) request.getSession().getAttribute("family");
            if (user == null || family == null) {
                response.sendRedirect("login.jsp");
                return;
            }
            ExpenseManager expenseManager = (ExpenseManager) request.getSession().getAttribute("expenseManager");
            if (expenseManager == null) {
                throw new ServletException("ExpenseManager not initialized in session");
            }
            List<Expense> allExpenses = expenseManager.getAllExpenses(family.getFamilyId());
            request.getSession().setAttribute("allExpenses", allExpenses);
            RequestDispatcher dispatcher = request.getRequestDispatcher("expenses.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
    // Use ExpenseManager from session

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            User user = (User) request.getSession().getAttribute("user");
            Family family = (Family) request.getSession().getAttribute("family");
            if (user == null || family == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            String action = request.getParameter("action");
            ExpenseManager expenseManager = (ExpenseManager) request.getSession().getAttribute("expenseManager");
            if (expenseManager == null) {
                throw new ServletException("ExpenseManager not initialized in session");
            }
            if (action == null || action.equals("create")) {
                double amount = Double.parseDouble(request.getParameter("amount"));
                String description = request.getParameter("description");
                String categoryId = request.getParameter("category");
                Date expenseDate = new SimpleDateFormat("yyyy-MM-dd")
                        .parse(request.getParameter("expenseDate"));
                Expense expense = new Expense(
                    family.getFamilyId(),
                    user.getUserId(),
                    categoryId,
                    amount,
                    description,
                    expenseDate
                );
                expenseManager.addExpense(expense);
            } else if (action.equals("update")) {
                String expenseId = request.getParameter("expenseId");
                double amount = Double.parseDouble(request.getParameter("amount"));
                String description = request.getParameter("description");
                String categoryId = request.getParameter("category");
                Date expenseDate = new SimpleDateFormat("yyyy-MM-dd")
                        .parse(request.getParameter("expenseDate"));
                Expense updatedExpense = new Expense(
                    expenseId,
                    family.getFamilyId(),
                    user.getUserId(),
                    categoryId,
                    amount,
                    description,
                    expenseDate,
                    new Date(),
                    new Date(),
                    true,
                    null
                );
                expenseManager.updateExpense(updatedExpense);
            } else if (action.equals("delete")) {
                String expenseId = request.getParameter("expenseId");
                expenseManager.deleteExpense(expenseId);
            }

            // Get all expenses for the family and set in session for display
            List<Expense> allExpenses = expenseManager.getAllExpenses(family.getFamilyId());
            request.getSession().setAttribute("allExpenses", allExpenses);

            RequestDispatcher dispatcher = request.getRequestDispatcher("expenses.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}