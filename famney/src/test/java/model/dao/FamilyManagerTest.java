package model.dao;

import model.Family;
import controller.DateUtil;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// JUnit tests for FamilyManager DAO class
// Tests family creation, member count management, family closure, and family code operations
// Uses in-memory SQLite database for test isolation
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FamilyManagerTest {
    
    private Connection testConn;
    private FamilyManager familyManager;
    
    // Set up test database before all tests
    // Creates Families table and initialises FamilyManager
    @BeforeAll
    public void setUpDatabase() throws Exception {
        // Use in-memory database for testing
        testConn = DriverManager.getConnection("jdbc:sqlite::memory:");
        
        // Create Families table for testing
        Statement stmt = testConn.createStatement();
        stmt.execute(
            "CREATE TABLE Families (" +
            "familyId VARCHAR(8) PRIMARY KEY, " +
            "familyCode VARCHAR(15) NOT NULL UNIQUE, " +
            "familyName VARCHAR(100) NOT NULL, " +
            "familyHead VARCHAR(8) NOT NULL, " +
            "memberCount INT NOT NULL DEFAULT 1, " +
            "createdDate TEXT NOT NULL, " +
            "lastModifiedDate TEXT NOT NULL, " +
            "isActive BOOLEAN NOT NULL DEFAULT TRUE)"
        );
        
        // Initialise FamilyManager with test connection
        familyManager = new FamilyManager(testConn);
    }
    
    // Clean up database after each test for isolation
    @AfterEach
    public void cleanDatabase() throws SQLException {
        Statement stmt = testConn.createStatement();
        stmt.execute("DELETE FROM Families");
    }
    
    // Close database connection after all tests complete
    @AfterAll
    public void closeDatabase() throws SQLException {
        if (testConn != null && !testConn.isClosed()) {
            testConn.close();
        }
    }
    
    // Test creating a new family successfully
    // Should generate family ID and family code automatically
    @Test
    public void testCreateFamily_Success() throws SQLException {
        // Create test family
        Family family = new Family();
        family.setFamilyName("Test Family");
        family.setFamilyHead("U0001");
        
        // Create family in database
        Family created = familyManager.createFamily(family);
        
        // Verify creation was successful
        assertNotNull(created, "Family should be created successfully");
        assertNotNull(created.getFamilyId(), "Family ID should be generated");
        assertNotNull(created.getFamilyCode(), "Family code should be generated");
        assertEquals("Test Family", created.getFamilyName());
        assertEquals("U0001", created.getFamilyHead());
    }
    
    // Test that family code follows correct format
    // Should be in format FAMNEY-XXXX where X is alphanumeric
    @Test
    public void testCreateFamily_FamilyCodeFormat() throws SQLException {
        // Create test family
        Family family = new Family();
        family.setFamilyName("Code Format Family");
        family.setFamilyHead("U0002");
        
        // Create family in database
        Family created = familyManager.createFamily(family);
        
        // Verify family code format
        assertNotNull(created.getFamilyCode(), "Family code should not be null");
        assertTrue(created.getFamilyCode().matches("FAMNEY-[A-Z0-9]{4}"),
                  "Family code should match format FAMNEY-XXXX");
    }
    
    // Test that created date is in correct format
    // Should be "YYYY-MM-DD HH:MM:SS" not milliseconds
    @Test
    public void testCreateFamily_DateFormat() throws SQLException {
        // Create test family
        Family family = new Family();
        family.setFamilyName("Date Format Family");
        family.setFamilyHead("U0003");
        
        // Create family in database
        Family created = familyManager.createFamily(family);
        
        // Find family to get dates from database
        Family found = familyManager.findByFamilyId(created.getFamilyId());
        
        // Verify date format is readable (not milliseconds)
        assertNotNull(found.getCreatedDate(), "Created date should not be null");
        assertNotNull(found.getLastModifiedDate(), "Last modified date should not be null");
        
        // Parse dates to verify they're in correct format
        String currentDate = DateUtil.formatDateTime(found.getCreatedDate());
        assertTrue(currentDate.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"),
                  "Date should be in format YYYY-MM-DD HH:MM:SS");
    }
    
    // Test finding family by family code
    // Should return family object when code exists
    @Test
    public void testFindByFamilyCode_Exists() throws SQLException {
        // Create test family
        Family family = new Family();
        family.setFamilyName("Findable Family");
        family.setFamilyHead("U0004");
        
        Family created = familyManager.createFamily(family);
        String familyCode = created.getFamilyCode();
        
        // Find family by code
        Family found = familyManager.findByFamilyCode(familyCode);
        
        // Verify family was found
        assertNotNull(found, "Should find family by code");
        assertEquals(familyCode, found.getFamilyCode());
        assertEquals("Findable Family", found.getFamilyName());
    }
    
    // Test finding family with non-existent code
    // Should return null when family code doesn't exist
    @Test
    public void testFindByFamilyCode_NotExists() throws SQLException {
        // Try to find non-existent family code
        Family found = familyManager.findByFamilyCode("FAMNEY-9999");
        
        // Verify no family was found
        assertNull(found, "Should return null for non-existent family code");
    }
    
    // Test that findByFamilyCode only returns active families
    // Closed families should not be found
    @Test
    public void testFindByFamilyCode_OnlyActiveFamily() throws SQLException {
        // Create family
        Family family = new Family();
        family.setFamilyName("Active Family");
        family.setFamilyHead("U0005");
        
        Family created = familyManager.createFamily(family);
        String familyCode = created.getFamilyCode();
        
        // Close the family
        familyManager.deleteFamily(created.getFamilyId());
        
        // Try to find closed family
        Family found = familyManager.findByFamilyCode(familyCode);
        
        // Verify closed family is not found
        assertNull(found, "Should not find inactive family");
    }
    
    // Test finding family by family ID
    // Used to get family details for logged in user
    @Test
    public void testFindByFamilyId_Exists() throws SQLException {
        // Create test family
        Family family = new Family();
        family.setFamilyName("ID Search Family");
        family.setFamilyHead("U0006");
        
        Family created = familyManager.createFamily(family);
        String familyId = created.getFamilyId();
        
        // Find family by ID
        Family found = familyManager.findByFamilyId(familyId);
        
        // Verify family was found
        assertNotNull(found, "Should find family by ID");
        assertEquals(familyId, found.getFamilyId());
        assertEquals("ID Search Family", found.getFamilyName());
    }
    
    // Test finding family with non-existent ID
    // Should return null when family ID doesn't exist
    @Test
    public void testFindByFamilyId_NotExists() throws SQLException {
        // Try to find non-existent family ID
        Family found = familyManager.findByFamilyId("F9999");
        
        // Verify no family was found
        assertNull(found, "Should return null for non-existent family ID");
    }
    
    // Test that findByFamilyId only returns active families
    // Closed families should not be found
    @Test
    public void testFindByFamilyId_OnlyActiveFamily() throws SQLException {
        // Create family
        Family family = new Family();
        family.setFamilyName("Active Family");
        family.setFamilyHead("U0007");
        
        Family created = familyManager.createFamily(family);
        String familyId = created.getFamilyId();
        
        // Close the family
        familyManager.deleteFamily(familyId);
        
        // Try to find closed family
        Family found = familyManager.findByFamilyId(familyId);
        
        // Verify closed family is not found
        assertNull(found, "Should not find inactive family");
    }
    
    // Test incrementing family member count
    // Called when new member joins the family
    @Test
    public void testIncrementMemberCount() throws SQLException {
        // Create test family
        Family family = new Family();
        family.setFamilyName("Growing Family");
        family.setFamilyHead("U0008");
        
        Family created = familyManager.createFamily(family);
        String familyId = created.getFamilyId();
        
        // Initial member count should be 1
        assertEquals(1, familyManager.getMemberCount(familyId));
        
        // Increment member count
        boolean incremented = familyManager.incrementMemberCount(familyId);
        
        // Verify increment was successful
        assertTrue(incremented, "Member count should increment successfully");
        assertEquals(2, familyManager.getMemberCount(familyId));
        
        // Increment again
        familyManager.incrementMemberCount(familyId);
        assertEquals(3, familyManager.getMemberCount(familyId));
    }
    
    // Test decrementing family member count
    // Called when member leaves or is removed from family
    @Test
    public void testDecrementMemberCount() throws SQLException {
        // Create test family with 3 members
        Family family = new Family();
        family.setFamilyName("Shrinking Family");
        family.setFamilyHead("U0009");
        
        Family created = familyManager.createFamily(family);
        String familyId = created.getFamilyId();
        
        // Add members to reach count of 3
        familyManager.incrementMemberCount(familyId);
        familyManager.incrementMemberCount(familyId);
        
        assertEquals(3, familyManager.getMemberCount(familyId));
        
        // Decrement member count
        boolean decremented = familyManager.decrementMemberCount(familyId);
        
        // Verify decrement was successful
        assertTrue(decremented, "Member count should decrement successfully");
        assertEquals(2, familyManager.getMemberCount(familyId));
    }
    
    // Test that member count cannot go below 1
    // Family must always have at least one member (the head)
    @Test
    public void testDecrementMemberCount_MinimumOne() throws SQLException {
        // Create test family
        Family family = new Family();
        family.setFamilyName("Single Member Family");
        family.setFamilyHead("U0010");
        
        Family created = familyManager.createFamily(family);
        String familyId = created.getFamilyId();
        
        // Initial member count is 1
        assertEquals(1, familyManager.getMemberCount(familyId));
        
        // Try to decrement below 1
        boolean decremented = familyManager.decrementMemberCount(familyId);
        
        // Verify decrement was not allowed
        assertFalse(decremented, "Should not decrement member count below 1");
        assertEquals(1, familyManager.getMemberCount(familyId));
    }
    
    // Test updating family name
    // Only Family Head should be able to change family name
    @Test
    public void testUpdateFamilyName() throws SQLException {
        // Create test family
        Family family = new Family();
        family.setFamilyName("Old Family Name");
        family.setFamilyHead("U0011");
        
        Family created = familyManager.createFamily(family);
        String familyId = created.getFamilyId();
        
        // Update family name
        boolean updated = familyManager.updateFamilyName(familyId, "New Family Name");
        
        // Verify update was successful
        assertTrue(updated, "Family name should be updated successfully");
        
        // Verify name changed in database
        Family found = familyManager.findByFamilyId(familyId);
        assertEquals("New Family Name", found.getFamilyName());
    }
    
    // Test closing family account (soft delete)
    // Sets isActive to false, making family inaccessible
    // This is called when Family Head closes the family
    @Test
    public void testDeleteFamily() throws SQLException {
        // Create test family
        Family family = new Family();
        family.setFamilyName("Closing Family");
        family.setFamilyHead("U0012");
        
        Family created = familyManager.createFamily(family);
        String familyId = created.getFamilyId();
        
        // Verify family exists and is active
        Family beforeDelete = familyManager.findByFamilyId(familyId);
        assertNotNull(beforeDelete, "Family should exist before deletion");
        assertTrue(beforeDelete.isActive(), "Family should be active before deletion");
        
        // Delete (close) the family
        boolean deleted = familyManager.deleteFamily(familyId);
        
        // Verify deletion was successful
        assertTrue(deleted, "Family should be deleted successfully");
        
        // Verify family is no longer found (only active families returned)
        Family afterDelete = familyManager.findByFamilyId(familyId);
        assertNull(afterDelete, "Family should not be found after deletion");
    }
    
    // Test that deleting family sets isActive to false (soft delete)
    // Data remains in database but marked as inactive for analytics
    @Test
    public void testDeleteFamily_SetsInactive() throws SQLException {
        // Create test family
        Family family = new Family();
        family.setFamilyName("Soft Delete Family");
        family.setFamilyHead("U0013");
        
        Family created = familyManager.createFamily(family);
        String familyId = created.getFamilyId();
        
        // Delete the family
        familyManager.deleteFamily(familyId);
        
        // Query database directly (bypassing active filter)
        Statement stmt = testConn.createStatement();
        var rs = stmt.executeQuery("SELECT isActive FROM Families WHERE familyId = '" + familyId + "'");
        
        // Verify isActive is false
        assertTrue(rs.next(), "Family record should still exist in database");
        assertFalse(rs.getBoolean("isActive"), "Family should be marked as inactive");
    }
    
    // Test deleting non-existent family returns false
    @Test
    public void testDeleteFamily_NotExists() throws SQLException {
        // Try to delete non-existent family
        boolean deleted = familyManager.deleteFamily("F9999");
        
        // Verify returns false
        assertFalse(deleted, "Should return false when family doesn't exist");
    }
    
    // Test checking if family code exists in database
    // Used during family creation to ensure unique codes
    @Test
    public void testFamilyCodeExists() throws SQLException {
        // Create test family
        Family family = new Family();
        family.setFamilyName("Exists Check Family");
        family.setFamilyHead("U0014");
        
        Family created = familyManager.createFamily(family);
        String familyCode = created.getFamilyCode();
        
        // Check if family code exists
        boolean exists = familyManager.familyCodeExists(familyCode);
        boolean notExists = familyManager.familyCodeExists("FAMNEY-ZZZZ");
        
        // Verify results
        assertTrue(exists, "Family code should exist in database");
        assertFalse(notExists, "Non-existent code should return false");
    }
    
    // Test getting current member count for a family
    // Used for display and validation purposes
    @Test
    public void testGetMemberCount() throws SQLException {
        // Create test family
        Family family = new Family();
        family.setFamilyName("Count Check Family");
        family.setFamilyHead("U0015");
        
        Family created = familyManager.createFamily(family);
        String familyId = created.getFamilyId();
        
        // Get initial member count
        int initialCount = familyManager.getMemberCount(familyId);
        assertEquals(1, initialCount, "Initial member count should be 1");
        
        // Add members and verify count updates
        familyManager.incrementMemberCount(familyId);
        familyManager.incrementMemberCount(familyId);
        
        int finalCount = familyManager.getMemberCount(familyId);
        assertEquals(3, finalCount, "Member count should be 3 after increments");
    }
    
    // Test that duplicate family codes are prevented
    // Database constraint should reject duplicate family codes
    @Test
    public void testCreateFamily_UniqueFamilyCode() throws SQLException {
        // Create first family
        Family family1 = new Family();
        family1.setFamilyName("First Family");
        family1.setFamilyHead("U0016");
        
        Family created1 = familyManager.createFamily(family1);
        assertNotNull(created1, "First family should be created");
        
        // Create second family (should have different code)
        Family family2 = new Family();
        family2.setFamilyName("Second Family");
        family2.setFamilyHead("U0017");
        
        Family created2 = familyManager.createFamily(family2);
        assertNotNull(created2, "Second family should be created");
        
        // Verify different family codes
        assertNotEquals(created1.getFamilyCode(), created2.getFamilyCode(),
                       "Each family should have unique family code");
    }
    
    // Test getMemberCount returns 0 for non-existent family
    @Test
    public void testGetMemberCount_NonExistentFamily() throws SQLException {
        // Get member count for non-existent family
        int count = familyManager.getMemberCount("F9999");
        
        // Verify returns 0
        assertEquals(0, count, "Should return 0 for non-existent family");
    }
}