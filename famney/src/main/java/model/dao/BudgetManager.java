package model.dao;

import java.sql.*;
import java.util.*;
import model.Budget;

/**
 * DAO for Budget operations.
 * Handles budgets and their category allocations.
 */
public class BudgetManager {
    /**
     * Updates the category and allocated amount for a budget (assumes one category per budget).
     */
    public boolean updateBudgetCategory(String budgetId, String newCategoryId, double newAmount) {
        // Soft-delete all old categories for this budget
        String sqlDeactivate = "UPDATE BudgetCategories SET isActive=0, lastModifiedDate=? WHERE budgetId=? AND isActive=1";
        // Check for a soft-deleted row for this (budgetId, categoryId)
        String sqlCheck = "SELECT COUNT(*) FROM BudgetCategories WHERE budgetId=? AND categoryId=? AND isActive=0";
        // Reactivate if exists
        String sqlReactivate = "UPDATE BudgetCategories SET isActive=1, allocatedAmount=?, lastModifiedDate=? WHERE budgetId=? AND categoryId=? AND isActive=0";
        // Insert new category allocation
        String sqlInsert = "INSERT INTO BudgetCategories (budgetId, categoryId, allocatedAmount, createdDate, lastModifiedDate, isActive) VALUES (?, ?, ?, ?, ?, 1)";
        try {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            try (PreparedStatement pstmt = connection.prepareStatement(sqlDeactivate)) {
                pstmt.setTimestamp(1, now);
                pstmt.setString(2, budgetId);
                pstmt.executeUpdate();
            }
            boolean reactivated = false;
            try (PreparedStatement pstmt = connection.prepareStatement(sqlCheck)) {
                pstmt.setString(1, budgetId);
                pstmt.setString(2, newCategoryId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    // Reactivate the row
                    try (PreparedStatement pstmt2 = connection.prepareStatement(sqlReactivate)) {
                        pstmt2.setDouble(1, newAmount);
                        pstmt2.setTimestamp(2, now);
                        pstmt2.setString(3, budgetId);
                        pstmt2.setString(4, newCategoryId);
                        int rows = pstmt2.executeUpdate();
                        System.out.println("[DEBUG] updateBudgetCategory: reactivated old category, rows affected: " + rows);
                        reactivated = rows > 0;
                    }
                }
            }
            if (!reactivated) {
                try (PreparedStatement pstmt = connection.prepareStatement(sqlInsert)) {
                    pstmt.setString(1, budgetId);
                    pstmt.setString(2, newCategoryId);
                    pstmt.setDouble(3, newAmount);
                    pstmt.setTimestamp(4, now);
                    pstmt.setTimestamp(5, now);
                    int rows = pstmt.executeUpdate();
                    System.out.println("[DEBUG] updateBudgetCategory: inserted new category, rows affected: " + rows);
                    return rows > 0;
                }
            } else {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Updates a budget's name, month, year, and totalAmount in the database.
     */
    public boolean updateBudget(Budget budget) {
        String sql = "UPDATE Budgets SET budgetName=?, month=?, year=?, totalAmount=?, lastModifiedDate=? WHERE budgetId=? AND isActive=1";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, budget.getBudgetName());
            pstmt.setInt(2, budget.getMonth());
            pstmt.setInt(3, budget.getYear());
            pstmt.setDouble(4, budget.getTotalAmount());
            pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            pstmt.setString(6, budget.getBudgetId());
            int rows = pstmt.executeUpdate();
            System.out.println("[DEBUG] updateBudget rows affected: " + rows);
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a budget (soft delete: sets isActive=0) in the database.
     */
    public boolean deleteBudget(String budgetId) {
        String sql = "UPDATE Budgets SET isActive=0, lastModifiedDate=? WHERE budgetId=? AND isActive=1";
        String sqlCat = "UPDATE BudgetCategories SET isActive=0, lastModifiedDate=? WHERE budgetId=? AND isActive=1";
        try {
            int rows = 0;
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                pstmt.setString(2, budgetId);
                rows = pstmt.executeUpdate();
                System.out.println("[DEBUG] deleteBudget rows affected: " + rows);
            }
            int catRows = 0;
            try (PreparedStatement pstmtCat = connection.prepareStatement(sqlCat)) {
                pstmtCat.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                pstmtCat.setString(2, budgetId);
                catRows = pstmtCat.executeUpdate();
                System.out.println("[DEBUG] deleteBudgetCategories rows affected: " + catRows);
            }
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    private Connection connection;

    /**
     * Returns all budgets for a given family from the database.
     */
    public List<Budget> getBudgetsForFamily(String familyId) throws SQLException {
        List<Budget> budgets = new ArrayList<>();
        String sql = "SELECT budgetId, familyId, budgetName, month, year, totalAmount, createdDate, lastModifiedDate, isActive, createdBy FROM Budgets WHERE familyId = ? AND isActive = 1 ORDER BY year DESC, month DESC, createdDate DESC";
        System.out.println("[DEBUG] getBudgetsForFamily: familyId=" + familyId);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, familyId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Budget budget = new Budget(
                    rs.getString("budgetId"),
                    rs.getString("familyId"),
                    rs.getString("budgetName"),
                    null, // description removed
                    rs.getInt("month"),
                    rs.getInt("year"),
                    rs.getDouble("totalAmount"),
                    rs.getTimestamp("createdDate"),
                    rs.getTimestamp("lastModifiedDate"),
                    rs.getBoolean("isActive"),
                    rs.getString("createdBy")
                );
                budgets.add(budget);
                System.out.println("[DEBUG] Found budget: " + budget);
            }
        }
        System.out.println("[DEBUG] Total budgets found: " + budgets.size());
        return budgets;
    }
    
    public BudgetManager(Connection connection) throws SQLException {
        this.connection = connection;
    }
    
    /**
     * Creates budget with category allocations (Transaction).
     * This is the most critical method - uses database transaction.
     */
    public boolean createBudgetWithAllocations(Budget budget, Map<String, Double> categoryAllocations) {
        try {
            connection.setAutoCommit(false);
            System.out.println("[DEBUG] createBudgetWithAllocations: budget=" + budget + ", categoryAllocations=" + categoryAllocations);
            // 1. Create budget
            String budgetSql = "INSERT INTO Budgets (budgetId, familyId, budgetName, month, year, totalAmount, createdBy, createdDate, lastModifiedDate, isActive) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            String budgetId = generateBudgetId();
            Timestamp now = new Timestamp(System.currentTimeMillis());
            try (PreparedStatement pstmt = connection.prepareStatement(budgetSql)) {
                pstmt.setString(1, budgetId);
                pstmt.setString(2, budget.getFamilyId());
                pstmt.setString(3, budget.getBudgetName());
                pstmt.setInt(4, budget.getMonth());
                pstmt.setInt(5, budget.getYear());
                pstmt.setDouble(6, budget.getTotalAmount());
                pstmt.setString(7, budget.getCreatedBy());
                pstmt.setTimestamp(8, now);
                pstmt.setTimestamp(9, now);
                pstmt.setBoolean(10, true);
                int rows = pstmt.executeUpdate();
                System.out.println("[DEBUG] Inserted into Budgets, rows affected: " + rows);
            }
            // 2. Create category allocations
            String allocationSql = "INSERT INTO BudgetCategories (budgetId, categoryId, " +
                                  "allocatedAmount, createdDate, lastModifiedDate, isActive) " +
                                  "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(allocationSql)) {
                for (Map.Entry<String, Double> entry : categoryAllocations.entrySet()) {
                    pstmt.setString(1, budgetId);
                    pstmt.setString(2, entry.getKey());
                    pstmt.setDouble(3, entry.getValue());
                    pstmt.setTimestamp(4, now);
                    pstmt.setTimestamp(5, now);
                    pstmt.setBoolean(6, true);
                    int rows = pstmt.executeUpdate();
                    System.out.println("[DEBUG] Inserted into BudgetCategories, rows affected: " + rows + ", categoryId=" + entry.getKey());
                }
            }
            connection.commit();
            budget.setBudgetId(budgetId);
            System.out.println("[DEBUG] Budget creation committed successfully.");
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println("[DEBUG] Exception in createBudgetWithAllocations: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Gets budget with actual spending comparison.
     * Joins with Expenses table to calculate budget vs actual.
     */
    public Map<String, Object> getBudgetPerformance(String budgetId) {
        Map<String, Object> performance = new HashMap<>();
        
        String sql = "SELECT bc.categoryId, c.categoryName, bc.allocatedAmount, " +
                    "COALESCE(SUM(e.amount), 0) as actualSpent " +
                    "FROM BudgetCategories bc " +
                    "JOIN Categories c ON bc.categoryId = c.categoryId " +
                    "LEFT JOIN Expenses e ON bc.categoryId = e.categoryId " +
                    "AND e.isActive = 1 " +
                    "WHERE bc.budgetId = ? AND bc.isActive = 1 " +
                    "GROUP BY bc.categoryId, c.categoryName, bc.allocatedAmount";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, budgetId);
            ResultSet rs = pstmt.executeQuery();
            
            List<Map<String, Object>> categories = new ArrayList<>();
            double totalBudgeted = 0;
            double totalSpent = 0;
            
            while (rs.next()) {
                Map<String, Object> category = new HashMap<>();
                double budgeted = rs.getDouble("allocatedAmount");
                double actual = rs.getDouble("actualSpent");
                
                category.put("categoryId", rs.getString("categoryId"));
                category.put("categoryName", rs.getString("categoryName"));
                category.put("budgeted", budgeted);
                category.put("actual", actual);
                category.put("variance", budgeted - actual);
                category.put("utilizationRate", budgeted > 0 ? (actual / budgeted) * 100 : 0);
                
                categories.add(category);
                totalBudgeted += budgeted;
                totalSpent += actual;
            }
            
            performance.put("categories", categories);
            performance.put("totalBudgeted", totalBudgeted);
            performance.put("totalSpent", totalSpent);
            performance.put("remainingBudget", totalBudgeted - totalSpent);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return performance;
    }
    
    private String generateBudgetId() {
        return "BDG" + System.currentTimeMillis() % 100000;
    }
}
