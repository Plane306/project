package controller;

import org.apache.commons.codec.digest.DigestUtils;

// Password security utility for user authentication
// Uses SHA-256 hashing with salt for secure password storage
public class PasswordUtil {
    
    // Salt value added to password before hashing
    // In production this should be per-user, but global salt is fine for this project
    private static final String SALT = "famney_salt";
    
    // Hash a plain text password using SHA-256 with salt
    // Used when creating new users or changing passwords
    // Example: hashPassword("password123") returns the hashed version for database
    public static String hashPassword(String password) {
        return DigestUtils.sha256Hex(password + SALT);
    }
    
    // Verify if plain password matches stored hash
    // Used during login to check credentials
    // Returns true if password is correct, false otherwise
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return hashPassword(plainPassword).equals(hashedPassword);
    }
    
    // Test method to generate hash for sample data
    // Run this to get hash value for table_data.sql
    public static void main(String[] args) {
        String testPassword = "password123";
        String hash = hashPassword(testPassword);
        
        System.out.println("Plain password: " + testPassword);
        System.out.println("Hashed value: " + hash);
        System.out.println("\nThis hash should be used in table_data.sql for all sample users");
    }
}