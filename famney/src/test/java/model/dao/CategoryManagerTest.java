package model.dao;

import model.Category;
import controller.DateUtil;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

// JUnit tests for CategoryManager DAO class
// Tests category CRUD operations, default category initialisation, and validation
// Uses in-memory SQLite database for test isolation
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CategoryManagerTest {
    
    private Connection testConn;
    private CategoryManager categoryManager;
    private String testFamilyId = "F0001";
    
    // Set up test database before all tests
    // Creates Categories table and initialises CategoryManager
    @BeforeAll
    public void setUpDatabase() throws Exception {
        // Use in-memory database for testing
        testConn = DriverManager.getConnection("jdbc:sqlite::memory:");
        
        // Create Categories table for testing
        Statement stmt = testConn.createStatement();
        stmt.execute(
            "CREATE TABLE Categories (" +
            "categoryId VARCHAR(8) PRIMARY KEY, " +
            "familyId VARCHAR(8) NOT NULL, " +
            "categoryName VARCHAR(50) NOT NULL, " +
            "categoryType VARCHAR(10) NOT NULL CHECK (categoryType IN ('Expense', 'Income')), " +
            "isDefault BOOLEAN NOT NULL DEFAULT FALSE, " +
            "description VARCHAR(200), " +
            "createdDate TEXT NOT NULL, " +
            "lastModifiedDate TEXT NOT NULL, " +
            "isActive BOOLEAN NOT NULL DEFAULT TRUE, " +
            "CONSTRAINT uk_category_family_name UNIQUE (familyId, categoryName))"
        );
        
        // Initialise CategoryManager with test connection
        categoryManager = new CategoryManager(testConn);
    }
    
    // Clean up database after each test for isolation
    @AfterEach
    public void cleanDatabase() throws SQLException {
        Statement stmt = testConn.createStatement();
        stmt.execute("DELETE FROM Categories");
    }
    
    // Close database connection after all tests complete
    @AfterAll
    public void closeDatabase() throws SQLException {
        if (testConn != null && !testConn.isClosed()) {
            testConn.close();
        }
    }
    
    // Test creating a new category successfully
    // Should generate category ID automatically
    @Test
    public void testCreateCategory_Success() throws SQLException {
        // Create test category
        Category category = new Category();
        category.setFamilyId(testFamilyId);
        category.setCategoryName("Test Category");
        category.setCategoryType("Expense");
        category.setDefault(false);
        category.setDescription("Test description");
        
        // Create category in database
        boolean result = categoryManager.createCategory(category);
        
        // Verify creation was successful
        assertTrue(result, "Category should be created successfully");
        assertNotNull(category.getCategoryId(), "Category ID should be generated");
    }
    
    // Test that created date is in correct format
    // Should be "YYYY-MM-DD HH:MM:SS" not milliseconds
    @Test
    public void testCreateCategory_DateFormat() throws SQLException {
        // Create test category
        Category category = new Category();
        category.setFamilyId(testFamilyId);
        category.setCategoryName("Date Test");
        category.setCategoryType("Income");
        category.setDefault(false);
        
        // Create category in database
        categoryManager.createCategory(category);
        
        // Find category to get dates from database
        Category found = categoryManager.findByCategoryId(category.getCategoryId());
        
        // Verify date format is readable (not milliseconds)
        assertNotNull(found.getCreatedDate(), "Created date should not be null");
        assertNotNull(found.getLastModifiedDate(), "Last modified date should not be null");
        
        // Parse dates to verify they're in correct format
        String createdDate = DateUtil.formatDateTime(found.getCreatedDate());
        assertTrue(createdDate.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"),
                  "Date should be in format YYYY-MM-DD HH:MM:SS");
    }
    
    // Test creating expense category
    @Test
    public void testCreateCategory_ExpenseType() throws SQLException {
        // Create expense category
        Category category = new Category(testFamilyId, "Food", "Expense", false, "Food expenses");
        
        boolean result = categoryManager.createCategory(category);
        
        // Verify creation successful
        assertTrue(result, "Expense category should be created");
        
        // Verify category type
        Category found = categoryManager.findByCategoryId(category.getCategoryId());
        assertEquals("Expense", found.getCategoryType());
        assertTrue(found.isExpenseCategory());
        assertFalse(found.isIncomeCategory());
    }
    
    // Test creating income category
    @Test
    public void testCreateCategory_IncomeType() throws SQLException {
        // Create income category
        Category category = new Category(testFamilyId, "Salary", "Income", false, "Monthly salary");
        
        boolean result = categoryManager.createCategory(category);
        
        // Verify creation successful
        assertTrue(result, "Income category should be created");
        
        // Verify category type
        Category found = categoryManager.findByCategoryId(category.getCategoryId());
        assertEquals("Income", found.getCategoryType());
        assertTrue(found.isIncomeCategory());
        assertFalse(found.isExpenseCategory());
    }
    
    // Test creating default category
    // Default categories are created during family initialisation
    @Test
    public void testCreateCategory_DefaultCategory() throws SQLException {
        // Create default category
        Category category = new Category(testFamilyId, "Utilities", "Expense", true, "Electric, water, gas");
        
        boolean result = categoryManager.createCategory(category);
        
        // Verify creation successful
        assertTrue(result, "Default category should be created");
        
        // Verify it's marked as default
        Category found = categoryManager.findByCategoryId(category.getCategoryId());
        assertTrue(found.isDefault());
        assertFalse(found.canBeDeleted()); // Default categories cannot be deleted
    }
    
    // Test that duplicate category names within same family are rejected
    // Category names must be unique per family
    @Test
    public void testCreateCategory_DuplicateName() throws SQLException {
        // Create first category
        Category category1 = new Category(testFamilyId, "Transport", "Expense", false);
        categoryManager.createCategory(category1);
        
        // Try to create second category with same name in same family
        Category category2 = new Category(testFamilyId, "Transport", "Expense", false);
        boolean result = categoryManager.createCategory(category2);
        
        // Verify second creation failed
        assertFalse(result, "Should not create duplicate category name within same family");
    }
    
    // Test that same category name can exist in different families
    @Test
    public void testCreateCategory_SameNameDifferentFamily() throws SQLException {
        // Create category in first family
        Category category1 = new Category(testFamilyId, "Entertainment", "Expense", false);
        categoryManager.createCategory(category1);
        
        // Create same category name in different family
        Category category2 = new Category("F0002", "Entertainment", "Expense", false);
        boolean result = categoryManager.createCategory(category2);
        
        // Verify second creation succeeded
        assertTrue(result, "Should allow same category name in different families");
    }
    
    // Test getting all categories for a family
    // Should return both expense and income categories
    @Test
    public void testGetFamilyCategories() throws SQLException {
        // Create multiple categories
        Category expense1 = new Category(testFamilyId, "Food", "Expense", false);
        Category expense2 = new Category(testFamilyId, "Transport", "Expense", false);
        Category income1 = new Category(testFamilyId, "Salary", "Income", false);
        
        categoryManager.createCategory(expense1);
        categoryManager.createCategory(expense2);
        categoryManager.createCategory(income1);
        
        // Get all family categories
        List<Category> categories = categoryManager.getFamilyCategories(testFamilyId);
        
        // Verify results
        assertNotNull(categories, "Should return list of categories");
        assertEquals(3, categories.size(), "Should have 3 categories");
    }
    
    // Test getting categories filtered by type
    @Test
    public void testGetCategoriesByType() throws SQLException {
        // Create mixed categories
        Category expense1 = new Category(testFamilyId, "Food", "Expense", false);
        Category expense2 = new Category(testFamilyId, "Shopping", "Expense", false);
        Category income1 = new Category(testFamilyId, "Salary", "Income", false);
        Category income2 = new Category(testFamilyId, "Freelance", "Income", false);
        
        categoryManager.createCategory(expense1);
        categoryManager.createCategory(expense2);
        categoryManager.createCategory(income1);
        categoryManager.createCategory(income2);
        
        // Get expense categories only
        List<Category> expenseCategories = categoryManager.getCategoriesByType(testFamilyId, "Expense");
        
        // Get income categories only
        List<Category> incomeCategories = categoryManager.getCategoriesByType(testFamilyId, "Income");
        
        // Verify results
        assertEquals(2, expenseCategories.size(), "Should have 2 expense categories");
        assertEquals(2, incomeCategories.size(), "Should have 2 income categories");
        
        // Verify all returned categories have correct type
        for (Category cat : expenseCategories) {
            assertEquals("Expense", cat.getCategoryType());
        }
        for (Category cat : incomeCategories) {
            assertEquals("Income", cat.getCategoryType());
        }
    }
    
    // Test finding category by ID
    @Test
    public void testFindByCategoryId_Exists() throws SQLException {
        // Create test category
        Category category = new Category(testFamilyId, "Healthcare", "Expense", false, "Medical expenses");
        categoryManager.createCategory(category);
        
        String categoryId = category.getCategoryId();
        
        // Find category by ID
        Category found = categoryManager.findByCategoryId(categoryId);
        
        // Verify category was found
        assertNotNull(found, "Should find category by ID");
        assertEquals(categoryId, found.getCategoryId());
        assertEquals("Healthcare", found.getCategoryName());
        assertEquals("Medical expenses", found.getDescription());
    }
    
    // Test finding category with non-existent ID
    @Test
    public void testFindByCategoryId_NotExists() throws SQLException {
        // Try to find non-existent category
        Category found = categoryManager.findByCategoryId("C9999");
        
        // Verify no category was found
        assertNull(found, "Should return null for non-existent category");
    }
    
    // Test that findByCategoryId only returns active categories
    @Test
    public void testFindByCategoryId_OnlyActive() throws SQLException {
        // Create and delete category
        Category category = new Category(testFamilyId, "Deleted", "Expense", false);
        categoryManager.createCategory(category);
        
        String categoryId = category.getCategoryId();
        categoryManager.deleteCategory(categoryId);
        
        // Try to find deleted category
        Category found = categoryManager.findByCategoryId(categoryId);
        
        // Verify deleted category is not found
        assertNull(found, "Should not find inactive category");
    }
    
    // Test updating category
    // Can update name and description, but not type
    @Test
    public void testUpdateCategory() throws SQLException {
        // Create test category
        Category category = new Category(testFamilyId, "Old Name", "Expense", false, "Old description");
        categoryManager.createCategory(category);
        
        // Update category
        category.setCategoryName("New Name");
        category.setDescription("New description");
        
        boolean updated = categoryManager.updateCategory(category);
        
        // Verify update was successful
        assertTrue(updated, "Category should be updated successfully");
        
        // Verify changes persisted in database
        Category found = categoryManager.findByCategoryId(category.getCategoryId());
        assertEquals("New Name", found.getCategoryName());
        assertEquals("New description", found.getDescription());
    }
    
    // Test that default categories cannot be updated
    // Update method should fail for default categories
    @Test
    public void testUpdateCategory_CannotUpdateDefault() throws SQLException {
        // Create default category
        Category category = new Category(testFamilyId, "Default Category", "Expense", true);
        categoryManager.createCategory(category);
        
        // Try to update default category
        category.setCategoryName("New Name");
        boolean updated = categoryManager.updateCategory(category);
        
        // Verify update failed
        assertFalse(updated, "Should not be able to update default category");
    }
    
    // Test soft deleting category
    // Sets isActive to false, preserving data for analytics
    @Test
    public void testDeleteCategory() throws SQLException {
        // Create test category
        Category category = new Category(testFamilyId, "To Delete", "Expense", false);
        categoryManager.createCategory(category);
        
        String categoryId = category.getCategoryId();
        
        // Soft delete category
        boolean deleted = categoryManager.deleteCategory(categoryId);
        
        // Verify deletion was successful
        assertTrue(deleted, "Category should be soft deleted successfully");
        
        // Verify category cannot be found (only active categories returned)
        Category found = categoryManager.findByCategoryId(categoryId);
        assertNull(found, "Deleted category should not be found");
        
        // Verify category data is preserved in database
        Statement stmt = testConn.createStatement();
        var rs = stmt.executeQuery("SELECT isActive FROM Categories WHERE categoryId = '" + categoryId + "'");
        assertTrue(rs.next(), "Category record should still exist in database");
        assertFalse(rs.getBoolean("isActive"), "Category should be marked as inactive");
    }
    
    // Test that default categories cannot be deleted
    @Test
    public void testDeleteCategory_CannotDeleteDefault() throws SQLException {
        // Create default category
        Category category = new Category(testFamilyId, "Default Category", "Expense", true);
        categoryManager.createCategory(category);
        
        String categoryId = category.getCategoryId();
        
        // Try to delete default category
        boolean deleted = categoryManager.deleteCategory(categoryId);
        
        // Verify deletion failed
        assertFalse(deleted, "Should not be able to delete default category");
        
        // Verify category still exists
        Category found = categoryManager.findByCategoryId(categoryId);
        assertNotNull(found, "Default category should still exist");
    }
    
    // Test checking if category name exists within family
    @Test
    public void testCategoryNameExists() throws SQLException {
        // Create test category
        Category category = new Category(testFamilyId, "Existing Name", "Expense", false);
        categoryManager.createCategory(category);
        
        // Check if name exists
        boolean exists = categoryManager.categoryNameExists(testFamilyId, "Existing Name");
        boolean notExists = categoryManager.categoryNameExists(testFamilyId, "Non-Existent");
        
        // Verify results
        assertTrue(exists, "Should find existing category name");
        assertFalse(notExists, "Should not find non-existent category name");
    }
    
    // Test checking if category name exists excluding current category
    // Used during updates to allow keeping same name
    @Test
    public void testCategoryNameExistsExcept() throws SQLException {
        // Create two categories
        Category category1 = new Category(testFamilyId, "First", "Expense", false);
        Category category2 = new Category(testFamilyId, "Second", "Expense", false);
        
        categoryManager.createCategory(category1);
        categoryManager.createCategory(category2);
        
        String category1Id = category1.getCategoryId();
        
        // Check if "First" exists excluding category1 itself
        boolean existsExcept = categoryManager.categoryNameExistsExcept(testFamilyId, "First", category1Id);
        
        // Check if "Second" exists excluding category1
        boolean otherExists = categoryManager.categoryNameExistsExcept(testFamilyId, "Second", category1Id);
        
        // Verify results
        assertFalse(existsExcept, "Should not find category when excluding itself");
        assertTrue(otherExists, "Should find other category with that name");
    }
    
    // Test initialising default categories for new family
    // Should create 6 expense and 4 income categories
    @Test
    public void testInitialiseFamilyDefaultCategories() throws SQLException {
        // Initialise default categories
        categoryManager.initialiseFamilyDefaultCategories(testFamilyId);
        
        // Get all categories
        List<Category> allCategories = categoryManager.getFamilyCategories(testFamilyId);
        List<Category> expenseCategories = categoryManager.getCategoriesByType(testFamilyId, "Expense");
        List<Category> incomeCategories = categoryManager.getCategoriesByType(testFamilyId, "Income");
        
        // Verify correct number of categories created
        assertEquals(10, allCategories.size(), "Should create 10 default categories");
        assertEquals(6, expenseCategories.size(), "Should create 6 expense categories");
        assertEquals(4, incomeCategories.size(), "Should create 4 income categories");
        
        // Verify all are marked as default
        for (Category cat : allCategories) {
            assertTrue(cat.isDefault(), "All initialised categories should be default");
        }
    }
    
    // Test getting category count statistics
    @Test
    public void testGetCategoryCount() throws SQLException {
        // Create multiple categories
        Category expense1 = new Category(testFamilyId, "Food", "Expense", false);
        Category expense2 = new Category(testFamilyId, "Transport", "Expense", false);
        Category income1 = new Category(testFamilyId, "Salary", "Income", false);
        
        categoryManager.createCategory(expense1);
        categoryManager.createCategory(expense2);
        categoryManager.createCategory(income1);
        
        // Get total count
        int totalCount = categoryManager.getCategoryCount(testFamilyId);
        
        // Verify count
        assertEquals(3, totalCount, "Total category count should be 3");
    }
    
    // Test getting category count by type
    @Test
    public void testGetCategoryCountByType() throws SQLException {
        // Create mixed categories
        Category expense1 = new Category(testFamilyId, "Food", "Expense", false);
        Category expense2 = new Category(testFamilyId, "Shopping", "Expense", false);
        Category expense3 = new Category(testFamilyId, "Transport", "Expense", false);
        Category income1 = new Category(testFamilyId, "Salary", "Income", false);
        
        categoryManager.createCategory(expense1);
        categoryManager.createCategory(expense2);
        categoryManager.createCategory(expense3);
        categoryManager.createCategory(income1);
        
        // Get counts by type
        int expenseCount = categoryManager.getCategoryCountByType(testFamilyId, "Expense");
        int incomeCount = categoryManager.getCategoryCountByType(testFamilyId, "Income");
        
        // Verify counts
        assertEquals(3, expenseCount, "Should have 3 expense categories");
        assertEquals(1, incomeCount, "Should have 1 income category");
    }
    
    // Test getting custom (non-default) category count
    @Test
    public void testGetCustomCategoryCount() throws SQLException {
        // Create default and custom categories
        Category default1 = new Category(testFamilyId, "Default1", "Expense", true);
        Category default2 = new Category(testFamilyId, "Default2", "Expense", true);
        Category custom1 = new Category(testFamilyId, "Custom1", "Expense", false);
        Category custom2 = new Category(testFamilyId, "Custom2", "Income", false);
        
        categoryManager.createCategory(default1);
        categoryManager.createCategory(default2);
        categoryManager.createCategory(custom1);
        categoryManager.createCategory(custom2);
        
        // Get custom count
        int customCount = categoryManager.getCustomCategoryCount(testFamilyId);
        
        // Verify count
        assertEquals(2, customCount, "Should have 2 custom categories");
    }
    
    // Test that inactive categories are not counted in statistics
    @Test
    public void testGetCategoryCount_OnlyActiveCounted() throws SQLException {
        // Create categories and delete one
        Category category1 = new Category(testFamilyId, "Active", "Expense", false);
        Category category2 = new Category(testFamilyId, "Inactive", "Expense", false);
        
        categoryManager.createCategory(category1);
        categoryManager.createCategory(category2);
        categoryManager.deleteCategory(category2.getCategoryId());
        
        // Get count
        int count = categoryManager.getCategoryCount(testFamilyId);
        
        // Verify only active category is counted
        assertEquals(1, count, "Should only count active categories");
    }
}