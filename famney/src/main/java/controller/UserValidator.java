package controller;

import java.io.Serializable;

// Validates user input for registration and profile updates
// Checks email format and required fields
public class UserValidator implements Serializable {
    
    // Email regex pattern - basic email validation
    private String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    
    // Name pattern - only letters and spaces, 2-100 characters
    private String namePattern = "^[A-Za-z\\s]{2,100}$";
    
    // Check if string matches pattern
    private boolean validate(String pattern, String input) {
        return input != null && input.matches(pattern);
    }
    
    // Validate email format
    // Returns true if email is valid format
    public boolean validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return validate(emailPattern, email.trim());
    }
    
    // Validate full name
    // Must be 2-100 characters, only letters and spaces
    public boolean validateFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return false;
        }
        return validate(namePattern, fullName.trim());
    }
    
    // Validate password
    // Must be at least 6 characters
    public boolean validatePassword(String password) {
        return password != null && password.length() >= 6;
    }
    
    // Validate family name
    // Must be 2-100 characters
    public boolean validateFamilyName(String familyName) {
        if (familyName == null || familyName.trim().isEmpty()) {
            return false;
        }
        String trimmed = familyName.trim();
        return trimmed.length() >= 2 && trimmed.length() <= 100;
    }
    
    // Validate role selection
    // Must be one of: Family Head, Adult, Teen, Kid
    public boolean validateRole(String role) {
        if (role == null) {
            return false;
        }
        return role.equals("Family Head") || role.equals("Adult") || 
               role.equals("Teen") || role.equals("Kid");
    }
    
    // Validate family code format
    // Must be in format FAMNEY-XXXX (11 characters total)
    public boolean validateFamilyCode(String familyCode) {
        if (familyCode == null) {
            return false;
        }
        String trimmed = familyCode.trim().toUpperCase();
        return trimmed.matches("^FAMNEY-[A-Z0-9]{4}$");
    }
    
    // Check if password and confirm password match
    public boolean passwordsMatch(String password, String confirmPassword) {
        if (password == null || confirmPassword == null) {
            return false;
        }
        return password.equals(confirmPassword);
    }
}