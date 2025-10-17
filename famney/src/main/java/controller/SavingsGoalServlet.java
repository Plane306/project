package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.Family;
import model.SavingsGoal;
import model.User;
import model.dao.SavingsGoalManager;

@WebServlet("/SavingsGoalServlet")
public class SavingsGoalServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        Family family = (Family) session.getAttribute("family");

        if (user == null || family == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        SavingsGoalManager manager = (SavingsGoalManager) session.getAttribute("savingsGoalManager");
        String action = request.getParameter("action");

        if ("create".equals(action)) {
            createGoal(request, response, session, manager, family.getFamilyId(), user.getUserId());
        } else if ("add_contribution".equals(action)) {
            addContribution(request, response, session, manager);
        } else if ("edit".equals(action)) {
            editGoal(request, response, session, manager);
        } else if ("delete".equals(action)) {
            deleteGoal(request, response, session, manager, user.getRole());
        } else {
            session.setAttribute("errorMessage", "Unknown action");
            response.sendRedirect("savings_goals.jsp");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        Family family = (Family) session.getAttribute("family");

        if (user == null || family == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        SavingsGoalManager manager = (SavingsGoalManager) session.getAttribute("savingsGoalManager");
        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            deleteGoal(request, response, session, manager, user.getRole());
        } else {
            session.setAttribute("errorMessage", "Unknown action");
            response.sendRedirect("savings_goals.jsp");
        }
    }

    private void createGoal(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, SavingsGoalManager manager,
            String familyId, String userId) throws IOException {

        String goalName = request.getParameter("goalName");
        String description = request.getParameter("description");
        String targetAmountStr = request.getParameter("targetAmount");
        String targetDateStr = request.getParameter("targetDate");

        SavingsGoalValidator validator = new SavingsGoalValidator();

        if (!validator.validateSavingsGoal(goalName, targetAmountStr, targetDateStr)) {
            session.setAttribute("errorMessage", "Invalid goal information. Please check all fields.");
            response.sendRedirect("goal_form.jsp");
            return;
        }

        try {
            double targetAmount = Double.parseDouble(targetAmountStr);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date targetDate = sdf.parse(targetDateStr);

            SavingsGoal goal = new SavingsGoal(familyId, goalName, description, targetAmount, targetDate, userId);

            boolean created = manager.createSavingsGoal(goal);

            if (created) {
                session.setAttribute("successMessage", "Savings goal created successfully!");
                response.sendRedirect("savings_goals.jsp");
            } else {
                session.setAttribute("errorMessage", "Failed to create savings goal. Please try again.");
                response.sendRedirect("goal_form.jsp");
            }

        } catch (ParseException | SQLException e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Error creating goal: " + e.getMessage());
            response.sendRedirect("goal_form.jsp");
        }
    }

    private void addContribution(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, SavingsGoalManager manager) throws IOException {

        String goalId = request.getParameter("goalId");
        String amountStr = request.getParameter("amount");

        SavingsGoalValidator validator = new SavingsGoalValidator();

        if (!validator.validateContribution(amountStr)) {
            session.setAttribute("errorMessage", "Invalid contribution amount.");
            response.sendRedirect("savings_goals.jsp");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            boolean added = manager.addToSavingsGoal(goalId, amount);

            if (added) {
                session.setAttribute("successMessage", "Contribution added successfully!");
            } else {
                session.setAttribute("errorMessage", "Failed to add contribution. Goal may be completed or not found.");
            }

        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid amount format.");
        }

        response.sendRedirect("savings_goals.jsp");
    }

    private void editGoal(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, SavingsGoalManager manager) throws IOException {

        String goalId = request.getParameter("goalId");
        String goalName = request.getParameter("goalName");
        String description = request.getParameter("description");
        String targetAmountStr = request.getParameter("targetAmount");
        String targetDateStr = request.getParameter("targetDate");

        SavingsGoalValidator validator = new SavingsGoalValidator();

        if (!validator.validateSavingsGoal(goalName, targetAmountStr, targetDateStr)) {
            session.setAttribute("errorMessage", "Invalid goal information.");
            response.sendRedirect("goal_form.jsp?goalId=" + goalId);
            return;
        }

        try {
            SavingsGoal goal = manager.getSavingsGoalById(goalId);

            if (goal == null) {
                session.setAttribute("errorMessage", "Goal not found.");
                response.sendRedirect("savings_goals.jsp");
                return;
            }

            goal.setGoalName(goalName);
            goal.setDescription(description);
            goal.setTargetAmount(Double.parseDouble(targetAmountStr));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            goal.setTargetDate(sdf.parse(targetDateStr));

            boolean updated = manager.updateSavingsGoal(goal);

            if (updated) {
                session.setAttribute("successMessage", "Goal updated successfully!");
            } else {
                session.setAttribute("errorMessage", "Failed to update goal.");
            }

        } catch (ParseException | SQLException e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Error updating goal: " + e.getMessage());
        }

        response.sendRedirect("savings_goals.jsp");
    }

    private void deleteGoal(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, SavingsGoalManager manager,
            String userRole) throws IOException {

        if (!"Family Head".equals(userRole)) {
            session.setAttribute("errorMessage", "Only Family Head can delete goals.");
            response.sendRedirect("savings_goals.jsp");
            return;
        }

        String goalId = request.getParameter("goalId");

        try {
            boolean deleted = manager.deleteSavingsGoal(goalId);

            if (deleted) {
                session.setAttribute("successMessage", "Goal deleted successfully.");
            } else {
                session.setAttribute("errorMessage", "Failed to delete goal.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Error deleting goal: " + e.getMessage());
        }

        response.sendRedirect("savings_goals.jsp");
    }
}