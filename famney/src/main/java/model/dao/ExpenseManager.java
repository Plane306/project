package model.dao;

import model.Expense;
import java.sql.*;
import java.util.*;

public class ExpenseManager {
	private Connection connection;

	public ExpenseManager(Connection connection) {
		this.connection = connection;
	}

	// Add expense to DB
	public boolean addExpense(Expense expense) {
		String sql = "INSERT INTO Expenses (expenseId, familyId, userId, categoryId, amount, description, expenseDate, createdDate, lastModifiedDate, isActive) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			String expenseId = UUID.randomUUID().toString().substring(0,8);
			ps.setString(1, expenseId);
			ps.setString(2, expense.getFamilyId());
			ps.setString(3, expense.getUserId());
			ps.setString(4, expense.getCategoryId());
			ps.setDouble(5, expense.getAmount());
			ps.setString(6, expense.getDescription());
			ps.setTimestamp(7, new java.sql.Timestamp(expense.getExpenseDate().getTime()));
			ps.setTimestamp(8, new java.sql.Timestamp(new java.util.Date().getTime()));
			ps.setTimestamp(9, new java.sql.Timestamp(new java.util.Date().getTime()));
			ps.setBoolean(10, true);
			int rows = ps.executeUpdate();
			return rows > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Get expense by ID
	public Expense getExpenseById(String expenseId) {
		String sql = "SELECT * FROM Expenses WHERE expenseId = ? AND isActive = 1";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, expenseId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return mapResultSetToExpense(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Get all expenses for a family
	public List<Expense> getAllExpenses(String familyId) {
		List<Expense> expenses = new ArrayList<>();
		String sql = "SELECT * FROM Expenses WHERE familyId = ? AND isActive = 1 ORDER BY expenseDate DESC";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, familyId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				expenses.add(mapResultSetToExpense(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return expenses;
	}

	// Update expense
	public boolean updateExpense(Expense expense) {
		String sql = "UPDATE Expenses SET categoryId=?, amount=?, description=?, expenseDate=?, lastModifiedDate=? WHERE expenseId=? AND isActive=1";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, expense.getCategoryId());
			ps.setDouble(2, expense.getAmount());
			ps.setString(3, expense.getDescription());
			ps.setTimestamp(4, new java.sql.Timestamp(expense.getExpenseDate().getTime()));
			ps.setTimestamp(5, new java.sql.Timestamp(new java.util.Date().getTime()));
			ps.setString(6, expense.getExpenseId());
			int rows = ps.executeUpdate();
			return rows > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Delete expense (soft delete)
	public boolean deleteExpense(String expenseId) {
		String sql = "UPDATE Expenses SET isActive=0, lastModifiedDate=? WHERE expenseId=?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setTimestamp(1, new java.sql.Timestamp(new java.util.Date().getTime()));
			ps.setString(2, expenseId);
			int rows = ps.executeUpdate();
			return rows > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Helper: map ResultSet to Expense
	private Expense mapResultSetToExpense(ResultSet rs) throws SQLException {
		return new Expense(
			rs.getString("expenseId"),
			rs.getString("familyId"),
			rs.getString("userId"),
			rs.getString("categoryId"),
			rs.getDouble("amount"),
			rs.getString("description"),
			rs.getTimestamp("expenseDate"),
			rs.getTimestamp("createdDate"),
			rs.getTimestamp("lastModifiedDate"),
			rs.getBoolean("isActive"),
			null // receiptUrl (future)
		);
	}
}
