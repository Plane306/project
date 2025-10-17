package model.dao;

import model.SavingsGoal;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test suite for SavingsGoalManager (F107).
 * Tests all CRUD operations and goal management functionality.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SavingsGoalManagerTest {

    private Connection connection;
    private SavingsGoalManager savingsGoalManager;
    private String testFamilyId = "FAM12345";
    private String testUserId = "U001";

    @BeforeAll
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        savingsGoalManager = new SavingsGoalManager(connection);
        createTestSchema();
        insertTestData();
    }

    @AfterEach
    void cleanUpGoals() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("DELETE FROM SavingsGoals");
        stmt.close();
    }

    @AfterAll
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private void createTestSchema() throws SQLException {
        Statement stmt = connection.createStatement();

        // Create Families table
        stmt.execute("CREATE TABLE Families (" +
                "familyId VARCHAR(8) PRIMARY KEY, " +
                "familyCode VARCHAR(15) NOT NULL UNIQUE, " +
                "familyName VARCHAR(100) NOT NULL, " +
                "familyHead VARCHAR(8) NOT NULL, " +
                "memberCount INT NOT NULL DEFAULT 1, " +
                "createdDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "lastModifiedDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "isActive BOOLEAN NOT NULL DEFAULT TRUE)");

        // Create Users table
        stmt.execute("CREATE TABLE Users (" +
                "userId VARCHAR(8) PRIMARY KEY, " +
                "email VARCHAR(100) NOT NULL UNIQUE, " +
                "password VARCHAR(255) NOT NULL, " +
                "fullName VARCHAR(100) NOT NULL, " +
                "role VARCHAR(20) NOT NULL, " +
                "familyId VARCHAR(8) NOT NULL, " +
                "joinDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "createdDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "lastModifiedDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "isActive BOOLEAN NOT NULL DEFAULT TRUE, " +
                "FOREIGN KEY (familyId) REFERENCES Families(familyId))");

        // Create SavingsGoals table
        stmt.execute("CREATE TABLE SavingsGoals (" +
                "goalId VARCHAR(8) PRIMARY KEY, " +
                "familyId VARCHAR(8) NOT NULL, " +
                "goalName VARCHAR(100) NOT NULL, " +
                "description TEXT, " +
                "targetAmount DECIMAL(12,2) NOT NULL CHECK (targetAmount > 0), " +
                "currentAmount DECIMAL(12,2) NOT NULL DEFAULT 0 CHECK (currentAmount >= 0), " +
                "targetDate DATE, " +
                "createdDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "lastModifiedDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "isActive BOOLEAN NOT NULL DEFAULT TRUE, " +
                "isCompleted BOOLEAN NOT NULL DEFAULT FALSE, " +
                "createdBy VARCHAR(8), " +
                "FOREIGN KEY (familyId) REFERENCES Families(familyId) ON DELETE CASCADE, " +
                "FOREIGN KEY (createdBy) REFERENCES Users(userId) ON DELETE SET NULL)");

        stmt.close();
    }

    private void insertTestData() throws SQLException {
        Statement stmt = connection.createStatement();

        stmt.execute("INSERT INTO Families (familyId, familyCode, familyName, familyHead) " +
                "VALUES ('" + testFamilyId + "', 'FAM-TEST', 'Test Family', '" + testUserId + "')");

        stmt.execute("INSERT INTO Users (userId, email, password, fullName, role, familyId) " +
                "VALUES ('" + testUserId + "', 'test@test.com', 'hash123', 'Test User', 'Family Head', '" + testFamilyId
                + "')");

        stmt.close();
    }

    private Date getFutureDate(int daysFromNow) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, daysFromNow);
        return cal.getTime();
    }

    // Test 1: Create savings goal successfully
    @Test
    void testCreateSavingsGoal_Success() throws SQLException {
        SavingsGoal goal = new SavingsGoal(testFamilyId, "Vacation Fund", 5000.0, getFutureDate(180), testUserId);
        goal.setDescription("Family trip to Hawaii");

        boolean result = savingsGoalManager.createSavingsGoal(goal);

        assertTrue(result);
        assertNotNull(goal.getGoalId());
        assertTrue(goal.getGoalId().startsWith("G"));
    }

    // Test 2: Create goal with null description
    @Test
    void testCreateSavingsGoal_NullDescription() throws SQLException {
        SavingsGoal goal = new SavingsGoal(testFamilyId, "Emergency Fund", 10000.0, getFutureDate(365), testUserId);

        boolean result = savingsGoalManager.createSavingsGoal(goal);

        assertTrue(result);
        assertNotNull(goal.getGoalId());
    }

    // Test 3: Create goal with null target date
    @Test
    void testCreateSavingsGoal_NullTargetDate() throws SQLException {
        SavingsGoal goal = new SavingsGoal(testFamilyId, "General Savings", 3000.0, null, testUserId);

        boolean result = savingsGoalManager.createSavingsGoal(goal);

        assertTrue(result);
    }

    // Test 4: Get savings goal by ID
    @Test
    void testGetSavingsGoalById_Success() throws SQLException {
        SavingsGoal goal = new SavingsGoal(testFamilyId, "Car Fund", 15000.0, getFutureDate(730), testUserId);
        savingsGoalManager.createSavingsGoal(goal);

        SavingsGoal retrieved = savingsGoalManager.getSavingsGoalById(goal.getGoalId());

        assertNotNull(retrieved);
        assertEquals(goal.getGoalId(), retrieved.getGoalId());
        assertEquals("Car Fund", retrieved.getGoalName());
        assertEquals(15000.0, retrieved.getTargetAmount());
    }

    // Test 5: Get non-existent goal returns null
    @Test
    void testGetSavingsGoalById_NotFound() throws SQLException {
        SavingsGoal result = savingsGoalManager.getSavingsGoalById("NONEXIST");

        assertNull(result);
    }

    // Test 6: Get all family savings goals
    @Test
    void testGetFamilySavingsGoals_MultipleGoals() throws SQLException {
        SavingsGoal goal1 = new SavingsGoal(testFamilyId, "Goal 1", 1000.0, getFutureDate(90), testUserId);
        SavingsGoal goal2 = new SavingsGoal(testFamilyId, "Goal 2", 2000.0, getFutureDate(180), testUserId);
        SavingsGoal goal3 = new SavingsGoal(testFamilyId, "Goal 3", 3000.0, getFutureDate(270), testUserId);

        savingsGoalManager.createSavingsGoal(goal1);
        savingsGoalManager.createSavingsGoal(goal2);
        savingsGoalManager.createSavingsGoal(goal3);

        List<SavingsGoal> goals = savingsGoalManager.getFamilySavingsGoals(testFamilyId);

        assertEquals(3, goals.size());
    }

    // Test 7: Get family goals returns empty list for no goals
    @Test
    void testGetFamilySavingsGoals_Empty() throws SQLException {
        List<SavingsGoal> goals = savingsGoalManager.getFamilySavingsGoals(testFamilyId);

        assertTrue(goals.isEmpty());
    }

    // Test 8: Add contribution to savings goal
    @Test
    void testAddToSavingsGoal_Success() throws SQLException {
        SavingsGoal goal = new SavingsGoal(testFamilyId, "Wedding Fund", 20000.0, getFutureDate(540), testUserId);
        savingsGoalManager.createSavingsGoal(goal);

        boolean result = savingsGoalManager.addToSavingsGoal(goal.getGoalId(), 5000.0);

        assertTrue(result);

        SavingsGoal updated = savingsGoalManager.getSavingsGoalById(goal.getGoalId());
        assertEquals(5000.0, updated.getCurrentAmount());
        assertFalse(updated.isCompleted());
    }

    // Test 9: Add contribution that completes goal
    @Test
    void testAddToSavingsGoal_CompletesGoal() throws SQLException {
        SavingsGoal goal = new SavingsGoal(testFamilyId, "Small Goal", 1000.0, getFutureDate(30), testUserId);
        savingsGoalManager.createSavingsGoal(goal);

        savingsGoalManager.addToSavingsGoal(goal.getGoalId(), 1000.0);

        SavingsGoal updated = savingsGoalManager.getSavingsGoalById(goal.getGoalId());
        assertEquals(1000.0, updated.getCurrentAmount());
        assertTrue(updated.isCompleted());
    }

    // Test 10: Add contribution that exceeds target
    @Test
    void testAddToSavingsGoal_ExceedsTarget() throws SQLException {
        SavingsGoal goal = new SavingsGoal(testFamilyId, "Book Fund", 500.0, getFutureDate(60), testUserId);
        savingsGoalManager.createSavingsGoal(goal);

        savingsGoalManager.addToSavingsGoal(goal.getGoalId(), 600.0);

        SavingsGoal updated = savingsGoalManager.getSavingsGoalById(goal.getGoalId());
        assertEquals(600.0, updated.getCurrentAmount());
        assertTrue(updated.isCompleted());
    }

    // Test 11: Cannot add negative contribution
    @Test
    void testAddToSavingsGoal_NegativeAmount() {
        SavingsGoal goal = new SavingsGoal(testFamilyId, "Test Goal", 1000.0, getFutureDate(30), testUserId);
        assertDoesNotThrow(() -> savingsGoalManager.createSavingsGoal(goal));

        boolean result = savingsGoalManager.addToSavingsGoal(goal.getGoalId(), -100.0);

        assertFalse(result);
    }

    // Test 12: Cannot add to completed goal
    @Test
    void testAddToSavingsGoal_AlreadyCompleted() throws SQLException {
        SavingsGoal goal = new SavingsGoal(testFamilyId, "Completed Goal", 500.0, getFutureDate(30), testUserId);
        savingsGoalManager.createSavingsGoal(goal);
        savingsGoalManager.addToSavingsGoal(goal.getGoalId(), 500.0);

        boolean result = savingsGoalManager.addToSavingsGoal(goal.getGoalId(), 100.0);

        assertFalse(result);
    }

    // Test 13: Update savings goal details
    @Test
    void testUpdateSavingsGoal_Success() throws SQLException {
        SavingsGoal goal = new SavingsGoal(testFamilyId, "Original Name", 5000.0, getFutureDate(180), testUserId);
        savingsGoalManager.createSavingsGoal(goal);

        goal.setGoalName("Updated Name");
        goal.setDescription("Updated description");
        goal.setTargetAmount(7000.0);

        boolean result = savingsGoalManager.updateSavingsGoal(goal);

        assertTrue(result);

        SavingsGoal updated = savingsGoalManager.getSavingsGoalById(goal.getGoalId());
        assertEquals("Updated Name", updated.getGoalName());
        assertEquals("Updated description", updated.getDescription());
        assertEquals(7000.0, updated.getTargetAmount());
    }

    // Test 14: Delete savings goal (soft delete)
    @Test
    void testDeleteSavingsGoal_Success() throws SQLException {
        SavingsGoal goal = new SavingsGoal(testFamilyId, "To Delete", 1000.0, getFutureDate(30), testUserId);
        savingsGoalManager.createSavingsGoal(goal);

        boolean result = savingsGoalManager.deleteSavingsGoal(goal.getGoalId());

        assertTrue(result);

        SavingsGoal deleted = savingsGoalManager.getSavingsGoalById(goal.getGoalId());
        assertNull(deleted);
    }

    // Test 15: Get active goals only
    @Test
    void testGetActiveGoals() throws SQLException {
        SavingsGoal goal1 = new SavingsGoal(testFamilyId, "Active 1", 1000.0, getFutureDate(90), testUserId);
        SavingsGoal goal2 = new SavingsGoal(testFamilyId, "Active 2", 2000.0, getFutureDate(180), testUserId);
        SavingsGoal goal3 = new SavingsGoal(testFamilyId, "Completed", 500.0, getFutureDate(30), testUserId);

        savingsGoalManager.createSavingsGoal(goal1);
        savingsGoalManager.createSavingsGoal(goal2);
        savingsGoalManager.createSavingsGoal(goal3);
        savingsGoalManager.addToSavingsGoal(goal3.getGoalId(), 500.0);

        List<SavingsGoal> activeGoals = savingsGoalManager.getActiveGoals(testFamilyId);

        assertEquals(2, activeGoals.size());
    }

    // Test 16: Get completed goals
    @Test
    void testGetCompletedGoals() throws SQLException {
        SavingsGoal goal1 = new SavingsGoal(testFamilyId, "Completed 1", 500.0, getFutureDate(30), testUserId);
        SavingsGoal goal2 = new SavingsGoal(testFamilyId, "Completed 2", 1000.0, getFutureDate(60), testUserId);

        savingsGoalManager.createSavingsGoal(goal1);
        savingsGoalManager.createSavingsGoal(goal2);
        savingsGoalManager.addToSavingsGoal(goal1.getGoalId(), 500.0);
        savingsGoalManager.addToSavingsGoal(goal2.getGoalId(), 1000.0);

        List<SavingsGoal> completedGoals = savingsGoalManager.getCompletedGoals(testFamilyId);

        assertEquals(2, completedGoals.size());
        assertTrue(completedGoals.get(0).isCompleted());
        assertTrue(completedGoals.get(1).isCompleted());
    }

    // Test 17: Get total saved across all goals
    @Test
    void testGetTotalSaved() throws SQLException {
        SavingsGoal goal1 = new SavingsGoal(testFamilyId, "Goal 1", 5000.0, getFutureDate(90), testUserId);
        SavingsGoal goal2 = new SavingsGoal(testFamilyId, "Goal 2", 3000.0, getFutureDate(180), testUserId);

        savingsGoalManager.createSavingsGoal(goal1);
        savingsGoalManager.createSavingsGoal(goal2);
        savingsGoalManager.addToSavingsGoal(goal1.getGoalId(), 2000.0);
        savingsGoalManager.addToSavingsGoal(goal2.getGoalId(), 1500.0);

        double total = savingsGoalManager.getTotalSaved(testFamilyId);

        assertEquals(3500.0, total, 0.01);
    }

    // Test 18: Get active goal count
    @Test
    void testGetActiveGoalCount() throws SQLException {
        SavingsGoal goal1 = new SavingsGoal(testFamilyId, "Active 1", 1000.0, getFutureDate(90), testUserId);
        SavingsGoal goal2 = new SavingsGoal(testFamilyId, "Active 2", 2000.0, getFutureDate(180), testUserId);
        SavingsGoal goal3 = new SavingsGoal(testFamilyId, "Completed", 500.0, getFutureDate(30), testUserId);

        savingsGoalManager.createSavingsGoal(goal1);
        savingsGoalManager.createSavingsGoal(goal2);
        savingsGoalManager.createSavingsGoal(goal3);
        savingsGoalManager.addToSavingsGoal(goal3.getGoalId(), 500.0);

        int count = savingsGoalManager.getActiveGoalCount(testFamilyId);

        assertEquals(2, count);
    }

    // Test 19: Multiple contributions to same goal
    @Test
    void testAddToSavingsGoal_MultipleContributions() throws SQLException {
        SavingsGoal goal = new SavingsGoal(testFamilyId, "Building Fund", 10000.0, getFutureDate(365), testUserId);
        savingsGoalManager.createSavingsGoal(goal);

        savingsGoalManager.addToSavingsGoal(goal.getGoalId(), 2000.0);
        savingsGoalManager.addToSavingsGoal(goal.getGoalId(), 3000.0);
        savingsGoalManager.addToSavingsGoal(goal.getGoalId(), 1500.0);

        SavingsGoal updated = savingsGoalManager.getSavingsGoalById(goal.getGoalId());
        assertEquals(6500.0, updated.getCurrentAmount());
        assertFalse(updated.isCompleted());
    }

    // Test 20: Transaction rollback on failure
    @Test
    void testAddToSavingsGoal_NonExistentGoal() {
        boolean result = savingsGoalManager.addToSavingsGoal("FAKE123", 100.0);

        assertFalse(result);
    }
}