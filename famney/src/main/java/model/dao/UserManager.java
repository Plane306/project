package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.User;
import controller.PasswordUtil;
import controller.IdGenerator;
import controller.DateUtil;

// Data Access Object for User table operations
// Handles all database operations for users including authentication and role management
public class UserManager {
    
    private Connection conn;
    
    // Constructor takes database connection from ConnServlet
    public UserManager(Connection conn) throws SQLException {
        this.conn = conn;
    }
    
    // Create new user in database
    // Generates user ID automatically and hashes password
    // Role can be NULL for pending approval (join family scenario)
    // Returns true if successful, false if failed
    // Uses retry logic if duplicate userId occurs (2-layer prevention)
    public boolean createUser(User user) throws SQLException {
        // Hash password before storing
        String hashedPassword = PasswordUtil.hashPassword(user.getPassword());
        
        String sql = "INSERT INTO Users (userId, email, password, fullName, role, familyId, " +
                     "joinDate, createdDate, lastModifiedDate, isActive) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        // Try up to 3 times in case of duplicate userId
        int maxAttempts = 3;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            // Generate unique user ID for each attempt
            user.setUserId(IdGenerator.generateUserId());
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                String currentDateTime = DateUtil.getCurrentDateTime();
                
                stmt.setString(1, user.getUserId());
                stmt.setString(2, user.getEmail().trim());
                stmt.setString(3, hashedPassword);
                stmt.setString(4, user.getFullName().trim());
                stmt.setString(5, user.getRole()); // Can be NULL for pending approval
                stmt.setString(6, user.getFamilyId());
                stmt.setString(7, currentDateTime);
                stmt.setString(8, currentDateTime);
                stmt.setString(9, currentDateTime);
                stmt.setBoolean(10, true);
                
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
                
            } catch (SQLException e) {
                String errorMsg = e.getMessage();
                
                // If duplicate userId - retry with new ID
                if (errorMsg.contains("UNIQUE constraint failed: Users.userId")) {
                    if (attempt == maxAttempts - 1) {
                        // Last attempt failed - return false
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
                
                // For other SQL errors - throw exception
                throw e;
            }
        }
        
        return false;
    }
    
    // Find user by email for login
    // Returns User object if found, null if not found
    // Only returns active users (isActive = 1)
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM Users WHERE email = ? AND isActive = 1";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email.trim());
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getString("userId"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setFullName(rs.getString("fullName"));
                user.setRole(rs.getString("role")); // Can be NULL
                user.setFamilyId(rs.getString("familyId"));
                user.setJoinDate(DateUtil.parseToTimestamp(rs.getString("joinDate")));
                user.setCreatedDate(DateUtil.parseToTimestamp(rs.getString("createdDate")));
                user.setLastModifiedDate(DateUtil.parseToTimestamp(rs.getString("lastModifiedDate")));
                user.setActive(rs.getBoolean("isActive"));
                
                return user;
            }
            
            return null;
        }
    }
    
    // Authenticate user login
    // Returns User object if credentials valid, null if invalid
    public User authenticate(String email, String password) throws SQLException {
        User user = findByEmail(email);
        
        if (user == null) {
            return null;
        }
        
        // Verify password matches stored hash
        if (PasswordUtil.verifyPassword(password, user.getPassword())) {
            return user;
        }
        
        return null;
    }
    
    // Get all users in a family
    // Used by family management page
    // Orders by pending status first, then by role hierarchy
    public List<User> getUsersByFamily(String familyId) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE familyId = ? AND isActive = 1 ORDER BY " +
                     "CASE WHEN role IS NULL THEN 0 ELSE 1 END, " +
                     "CASE role " +
                     "WHEN 'Family Head' THEN 1 " +
                     "WHEN 'Adult' THEN 2 " +
                     "WHEN 'Teen' THEN 3 " +
                     "WHEN 'Kid' THEN 4 END";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, familyId);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getString("userId"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setFullName(rs.getString("fullName"));
                user.setRole(rs.getString("role")); // Can be NULL
                user.setFamilyId(rs.getString("familyId"));
                user.setJoinDate(DateUtil.parseToTimestamp(rs.getString("joinDate")));
                user.setCreatedDate(DateUtil.parseToTimestamp(rs.getString("createdDate")));
                user.setLastModifiedDate(DateUtil.parseToTimestamp(rs.getString("lastModifiedDate")));
                user.setActive(rs.getBoolean("isActive"));
                
                users.add(user);
            }
        }
        
        return users;
    }
    
    // Get users with pending role approval (role is NULL)
    // Used by Family Head to see who needs role assignment
    public List<User> getPendingUsers(String familyId) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE familyId = ? AND role IS NULL AND isActive = 1 " +
                     "ORDER BY joinDate DESC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, familyId);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getString("userId"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setFullName(rs.getString("fullName"));
                user.setRole(null); // Explicitly NULL
                user.setFamilyId(rs.getString("familyId"));
                user.setJoinDate(DateUtil.parseToTimestamp(rs.getString("joinDate")));
                user.setCreatedDate(DateUtil.parseToTimestamp(rs.getString("createdDate")));
                user.setLastModifiedDate(DateUtil.parseToTimestamp(rs.getString("lastModifiedDate")));
                user.setActive(rs.getBoolean("isActive"));
                
                users.add(user);
            }
        }
        
        return users;
    }
    
    // Update user profile
    // Only updates email, full name, and lastModifiedDate
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE Users SET email = ?, fullName = ?, lastModifiedDate = ? " +
                     "WHERE userId = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getEmail().trim());
            stmt.setString(2, user.getFullName().trim());
            stmt.setString(3, DateUtil.getCurrentDateTime());
            stmt.setString(4, user.getUserId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    // Update user password
    // Separate method for password updates with hashing
    public boolean updatePassword(String userId, String newPassword) throws SQLException {
        String hashedPassword = PasswordUtil.hashPassword(newPassword);
        
        String sql = "UPDATE Users SET password = ?, lastModifiedDate = ? WHERE userId = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hashedPassword);
            stmt.setString(2, DateUtil.getCurrentDateTime());
            stmt.setString(3, userId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    // Update user role
    // Only Family Head can change roles
    // Also used to assign initial role for pending users
    public boolean updateUserRole(String userId, String newRole) throws SQLException {
        String sql = "UPDATE Users SET role = ?, lastModifiedDate = ? WHERE userId = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newRole);
            stmt.setString(2, DateUtil.getCurrentDateTime());
            stmt.setString(3, userId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    // Soft delete user (set isActive to false)
    // Only Family Head can remove members
    // Data is preserved for analytics
    public boolean deleteUser(String userId) throws SQLException {
        String sql = "UPDATE Users SET isActive = 0, lastModifiedDate = ? WHERE userId = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, DateUtil.getCurrentDateTime());
            stmt.setString(2, userId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    // Deactivate all users in a family (when family is closed)
    // Called when Family Head closes the entire family
    // All member data is preserved for analytics
    public boolean deactivateAllFamilyUsers(String familyId) throws SQLException {
        String sql = "UPDATE Users SET isActive = 0, lastModifiedDate = ? WHERE familyId = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, DateUtil.getCurrentDateTime());
            stmt.setString(2, familyId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    // Check if email already exists for ACTIVE users only
    // Used during registration validation
    // Allows email reuse if previous user is inactive (soft deleted)
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Users WHERE email = ? AND isActive = 1";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email.trim());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        
        return false;
    }
}