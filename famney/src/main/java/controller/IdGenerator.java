package controller;

import java.util.Random;

// Utility class for generating unique IDs and codes
// Used by servlets when creating new families, users, and categories
// For this project, we will use 2-layer duplicate prevention:
// 1. Timestamp-based generation has very low collision chance
// 2. Database PRIMARY KEY constraint as safety net (rejects duplicates automatically)
public class IdGenerator {
    
    private static final Random random = new Random();
    
    // Generate family code in format: FAMNEY-XXXX
    // XXXX = 4 random uppercase letters and numbers
    // Example: FAMNEY-A1B2, FAMNEY-XY9Z
    // This code is shared with family members to join the family
    public static String generateFamilyCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder("FAMNEY-");
        
        for (int i = 0; i < 4; i++) {
            int index = random.nextInt(chars.length());
            code.append(chars.charAt(index));
        }
        
        return code.toString();
    }
    
    // Generate user ID in format: U0001, U0002, etc
    // Uses current timestamp to generate unique ID
    // Collision chance is very low because millisecond precision
    public static String generateUserId() {
        long timestamp = System.currentTimeMillis();
        int id = (int) (timestamp % 10000);
        return String.format("U%04d", id);
    }
    
    // Generate family ID in format: F0001, F0002, etc
    // Same approach as user ID
    public static String generateFamilyId() {
        long timestamp = System.currentTimeMillis();
        int id = (int) (timestamp % 10000);
        return String.format("F%04d", id);
    }
    
    // Generate category ID in format: C0001, C0002, etc
    // Same approach as user ID
    public static String generateCategoryId() {
        long timestamp = System.currentTimeMillis();
        int id = (int) (timestamp % 10000);
        return String.format("C%04d", id);
    }
    
    // Generate budget ID in format: B0001, B0002, etc
    // Added for F103 budget feature
    public static String generateBudgetId() {
        long timestamp = System.currentTimeMillis();
        int id = (int) (timestamp % 10000);
        return String.format("B%04d", id);
    }
    
    // Generate income ID in format: I0001, I0002, etc
    // Added for F105 income feature
    public static String generateIncomeId() {
        long timestamp = System.currentTimeMillis();
        int id = (int) (timestamp % 10000);
        return String.format("I%04d", id);
    }
    
    // Generate expense ID in format: E0001, E0002, etc
    // Added for F104 expense feature
    public static String generateExpenseId() {
        long timestamp = System.currentTimeMillis();
        int id = (int) (timestamp % 10000);
        return String.format("E%04d", id);
    }
    
    // Generate savings goal ID in format: G0001, G0002, etc
    // Added for F107 savings goals feature
    public static String generateGoalId() {
        long timestamp = System.currentTimeMillis();
        int id = (int) (timestamp % 10000);
        return String.format("G%04d", id);
    }
    
    // Test method to see sample generated IDs
    // Run this to verify ID formats are correct
    public static void main(String[] args) {
        System.out.println("Sample Generated IDs:");
        System.out.println("Family Code: " + generateFamilyCode());
        System.out.println("User ID: " + generateUserId());
        System.out.println("Family ID: " + generateFamilyId());
        System.out.println("Category ID: " + generateCategoryId());
        System.out.println("Budget ID: " + generateBudgetId());
        System.out.println("Income ID: " + generateIncomeId());
        System.out.println("Expense ID: " + generateExpenseId());
        System.out.println("Goal ID: " + generateGoalId());
        
        // Test generating multiple IDs to show they're different
        System.out.println("\nGenerating 3 user IDs in sequence:");
        for (int i = 0; i < 3; i++) {
            System.out.println("  " + generateUserId());
            try {
                Thread.sleep(5); // Small delay to ensure different timestamps
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}