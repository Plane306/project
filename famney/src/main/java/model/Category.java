package model;

import java.io.Serializable;
import java.util.Date;

// Category class (Represents expense and income categories for family budget organization)
// Core entity F102 Category Management feature
public class Category implements Serializable {
    
    private String categoryId;
    private String familyId;
    private String categoryName;
    private String categoryType; // "Expense" or "Income"
    private boolean isDefault; // true for system default categories
    private String description;
    private Date createdDate;
    private Date lastModifiedDate;
    private boolean isActive;
    
    // Constructor for creating new category
    public Category(String familyId, String categoryName, String categoryType, boolean isDefault) {
        this.familyId = familyId;
        this.categoryName = categoryName;
        this.categoryType = categoryType;
        this.isDefault = isDefault;
        this.createdDate = new Date();
        this.lastModifiedDate = new Date();
        this.isActive = true;
    }
    
    // Constructor with description
    public Category(String familyId, String categoryName, String categoryType, boolean isDefault, String description) {
        this.familyId = familyId;
        this.categoryName = categoryName;
        this.categoryType = categoryType;
        this.isDefault = isDefault;
        this.description = description;
        this.createdDate = new Date();
        this.lastModifiedDate = new Date();
        this.isActive = true;
    }

    // Full constructor (for database retrieval)
    public Category(String categoryId, String familyId, String categoryName, String categoryType, 
                    boolean isDefault, String description, Date createdDate, Date lastModifiedDate, boolean isActive) {
        this.categoryId = categoryId;
        this.familyId = familyId;
        this.categoryName = categoryName;
        this.categoryType = categoryType;
        this.isDefault = isDefault;
        this.description = description;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
        this.isActive = isActive;
    }
    
    // Default constructor
    public Category() {
        this.createdDate = new Date();
        this.lastModifiedDate = new Date();
        this.isActive = true;
        this.isDefault = false;
    }
    
    // Getters and Setters
    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
        this.lastModifiedDate = new Date();
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
        this.lastModifiedDate = new Date();
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
        this.lastModifiedDate = new Date();
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
        this.lastModifiedDate = new Date();
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
        this.lastModifiedDate = new Date();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
    
    // Business logic methods
    
    // Check if category is expense type
    public boolean isExpenseCategory() {
        return "Expense".equalsIgnoreCase(categoryType);
    }
    
    // Check if category is income type
    public boolean isIncomeCategory() {
        return "Income".equalsIgnoreCase(categoryType);
    }
    
    // Check if category can be deleted
    // Default categories and categories in use should not be deleted
    public boolean canBeDeleted() {
        return !isDefault; // For R0, we only check if it's not default
        // In R1+, we would also check if category is used in transactions
    }
    
    // Get display name with type indicator
    public String getDisplayName() {
        return categoryName + " (" + categoryType + ")";
    }
    
    // Get category type display
    public String getCategoryTypeDisplay() {
        return isExpenseCategory() ? "Expense Category" : "Income Category";
    }
    
    // Validate category name
    public boolean hasValidName() {
        return categoryName != null && !categoryName.trim().isEmpty() && categoryName.trim().length() <= 50;
    }
    
    // Validate category type
    public boolean hasValidType() {
        return "Expense".equalsIgnoreCase(categoryType) || "Income".equalsIgnoreCase(categoryType);
    }
    
    @Override
    public String toString() {
        return "Category{" +
                "categoryId='" + categoryId + '\'' +
                ", familyId='" + familyId + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", categoryType='" + categoryType + '\'' +
                ", isDefault=" + isDefault +
                ", isActive=" + isActive +
                '}';
    }
}