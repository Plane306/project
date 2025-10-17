package model.dao;

import model.SavingsGoal;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * DAO Manager for Savings Goals (F107).
 * Handles CRUD operations and goal progress tracking.
 */
public class SavingsGoalManager {

    private Connection connection;

    public SavingsGoalManager(Connection connection) throws SQLException {
        this.connection = connection;
    }

    /**
     * Creates a new savings goal with retry logic for duplicate IDs.
     */
    public boolean createSavingsGoal(SavingsGoal goal) throws SQLException {
        String sql = "INSERT INTO SavingsGoals (goalId, familyId, goalName, description, " +
                "targetAmount, currentAmount, targetDate, createdDate, lastModifiedDate, " +
                "isActive, isCompleted, createdBy) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        int maxAttempts = 3;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            goal.setGoalId(generateGoalId());

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                Timestamp now = new Timestamp(System.currentTimeMillis());

                pstmt.setString(1, goal.getGoalId());
                pstmt.setString(2, goal.getFamilyId());
                pstmt.setString(3, goal.getGoalName().trim());
                pstmt.setString(4, goal.getDescription());
                pstmt.setDouble(5, goal.getTargetAmount());
                pstmt.setDouble(6, goal.getCurrentAmount());

                if (goal.getTargetDate() != null) {
                    pstmt.setDate(7, new java.sql.Date(goal.getTargetDate().getTime()));
                } else {
                    pstmt.setNull(7, Types.DATE);
                }

                pstmt.setTimestamp(8, now);
                pstmt.setTimestamp(9, now);
                pstmt.setBoolean(10, true);
                pstmt.setBoolean(11, false);
                pstmt.setString(12, goal.getCreatedBy());

                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;

            } catch (SQLException e) {
                if (e.getMessage().contains("UNIQUE constraint failed: SavingsGoals.goalId")) {
                    if (attempt == maxAttempts - 1)
                        return false;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return false;
                    }
                    continue;
                }
                throw e;
            }
        }
        return false;
    }

    /**
     * Adds amount to savings goal with transaction support.
     * Auto-completes goal if target is reached.
     */
    public boolean addToSavingsGoal(String goalId, double amount) {
        if (amount <= 0)
            return false;

        try {
            connection.setAutoCommit(false);

            String selectSql = "SELECT currentAmount, targetAmount, isCompleted " +
                    "FROM SavingsGoals WHERE goalId = ? AND isActive = 1";

            double currentAmount = 0;
            double targetAmount = 0;
            boolean isCompleted = false;

            try (PreparedStatement pstmt = connection.prepareStatement(selectSql)) {
                pstmt.setString(1, goalId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    currentAmount = rs.getDouble("currentAmount");
                    targetAmount = rs.getDouble("targetAmount");
                    isCompleted = rs.getBoolean("isCompleted");
                } else {
                    connection.rollback();
                    return false;
                }
            }

            if (isCompleted) {
                connection.rollback();
                return false;
            }

            double newAmount = currentAmount + amount;
            boolean shouldComplete = newAmount >= targetAmount;

            String updateSql = "UPDATE SavingsGoals SET currentAmount = ?, isCompleted = ?, " +
                    "lastModifiedDate = ? WHERE goalId = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(updateSql)) {
                pstmt.setDouble(1, newAmount);
                pstmt.setBoolean(2, shouldComplete);
                pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                pstmt.setString(4, goalId);

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    connection.commit();
                    return true;
                } else {
                    connection.rollback();
                    return false;
                }
            }

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
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
     * Gets all active savings goals for a family.
     */
    public List<SavingsGoal> getFamilySavingsGoals(String familyId) throws SQLException {
        List<SavingsGoal> goals = new ArrayList<>();

        String sql = "SELECT * FROM SavingsGoals WHERE familyId = ? AND isActive = 1 " +
                "ORDER BY isCompleted ASC, targetDate ASC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, familyId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                goals.add(extractSavingsGoalFromResultSet(rs));
            }
        }

        return goals;
    }

    /**
     * Gets a specific savings goal by ID.
     */
    public SavingsGoal getSavingsGoalById(String goalId) throws SQLException {
        String sql = "SELECT * FROM SavingsGoals WHERE goalId = ? AND isActive = 1";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, goalId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractSavingsGoalFromResultSet(rs);
            }
        }

        return null;
    }

    /**
     * Updates savings goal details.
     */
    public boolean updateSavingsGoal(SavingsGoal goal) throws SQLException {
        String sql = "UPDATE SavingsGoals SET goalName = ?, description = ?, " +
                "targetAmount = ?, targetDate = ?, isCompleted = ?, lastModifiedDate = ? " +
                "WHERE goalId = ? AND isActive = 1";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Recalculate completion status
            boolean isCompleted = goal.getCurrentAmount() >= goal.getTargetAmount();

            pstmt.setString(1, goal.getGoalName().trim());
            pstmt.setString(2, goal.getDescription());
            pstmt.setDouble(3, goal.getTargetAmount());

            if (goal.getTargetDate() != null) {
                pstmt.setDate(4, new java.sql.Date(goal.getTargetDate().getTime()));
            } else {
                pstmt.setNull(4, Types.DATE);
            }

            pstmt.setBoolean(5, isCompleted); // Add this line
            pstmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            pstmt.setString(7, goal.getGoalId());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Soft deletes a savings goal.
     */
    public boolean deleteSavingsGoal(String goalId) throws SQLException {
        String sql = "UPDATE SavingsGoals SET isActive = 0, lastModifiedDate = ? " +
                "WHERE goalId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            pstmt.setString(2, goalId);

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Gets completed goals for a family.
     */
    public List<SavingsGoal> getCompletedGoals(String familyId) throws SQLException {
        List<SavingsGoal> goals = new ArrayList<>();

        String sql = "SELECT * FROM SavingsGoals WHERE familyId = ? AND isActive = 1 " +
                "AND isCompleted = 1 ORDER BY lastModifiedDate DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, familyId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                goals.add(extractSavingsGoalFromResultSet(rs));
            }
        }

        return goals;
    }

    /**
     * Gets active goals for a family.
     */
    public List<SavingsGoal> getActiveGoals(String familyId) throws SQLException {
        List<SavingsGoal> goals = new ArrayList<>();

        String sql = "SELECT * FROM SavingsGoals WHERE familyId = ? AND isActive = 1 " +
                "AND isCompleted = 0 ORDER BY targetDate ASC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, familyId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                goals.add(extractSavingsGoalFromResultSet(rs));
            }
        }

        return goals;
    }

    /**
     * Gets total saved across all goals.
     */
    public double getTotalSaved(String familyId) throws SQLException {
        String sql = "SELECT SUM(currentAmount) as total FROM SavingsGoals " +
                "WHERE familyId = ? AND isActive = 1";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, familyId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total");
            }
        }

        return 0.0;
    }

    /**
     * Gets count of active goals.
     */
    public int getActiveGoalCount(String familyId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM SavingsGoals " +
                "WHERE familyId = ? AND isActive = 1 AND isCompleted = 0";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, familyId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }
        }

        return 0;
    }

    /**
     * Extracts SavingsGoal from ResultSet.
     */
    private SavingsGoal extractSavingsGoalFromResultSet(ResultSet rs) throws SQLException {
        SavingsGoal goal = new SavingsGoal();

        goal.setGoalId(rs.getString("goalId"));
        goal.setFamilyId(rs.getString("familyId"));
        goal.setGoalName(rs.getString("goalName"));
        goal.setDescription(rs.getString("description"));
        goal.setTargetAmount(rs.getDouble("targetAmount"));
        goal.setCurrentAmount(rs.getDouble("currentAmount"));

        java.sql.Date targetDate = rs.getDate("targetDate");
        if (targetDate != null) {
            goal.setTargetDate(new Date(targetDate.getTime()));
        }

        Timestamp createdDate = rs.getTimestamp("createdDate");
        if (createdDate != null) {
            goal.setCreatedDate(new Date(createdDate.getTime()));
        }

        Timestamp lastModifiedDate = rs.getTimestamp("lastModifiedDate");
        if (lastModifiedDate != null) {
            goal.setLastModifiedDate(new Date(lastModifiedDate.getTime()));
        }

        goal.setActive(rs.getBoolean("isActive"));
        goal.setCompleted(rs.getBoolean("isCompleted"));
        goal.setCreatedBy(rs.getString("createdBy"));

        return goal;
    }

    /**
     * Generates unique goal ID.
     */
    private String generateGoalId() {
        return "G" + String.format("%07d", System.currentTimeMillis() % 10000000);
    }
}