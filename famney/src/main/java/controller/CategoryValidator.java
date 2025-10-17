package controller;

import java.io.Serializable;

// Validates category input for creation and updates
// Checks category name, type, and description fields
public class CategoryValidator implements Serializable {
    
    // Category name pattern - 2-50 characters, letters, numbers, spaces, and common symbols
    private String namePattern = "^[A-Za-z0-9\\s&\\-,.']{2,50}$";
    
    // Check if string matches pattern
    private boolean validate(String pattern, String input) {
        return input != null && input.matches(pattern);
    }
    
    // Validate category name
    // Must be 2-50 characters, can include letters, numbers, spaces, and symbols like & - ,
    public boolean validateCategoryName(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return false;
        }
        return validate(namePattern, categoryName.trim());
    }
    
    // Validate category type
    // Must be either "Expense" or "Income"
    public boolean validateCategoryType(String categoryType) {
        if (categoryType == null) {
            return false;
        }
        return categoryType.equals("Expense") || categoryType.equals("Income");
    }
    
    // Validate description (optional field)
    // If provided, must not exceed 200 characters
    public boolean validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            return true; // Description is optional
        }
        return description.trim().length() <= 200;
    }
    
    // Check if category name already exists for the family
    // Prevents duplicate category names within same family
    // This check is done in CategoryManager with database query
    public boolean isUniqueName(String categoryName, String familyId) {
        // This is checked in CategoryManager using database query
        // Kept here for interface consistency with UserValidator
        return true;
    }
}