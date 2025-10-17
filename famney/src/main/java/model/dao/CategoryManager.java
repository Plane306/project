package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Category;
import controller.IdGenerator;
import controller.DateUtil;

// Data Access Object for Category table operations
// Handles category creation, retrieval, updates, and default category initialisation
public class CategoryManager {
    
    private Connection conn;
    
    // Constructor takes database connection from ConnServlet
    public CategoryManager(Connection conn) throws SQLException {
        this.conn = conn;
    }
    
    // Create new category in database
    // Generates category ID automatically
    // Returns true if successful, false if failed (duplicate name)
    // Uses retry logic if duplicate categoryId occurs (2-layer prevention)
    public boolean createCategory(Category category) throws SQLException {
        String sql = "INSERT INTO Categories (categoryId, familyId, categoryName, categoryType, " +
                     "isDefault, description, createdDate, lastModifiedDate, isActive) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        // Try up to 3 times in case of duplicate categoryId
        int maxAttempts = 3;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            // Generate unique category ID for each attempt
            category.setCategoryId(IdGenerator.generateCategoryId());
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                String currentDateTime = DateUtil.getCurrentDateTime();
                
                stmt.setString(1, category.getCategoryId());
                stmt.setString(2, category.getFamilyId());
                stmt.setString(3, category.getCategoryName().trim());
                stmt.setString(4, category.getCategoryType());
                stmt.setBoolean(5, category.isDefault());
                stmt.setString(6, category.getDescription());
                stmt.setString(7, currentDateTime);
                stmt.setString(8, currentDateTime);
                stmt.setBoolean(9, true);
                
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
                
            } catch (SQLException e) {
                String errorMsg = e.getMessage();
                
                // If duplicate category name within family - return false immediately
                if (errorMsg.contains("UNIQUE constraint failed: Categories.familyId, Categories.categoryName")) {
                    return false;
                }
                
                // If duplicate categoryId - retry with new ID
                if (errorMsg.contains("UNIQUE constraint failed: Categories.categoryId")) {
                    if (attempt == maxAttempts - 1) {
                        return false;
                    }
                    // Continue to next attempt
                    try {
                        Thread.sleep(10); // Small delay before retry
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                    continue;
                }
                
                // Other SQL errors - throw exception
                throw e;
            }
        }
        
