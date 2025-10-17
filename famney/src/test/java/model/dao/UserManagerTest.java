package model.dao;

import model.User;
import controller.PasswordUtil;
import controller.DateUtil;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

// JUnit tests for UserManager DAO class
// Tests user authentication, CRUD operations, role management, and email reuse logic
// Uses in-memory SQLite database for test isolation
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserManagerTest {
    
    private Connection testConn;
    private UserManager userManager;
    private String testFamilyId = "F0001";
    
    // Set up test database before all tests
    // Creates tables and initialises UserManager with test connection
    @BeforeAll
    public void setUpDatabase() throws Exception {
        // Use in-memory database for testing (faster and isolated)
        testConn = DriverManager.getConnection("jdbc:sqlite::memory:");
        
        // Create Users table for testing (email is NOT UNIQUE anymore)
        Statement stmt = testConn.createStatement();
        stmt.execute(
            "CREATE TABLE Users (" +
            "userId VARCHAR(8) PRIMARY KEY, " +
            "email VARCHAR(100) NOT NULL, " +
            "password VARCHAR(255) NOT NULL, " +
            "fullName VARCHAR(100) NOT NULL, " +
            "role VARCHAR(20) CHECK (role IN ('Family Head', 'Adult', 'Teen', 'Kid') OR role IS NULL), " +
            "familyId VARCHAR(8) NOT NULL, " +
            "joinDate TEXT NOT NULL, " +
            "createdDate TEXT NOT NULL, " +
            "lastModifiedDate TEXT NOT NULL, " +
            "isActive BOOLEAN NOT NULL DEFAULT TRUE)"
        );
        
        // Initialise UserManager with test connection
        userManager = new UserManager(testConn);
    }
    
    // Clean up database after each test to ensure test isolation
    @AfterEach
    public void cleanDatabase() throws SQLException {
        Statement stmt = testConn.createStatement();
        stmt.execute("DELETE FROM Users");
    }
    
    // Close database connection after all tests complete
    @AfterAll
    public void closeDatabase() throws SQLException {
        if (testConn != null && !testConn.isClosed()) {
            testConn.close();
        }
    }
    
    // Test creating a new user successfully
    // Verifies that user can be created and stored in database
    @Test
    public void testCreateUser_Success() throws SQLException {
        // Create test user
        User user = new User();
        user.setEmail("john@test.com");
        user.setPassword("password123");
        user.setFullName("John Smith");
        user.setRole("Family Head");
        user.setFamilyId(testFamilyId);
        
        // Create user in database
        boolean result = userManager.createUser(user);
        
        // Verify creation was successful
        assertTrue(result, "User should be created successfully");
        assertNotNull(user.getUserId(), "User ID should be generated");
    }
    
    // Test that created date is in correct format
    // Should be "YYYY-MM-DD HH:MM:SS" not milliseconds
    @Test
    public void testCreateUser_DateFormat() throws SQLException {
        // Create test user
        User user = new User();
        user.setEmail("datetest@test.com");
        user.setPassword("password123");
        user.setFullName("Date Test User");
        user.setRole("Adult");
        user.setFamilyId(testFamilyId);
        
        // Create user in database
        userManager.createUser(user);
        
        // Find user to get dates from database
        User found = userManager.findByEmail("datetest@test.com");
        
        // Verify date format is readable (not milliseconds)
        assertNotNull(found.getJoinDate(), "Join date should not be null");
        assertNotNull(found.getCreatedDate(), "Created date should not be null");
        
        // Parse dates to verify they're in correct format
        String joinDate = DateUtil.formatDateTime(found.getJoinDate());
        assertTrue(joinDate.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"),
                  "Date should be in format YYYY-MM-DD HH:MM:SS");
    }
    
    // Test creating user with NULL role (pending approval scenario)
    // Used when member joins family and waits for Family Head to assign role
    @Test
    public void testCreateUser_WithNullRole() throws SQLException {
        // Create test user with NULL role
        User user = new User();
        user.setEmail("pending@test.com");
        user.setPassword("password123");
        user.setFullName("Pending User");
        user.setRole(null); // Pending role assignment
        user.setFamilyId(testFamilyId);
        
        // Create user in database
        boolean result = userManager.createUser(user);
        
        // Verify creation was successful
        assertTrue(result, "User with NULL role should be created successfully");
        
        // Verify user can be retrieved with NULL role
        User found = userManager.findByEmail("pending@test.com");
        assertNotNull(found, "Should find user with NULL role");
        assertNull(found.getRole(), "User role should be NULL");
    }
    
    // Test NEW email uniqueness logic
    // Email can be reused if previous user is inactive (soft deleted)
    // Only active users need unique emails
    @Test
    public void testCreateUser_EmailReuseAfterInactive() throws SQLException {
        // Create first user
        User user1 = new User();
        user1.setEmail("reuse@test.com");
        user1.setPassword("password123");
        user1.setFullName("First User");
        user1.setRole("Adult");
        user1.setFamilyId(testFamilyId);
        
        userManager.createUser(user1);
        
        // Deactivate first user (soft delete)
        userManager.deleteUser(user1.getUserId());
        
        // Email should now be available for reuse
        assertFalse(userManager.emailExists("reuse@test.com"), 
                   "Email should be available after user becomes inactive");
        
        // Create second user with same email (different family)
        User user2 = new User();
        user2.setEmail("reuse@test.com");
        user2.setPassword("different123");
        user2.setFullName("Second User");
        user2.setRole("Family Head");
        user2.setFamilyId("F0002");
        
        boolean result = userManager.createUser(user2);
        
        // Verify second user creation succeeded
        assertTrue(result, "Should be able to reuse email after first user is inactive");
        
        // Verify only active user is found
        User found = userManager.findByEmail("reuse@test.com");
        assertNotNull(found, "Should find active user");
        assertEquals("Second User", found.getFullName(), "Should find the new active user");
    }
    
    // Test that creating user with duplicate ACTIVE email fails
    // Two active users cannot have the same email
    @Test
    public void testEmailExists_BlocksDuplicateActiveEmail() throws SQLException {
        // Create first user
        User user1 = new User();
        user1.setEmail("active@test.com");
        user1.setPassword("password123");
        user1.setFullName("First User");
        user1.setRole("Adult");
        user1.setFamilyId(testFamilyId);
        
        userManager.createUser(user1);
        
        // Email should exist for active user
        assertTrue(userManager.emailExists("active@test.com"), 
                  "Email should exist for active user");
        
        // Try to create second ACTIVE user with same email
        User user2 = new User();
        user2.setEmail("active@test.com");
        user2.setPassword("different123");
        user2.setFullName("Second User");
        user2.setRole("Teen");
        user2.setFamilyId("F0002");
        
        // This should be caught by emailExists() check in servlet
        // But let's verify the check works correctly
        boolean emailTaken = userManager.emailExists("active@test.com");
        assertTrue(emailTaken, "Should detect that email is already in use by active user");
    }
    
    // Test user authentication with correct credentials
    // Should return user object when email and password match
    @Test
    public void testAuthenticate_ValidCredentials() throws SQLException {
        // Create test user
        User user = new User();
        user.setEmail("auth@test.com");
        user.setPassword("testpass123");
        user.setFullName("Auth User");
        user.setRole("Family Head");
        user.setFamilyId(testFamilyId);
        
        userManager.createUser(user);
        
        // Try to authenticate with correct credentials
        User authenticated = userManager.authenticate("auth@test.com", "testpass123");
        
        // Verify authentication successful
        assertNotNull(authenticated, "Should authenticate with valid credentials");
        assertEquals("auth@test.com", authenticated.getEmail());
        assertEquals("Auth User", authenticated.getFullName());
    }
    
    // Test authentication with wrong password
    // Should return null when password doesn't match
    @Test
    public void testAuthenticate_WrongPassword() throws SQLException {
        // Create test user
        User user = new User();
        user.setEmail("wrong@test.com");
        user.setPassword("correctpass");
        user.setFullName("Test User");
        user.setRole("Adult");
        user.setFamilyId(testFamilyId);
        
        userManager.createUser(user);
        
        // Try to authenticate with wrong password
        User authenticated = userManager.authenticate("wrong@test.com", "wrongpass");
        
        // Verify authentication failed
        assertNull(authenticated, "Should not authenticate with wrong password");
    }
    
    // Test authentication with non-existent email
    // Should return null when email doesn't exist in database
    @Test
    public void testAuthenticate_NonExistentEmail() throws SQLException {
        // Try to authenticate with email that doesn't exist
        User authenticated = userManager.authenticate("notexist@test.com", "anypass");
        
        // Verify authentication failed
        assertNull(authenticated, "Should not authenticate non-existent user");
    }
    
    // Test authentication can succeed even with NULL role
    // Authentication only checks credentials, not role status
    // Login blocking for NULL role is handled in servlet layer
    @Test
    public void testAuthenticate_PendingRole() throws SQLException {
        // Create user with NULL role
        User user = new User();
        user.setEmail("pending@test.com");
        user.setPassword("password123");
        user.setFullName("Pending User");
        user.setRole(null);
        user.setFamilyId(testFamilyId);
        
        userManager.createUser(user);
        
        // Authenticate should succeed (servlet will block login later)
        User authenticated = userManager.authenticate("pending@test.com", "password123");
        
        // Verify authentication succeeds
        assertNotNull(authenticated, "Authentication should succeed even with NULL role");
        assertNull(authenticated.getRole(), "Role should still be NULL");
    }
    
    // Test that inactive users cannot authenticate
    // Only active users can login
    @Test
    public void testAuthenticate_InactiveUserCannotLogin() throws SQLException {
        // Create and then deactivate user
        User user = new User();
        user.setEmail("inactive@test.com");
        user.setPassword("password123");
        user.setFullName("Inactive User");
        user.setRole("Adult");
        user.setFamilyId(testFamilyId);
        
        userManager.createUser(user);
        userManager.deleteUser(user.getUserId());
        
        // Try to authenticate inactive user
        User authenticated = userManager.authenticate("inactive@test.com", "password123");
        
        // Verify authentication fails for inactive user
        assertNull(authenticated, "Inactive user should not be able to authenticate");
    }
    
    // Test finding user by email address
    // Should return user object when email exists in database
    @Test
    public void testFindByEmail_Exists() throws SQLException {
        // Create test user
        User user = new User();
        user.setEmail("find@test.com");
        user.setPassword("password123");
        user.setFullName("Findable User");
        user.setRole("Teen");
        user.setFamilyId(testFamilyId);
        
        userManager.createUser(user);
        
        // Find user by email
        User found = userManager.findByEmail("find@test.com");
        
        // Verify user was found
        assertNotNull(found, "Should find user by email");
        assertEquals("find@test.com", found.getEmail());
        assertEquals("Findable User", found.getFullName());
    }
    
    // Test finding user with email that doesn't exist
    // Should return null when email not found in database
    @Test
    public void testFindByEmail_NotExists() throws SQLException {
        // Try to find non-existent email
        User found = userManager.findByEmail("notfound@test.com");
        
        // Verify no user was found
        assertNull(found, "Should return null for non-existent email");
    }
    
    // Test that findByEmail only returns active users
    // Inactive users should not be found
    @Test
    public void testFindByEmail_OnlyReturnsActiveUsers() throws SQLException {
        // Create and deactivate user
        User user = new User();
        user.setEmail("inactive@test.com");
        user.setPassword("password123");
        user.setFullName("Inactive User");
        user.setRole("Adult");
        user.setFamilyId(testFamilyId);
        
        userManager.createUser(user);
        userManager.deleteUser(user.getUserId());
        
        // Try to find inactive user
        User found = userManager.findByEmail("inactive@test.com");
        
        // Verify inactive user is not found
        assertNull(found, "Should not find inactive user");
    }
    
    // Test checking if email exists in database (only active users)
    // Used during registration to prevent duplicate emails
    // Should only check active users, not inactive ones
    @Test
    public void testEmailExists_OnlyActiveUsers() throws SQLException {
        // Create active user
        User activeUser = new User();
        activeUser.setEmail("active@test.com");
        activeUser.setPassword("password123");
        activeUser.setFullName("Active User");
        activeUser.setRole("Adult");
        activeUser.setFamilyId(testFamilyId);
        
        userManager.createUser(activeUser);
        
        // Create inactive user
        User inactiveUser = new User();
        inactiveUser.setEmail("inactive@test.com");
        inactiveUser.setPassword("password123");
        inactiveUser.setFullName("Inactive User");
        inactiveUser.setRole("Adult");
        inactiveUser.setFamilyId(testFamilyId);
        
        userManager.createUser(inactiveUser);
        userManager.deleteUser(inactiveUser.getUserId()); // Make inactive
        
        // Check email exists
        boolean activeExists = userManager.emailExists("active@test.com");
        boolean inactiveExists = userManager.emailExists("inactive@test.com");
        boolean notExists = userManager.emailExists("notexists@test.com");
        
        // Verify results
        assertTrue(activeExists, "Active user email should exist");
        assertFalse(inactiveExists, "Inactive user email should not exist");
        assertFalse(notExists, "Non-existent email should return false");
    }
    
    // Test getting pending users (users with NULL role)
    // Used by Family Head to see who needs role assignment
    @Test
    public void testGetPendingUsers() throws SQLException {
        // Create active user with role
        User activeUser = new User();
        activeUser.setEmail("active@test.com");
        activeUser.setPassword("password123");
        activeUser.setFullName("Active User");
        activeUser.setRole("Adult");
        activeUser.setFamilyId(testFamilyId);
        userManager.createUser(activeUser);
        
        // Create pending users (NULL role)
        User pending1 = new User();
        pending1.setEmail("pending1@test.com");
        pending1.setPassword("password123");
        pending1.setFullName("Pending User 1");
        pending1.setRole(null);
        pending1.setFamilyId(testFamilyId);
        userManager.createUser(pending1);
        
        User pending2 = new User();
        pending2.setEmail("pending2@test.com");
        pending2.setPassword("password123");
        pending2.setFullName("Pending User 2");
        pending2.setRole(null);
        pending2.setFamilyId(testFamilyId);
        userManager.createUser(pending2);
        
        // Get pending users
        List<User> pendingUsers = userManager.getPendingUsers(testFamilyId);
        
        // Verify results
        assertNotNull(pendingUsers, "Should return list of pending users");
        assertEquals(2, pendingUsers.size(), "Should have 2 pending users");
        
        // Verify all returned users have NULL role
        for (User user : pendingUsers) {
            assertNull(user.getRole(), "All pending users should have NULL role");
        }
    }
    
    // Test getting pending users returns empty list when none exist
    @Test
    public void testGetPendingUsers_NoPending() throws SQLException {
        // Create only active user with role
        User activeUser = new User();
        activeUser.setEmail("active@test.com");
        activeUser.setPassword("password123");
        activeUser.setFullName("Active User");
        activeUser.setRole("Adult");
        activeUser.setFamilyId(testFamilyId);
        userManager.createUser(activeUser);
        
        // Get pending users
        List<User> pendingUsers = userManager.getPendingUsers(testFamilyId);
        
        // Verify empty list
        assertNotNull(pendingUsers, "Should return list");
        assertTrue(pendingUsers.isEmpty(), "Should be empty when no pending users");
    }
    
    // Test updating user profile information
    // Should update email and full name successfully
    @Test
    public void testUpdateUser() throws SQLException {
        // Create test user
        User user = new User();
        user.setEmail("old@test.com");
        user.setPassword("password123");
        user.setFullName("Old Name");
        user.setRole("Adult");
        user.setFamilyId(testFamilyId);
        
        userManager.createUser(user);
        
        // Update user details
        user.setEmail("new@test.com");
        user.setFullName("New Name");
        
        boolean updated = userManager.updateUser(user);
        
        // Verify update was successful
        assertTrue(updated, "User should be updated successfully");
        
        // Verify changes persisted in database
        User found = userManager.findByEmail("new@test.com");
        assertNotNull(found, "Should find user with new email");
        assertEquals("New Name", found.getFullName());
    }
    
    // Test updating user password
    // Password should be hashed before storing in database
    @Test
    public void testUpdatePassword() throws SQLException {
        // Create test user
        User user = new User();
        user.setEmail("passupdate@test.com");
        user.setPassword("oldpassword");
        user.setFullName("Password User");
        user.setRole("Adult");
        user.setFamilyId(testFamilyId);
        
        userManager.createUser(user);
        String userId = user.getUserId();
        
        // Update password
        boolean updated = userManager.updatePassword(userId, "newpassword");
        
        // Verify password update was successful
        assertTrue(updated, "Password should be updated successfully");
        
        // Verify can authenticate with new password
        User authenticated = userManager.authenticate("passupdate@test.com", "newpassword");
        assertNotNull(authenticated, "Should authenticate with new password");
        
        // Verify cannot authenticate with old password
        User notAuthenticated = userManager.authenticate("passupdate@test.com", "oldpassword");
        assertNull(notAuthenticated, "Should not authenticate with old password");
    }
    
    // Test updating user role (Family Head functionality)
    // Only Family Head should be able to change member roles
    @Test
    public void testUpdateUserRole() throws SQLException {
        // Create test user
        User user = new User();
        user.setEmail("role@test.com");
        user.setPassword("password123");
        user.setFullName("Role User");
        user.setRole("Teen");
        user.setFamilyId(testFamilyId);
        
        userManager.createUser(user);
        String userId = user.getUserId();
        
        // Update role from Teen to Adult
        boolean updated = userManager.updateUserRole(userId, "Adult");
        
        // Verify role update was successful
        assertTrue(updated, "Role should be updated successfully");
        
        // Verify role changed in database
        User found = userManager.findByEmail("role@test.com");
        assertEquals("Adult", found.getRole());
    }
    
    // Test assigning role to pending user (NULL to actual role)
    // This is the primary use case for updateUserRole after join family
    @Test
    public void testUpdateUserRole_AssignPendingRole() throws SQLException {
        // Create user with NULL role
        User user = new User();
        user.setEmail("assign@test.com");
        user.setPassword("password123");
        user.setFullName("Assign User");
        user.setRole(null);
        user.setFamilyId(testFamilyId);
        
        userManager.createUser(user);
        String userId = user.getUserId();
        
        // Assign role
        boolean updated = userManager.updateUserRole(userId, "Adult");
        
        // Verify role assignment was successful
        assertTrue(updated, "Role should be assigned successfully");
        
        // Verify role is no longer NULL
        User found = userManager.findByEmail("assign@test.com");
        assertNotNull(found.getRole(), "Role should no longer be NULL");
        assertEquals("Adult", found.getRole());
    }
    
    // Test soft deleting user (setting isActive to false)
    // User data should remain in database but marked as inactive for analytics
    @Test
    public void testDeleteUser() throws SQLException {
        // Create test user
        User user = new User();
        user.setEmail("delete@test.com");
        user.setPassword("password123");
        user.setFullName("Delete User");
        user.setRole("Kid");
        user.setFamilyId(testFamilyId);
        
        userManager.createUser(user);
        String userId = user.getUserId();
        
        // Soft delete user
        boolean deleted = userManager.deleteUser(userId);
        
        // Verify deletion was successful
        assertTrue(deleted, "User should be soft deleted successfully");
        
        // Verify user cannot be found (findByEmail only returns active users)
        User found = userManager.findByEmail("delete@test.com");
        assertNull(found, "Deleted user should not be found by active queries");
        
        // Verify user data is preserved in database
        Statement stmt = testConn.createStatement();
        var rs = stmt.executeQuery("SELECT isActive FROM Users WHERE userId = '" + userId + "'");
        assertTrue(rs.next(), "User record should still exist in database");
        assertFalse(rs.getBoolean("isActive"), "User should be marked as inactive");
    }
    
    // Test deactivating all users in a family
    // Used when Family Head closes the entire family
    @Test
    public void testDeactivateAllFamilyUsers() throws SQLException {
        // Create multiple users in same family
        User user1 = new User();
        user1.setEmail("user1@test.com");
        user1.setPassword("password123");
        user1.setFullName("User 1");
        user1.setRole("Family Head");
        user1.setFamilyId(testFamilyId);
        userManager.createUser(user1);
        
        User user2 = new User();
        user2.setEmail("user2@test.com");
        user2.setPassword("password123");
        user2.setFullName("User 2");
        user2.setRole("Adult");
        user2.setFamilyId(testFamilyId);
        userManager.createUser(user2);
        
        User user3 = new User();
        user3.setEmail("user3@test.com");
        user3.setPassword("password123");
        user3.setFullName("User 3");
        user3.setRole("Teen");
        user3.setFamilyId(testFamilyId);
        userManager.createUser(user3);
        
        // Deactivate all users
        boolean deactivated = userManager.deactivateAllFamilyUsers(testFamilyId);
        
        // Verify deactivation was successful
        assertTrue(deactivated, "All users should be deactivated");
        
        // Verify all users are inactive
        assertNull(userManager.findByEmail("user1@test.com"), "User 1 should be inactive");
        assertNull(userManager.findByEmail("user2@test.com"), "User 2 should be inactive");
        assertNull(userManager.findByEmail("user3@test.com"), "User 3 should be inactive");
    }
    
    // Test password hashing during user creation
    // Password should be hashed, not stored as plain text
    @Test
    public void testPasswordHashingOnCreation() throws SQLException {
        // Create user with plain text password
        User user = new User();
        user.setEmail("hash@test.com");
        user.setPassword("plainpassword");
        user.setFullName("Hash User");
        user.setRole("Adult");
        user.setFamilyId(testFamilyId);
        
        userManager.createUser(user);
        
        // Retrieve user from database
        User found = userManager.findByEmail("hash@test.com");
        
        // Verify password is hashed (not equal to plain text)
        assertNotEquals("plainpassword", found.getPassword(), 
                       "Password should be hashed, not stored as plain text");
        
        // Verify hashed password can be verified
        assertTrue(PasswordUtil.verifyPassword("plainpassword", found.getPassword()),
                  "Should be able to verify original password against hash");
    }
    
    // Test getting all users by family
    // Used by family management page to display all members
    @Test
    public void testGetUsersByFamily() throws SQLException {
        // Create multiple users in same family
        User user1 = new User();
        user1.setEmail("family1@test.com");
        user1.setPassword("password123");
        user1.setFullName("Family User 1");
        user1.setRole("Family Head");
        user1.setFamilyId(testFamilyId);
        userManager.createUser(user1);
        
        User user2 = new User();
        user2.setEmail("family2@test.com");
        user2.setPassword("password123");
        user2.setFullName("Family User 2");
        user2.setRole("Adult");
        user2.setFamilyId(testFamilyId);
        userManager.createUser(user2);
        
        User user3 = new User();
        user3.setEmail("family3@test.com");
        user3.setPassword("password123");
        user3.setFullName("Family User 3");
        user3.setRole(null); // Pending
        user3.setFamilyId(testFamilyId);
        userManager.createUser(user3);
        
        // Create user in different family
        User otherFamily = new User();
        otherFamily.setEmail("other@test.com");
        otherFamily.setPassword("password123");
        otherFamily.setFullName("Other Family User");
        otherFamily.setRole("Adult");
        otherFamily.setFamilyId("F0002");
        userManager.createUser(otherFamily);
        
        // Get users by family
        List<User> familyUsers = userManager.getUsersByFamily(testFamilyId);
        
        // Verify results
        assertNotNull(familyUsers, "Should return list of family users");
        assertEquals(3, familyUsers.size(), "Should have 3 users in test family");
        
        // Verify pending user is listed first (ordered by pending status)
        assertEquals("Family User 3", familyUsers.get(0).getFullName(), 
                    "Pending users should appear first in list");
        assertNull(familyUsers.get(0).getRole(), "First user should have NULL role");
    }
}