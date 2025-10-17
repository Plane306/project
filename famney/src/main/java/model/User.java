package model;

import java.io.Serializable;
import java.util.Date;

// User class (Represents family members with role-based access)
// Core entity for F101 User Authentication & Family Management feature
public class User implements Serializable {
    
    private String userId;
    private String email;
    private String password; // Hashed password planned for R1
    private String fullName;
    private String role; // Family Head, Adult, Teen, Kid
    private String familyId; // Reference to family
    private Date joinDate;
    private Date createdDate;
    private Date lastModifiedDate;
    private boolean isActive;
    
    // Constructor for creating new user
    public User(String email, String password, String fullName, String role, String familyId) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.familyId = familyId;
        this.joinDate = new Date();
        this.createdDate = new Date();
        this.lastModifiedDate = new Date();
        this.isActive = true;
    }

    // Full constructor (for database retrieval)
    public User(String userId, String email, String password, String fullName, String role, 
                String familyId, Date joinDate, Date createdDate, Date lastModifiedDate, boolean isActive) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.familyId = familyId;
        this.joinDate = joinDate;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
        this.isActive = isActive;
    }
    
    // Default constructor
    public User() {
        this.joinDate = new Date();
        this.createdDate = new Date();
        this.lastModifiedDate = new Date();
        this.isActive = true;
    }
    
    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        this.lastModifiedDate = new Date();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        this.lastModifiedDate = new Date();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        this.lastModifiedDate = new Date();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
        this.lastModifiedDate = new Date();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
        this.lastModifiedDate = new Date();
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
        this.lastModifiedDate = new Date();
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
        this.lastModifiedDate = new Date();
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
        this.lastModifiedDate = new Date();
    }
    
    // Essential business logic methods only
    
    // Get display name for UI
    public String getDisplayName() {
        return fullName + " (" + role + ")";
    }
    
    // Check if user is family head
    public boolean isFamilyHead() {
        return "Family Head".equals(role);
    }
    
    // Check if user is adult (Family Head or Adult)
    public boolean isAdult() {
        return "Family Head".equals(role) || "Adult".equals(role);
    }

    public boolean isTeen() {
        return "Teen".equals(role);
    }

    public boolean isKid() {
        return "Kid".equals(role);
    }
    
    // Check if user can manage finances (Family Head or Adult)
    public boolean canManageFinances() {
        return "Family Head".equals(role) || "Adult".equals(role);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role='" + role + '\'' +
                ", familyId='" + familyId + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}