        return false;
    }
    
    // Get all categories for a family
    // Returns both expense and income categories
    // Used in categories.jsp to display all categories
    public List<Category> getFamilyCategories(String familyId) throws SQLException {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM Categories WHERE familyId = ? AND isActive = 1 " +
                     "ORDER BY categoryType, categoryName";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, familyId);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Category category = new Category();
                category.setCategoryId(rs.getString("categoryId"));
                category.setFamilyId(rs.getString("familyId"));
                category.setCategoryName(rs.getString("categoryName"));
                category.setCategoryType(rs.getString("categoryType"));
                category.setDefault(rs.getBoolean("isDefault"));
                category.setDescription(rs.getString("description"));
                category.setCreatedDate(DateUtil.parseToTimestamp(rs.getString("createdDate")));
                category.setLastModifiedDate(DateUtil.parseToTimestamp(rs.getString("lastModifiedDate")));
                category.setActive(rs.getBoolean("isActive"));
                
                categories.add(category);
            }
        }
        
        return categories;
    }
    
    // Get categories filtered by type (Expense or Income)
    // Used when user wants to see only expense or income categories
    public List<Category> getCategoriesByType(String familyId, String categoryType) throws SQLException {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM Categories WHERE familyId = ? AND categoryType = ? AND isActive = 1 " +
                     "ORDER BY categoryName";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, familyId);
            stmt.setString(2, categoryType);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Category category = new Category();
                category.setCategoryId(rs.getString("categoryId"));
                category.setFamilyId(rs.getString("familyId"));
                category.setCategoryName(rs.getString("categoryName"));
                category.setCategoryType(rs.getString("categoryType"));
                category.setDefault(rs.getBoolean("isDefault"));
                category.setDescription(rs.getString("description"));
                category.setCreatedDate(DateUtil.parseToTimestamp(rs.getString("createdDate")));
                category.setLastModifiedDate(DateUtil.parseToTimestamp(rs.getString("lastModifiedDate")));
                category.setActive(rs.getBoolean("isActive"));
                
                categories.add(category);
            }
        }
        
        return categories;
    }
    
    // Find category by category ID
    // Used when editing a category
    public Category findByCategoryId(String categoryId) throws SQLException {
        String sql = "SELECT * FROM Categories WHERE categoryId = ? AND isActive = 1";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categoryId);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Category category = new Category();
                category.setCategoryId(rs.getString("categoryId"));
                category.setFamilyId(rs.getString("familyId"));
                category.setCategoryName(rs.getString("categoryName"));
                category.setCategoryType(rs.getString("categoryType"));
                category.setDefault(rs.getBoolean("isDefault"));
                category.setDescription(rs.getString("description"));
                category.setCreatedDate(DateUtil.parseToTimestamp(rs.getString("createdDate")));
                category.setLastModifiedDate(DateUtil.parseToTimestamp(rs.getString("lastModifiedDate")));
                category.setActive(rs.getBoolean("isActive"));
                
                return category;
            }
            
            return null;
        }
    }
    
    // Update category details
    // Only allows updating name and description, not type
    // Cannot update default categories (enforced in servlet)
    public boolean updateCategory(Category category) throws SQLException {
        String sql = "UPDATE Categories SET categoryName = ?, description = ?, " +
                     "lastModifiedDate = ? WHERE categoryId = ? AND isDefault = 0";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category.getCategoryName().trim());
            stmt.setString(2, category.getDescription());
            stmt.setString(3, DateUtil.getCurrentDateTime());
            stmt.setString(4, category.getCategoryId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    // Soft delete category (set isActive to false)
    // Only Family Head can delete categories
    // Cannot delete default categories (enforced in servlet)
    // Data is preserved for analytics purposes
    public boolean deleteCategory(String categoryId) throws SQLException {
        String sql = "UPDATE Categories SET isActive = 0, lastModifiedDate = ? " +
                     "WHERE categoryId = ? AND isDefault = 0";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, DateUtil.getCurrentDateTime());
            stmt.setString(2, categoryId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    // Check if category name already exists for family
    // Used during creation and update to prevent duplicates
    // Category names must be unique within each family
    public boolean categoryNameExists(String familyId, String categoryName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Categories WHERE familyId = ? AND categoryName = ? AND isActive = 1";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, familyId);
            stmt.setString(2, categoryName.trim());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        
        return false;
    }
    
    // Check if category name exists excluding current category (for updates)
    // Used when editing category to allow keeping same name
    public boolean categoryNameExistsExcept(String familyId, String categoryName, String categoryId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Categories WHERE familyId = ? AND categoryName = ? " +
                     "AND categoryId != ? AND isActive = 1";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, familyId);
            stmt.setString(2, categoryName.trim());
            stmt.setString(3, categoryId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        
        return false;
    }
    
    // Initialise default categories for new family
    // Called when family is created (from CreateFamilyServlet)
    // Creates 6 expense categories and 4 income categories
    public void initialiseFamilyDefaultCategories(String familyId) throws SQLException {
        // Default expense categories
        String[][] expenseCategories = {
            {"Food & Dining", "Groceries, restaurants, takeaways"},
            {"Transportation", "Petrol, public transport, car maintenance"},
            {"Utilities", "Electricity, water, gas, internet"},
            {"Entertainment", "Movies, games, hobbies"},
            {"Healthcare", "Medical expenses, insurance"},
            {"Shopping", "Clothes, electronics, household items"}
        };
        
        // Default income categories
        String[][] incomeCategories = {
            {"Salary", "Monthly salary from employment"},
            {"Freelance", "Freelance work and contracts"},
            {"Allowance", "Pocket money and allowances"},
            {"Investment", "Dividends, interest, capital gains"}
        };
        
        // Create expense categories
        for (String[] catData : expenseCategories) {
            Category category = new Category(familyId, catData[0], "Expense", true, catData[1]);
            createCategory(category);
        }
        
        // Create income categories
        for (String[] catData : incomeCategories) {
            Category category = new Category(familyId, catData[0], "Income", true, catData[1]);
            createCategory(category);
        }
    }
    
    // Count total categories for a family
    // Used for statistics display
    public int getCategoryCount(String familyId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Categories WHERE familyId = ? AND isActive = 1";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, familyId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        
        return 0;
    }
    
    // Count categories by type
    // Used for statistics display on categories page
    public int getCategoryCountByType(String familyId, String categoryType) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Categories WHERE familyId = ? AND categoryType = ? AND isActive = 1";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, familyId);
            stmt.setString(2, categoryType);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        
        return 0;
    }
    
    // Count custom (non-default) categories
    // Used for statistics display
    public int getCustomCategoryCount(String familyId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Categories WHERE familyId = ? AND isDefault = 0 AND isActive = 1";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, familyId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        
        return 0;
    }
}