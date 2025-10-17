package model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

// Expense class (Represents family expense transactions)
// Core entity for F104 Expense Tracking feature
public class Expense implements Serializable {
    
    private String expenseId;
    private String familyId;
    private String userId; // Who recorded this expense
    private String categoryId;
    private double amount;
    private String description;
    private Date expenseDate; // When the expense occurred
    private Date createdDate; // When the record was created
    private Date lastModifiedDate;
    private boolean isActive;
    private String receiptUrl; // For future enhancement (R1+)
    
    // Constructor for creating new expense
    public Expense(String familyId, String userId, String categoryId, double amount, String description, Date expenseDate) {
        this.familyId = familyId;
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.description = description;
        this.expenseDate = expenseDate;
        this.createdDate = new Date();
        this.lastModifiedDate = new Date();
        this.isActive = true;
    }
    
    // Constructor with current date as expense date
    public Expense(String familyId, String userId, String categoryId, double amount, String description) {
        this.familyId = familyId;
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.description = description;
        this.expenseDate = new Date();
        this.createdDate = new Date();
        this.lastModifiedDate = new Date();
        this.isActive = true;
    }

    // Full constructor (for database retrieval)
    public Expense(String expenseId, String familyId, String userId, String categoryId, double amount, 
                   String description, Date expenseDate, Date createdDate, Date lastModifiedDate, 
                   boolean isActive, String receiptUrl) {
        this.expenseId = expenseId;
        this.familyId = familyId;
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.description = description;
        this.expenseDate = expenseDate;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
        this.isActive = isActive;
        this.receiptUrl = receiptUrl;
    }
    
    // Default constructor
    public Expense() {
        this.expenseDate = new Date();
        this.createdDate = new Date();
        this.lastModifiedDate = new Date();
        this.isActive = true;
        this.amount = 0.0;
    }
    
    // Getters and Setters
    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
        this.lastModifiedDate = new Date();
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
        this.lastModifiedDate = new Date();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        this.lastModifiedDate = new Date();
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
        this.lastModifiedDate = new Date();
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
        this.lastModifiedDate = new Date();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.lastModifiedDate = new Date();
    }

    public Date getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(Date expenseDate) {
        this.expenseDate = expenseDate;
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

    public String getReceiptUrl() {
        return receiptUrl;
    }

    public void setReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
        this.lastModifiedDate = new Date();
    }
    
    // Business logic methods
    
    // Check if expense amount is valid
    public boolean hasValidAmount() {
        return amount > 0;
    }
    
    // Check if expense has required fields
    public boolean hasRequiredFields() {
        return familyId != null && !familyId.trim().isEmpty() &&
               userId != null && !userId.trim().isEmpty() &&
               categoryId != null && !categoryId.trim().isEmpty() &&
               hasValidAmount() &&
               expenseDate != null;
    }
    
    // Get formatted amount as currency string
    public String getFormattedAmount() {
        return String.format("$%.2f", amount);
    }
    
    // Get short description for display (max 30 chars)
    public String getShortDescription() {
        if (description == null || description.trim().isEmpty()) {
            return "No description";
        }
        String trimmed = description.trim();
        return trimmed.length() <= 30 ? trimmed : trimmed.substring(0, 27) + "...";
    }
    
    // Check if expense is from this month
    public boolean isFromThisMonth() {
        if (expenseDate == null) return false;
        
        Calendar expenseCal = Calendar.getInstance();
        expenseCal.setTime(expenseDate);
        
        Calendar now = Calendar.getInstance();
        
        return expenseCal.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
            expenseCal.get(Calendar.YEAR) == now.get(Calendar.YEAR);
    }

    // Check if expense is from today
    public boolean isFromToday() {
        if (expenseDate == null) return false;
        
        Calendar expenseCal = Calendar.getInstance();
        expenseCal.setTime(expenseDate);
        
        Calendar now = Calendar.getInstance();
        
        return expenseCal.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH) &&
            expenseCal.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
            expenseCal.get(Calendar.YEAR) == now.get(Calendar.YEAR);
    }
    
    // Get expense age in days
    public long getExpenseAgeInDays() {
        if (expenseDate == null) return 0;
        
        Date now = new Date();
        long diffInMillies = Math.abs(now.getTime() - expenseDate.getTime());
        return diffInMillies / (24 * 60 * 60 * 1000);
    }
    
    // Check if expense is recent (within last 7 days)
    public boolean isRecent() {
        return getExpenseAgeInDays() <= 7;
    }
    
    // Get transaction type for unified transaction history
    public String getTransactionType() {
        return "Expense";
    }
    
    // Get display title combining amount and description
    public String getDisplayTitle() {
        String desc = getShortDescription();
        return getFormattedAmount() + " - " + desc;
    }
    
    // Validate expense data
    public boolean isValid() {
        return hasRequiredFields() && 
               (description == null || description.length() <= 255);
    }
    
    @Override
    public String toString() {
        return "Expense{" +
                "expenseId='" + expenseId + '\'' +
                ", familyId='" + familyId + '\'' +
                ", userId='" + userId + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", expenseDate=" + expenseDate +
                ", isActive=" + isActive +
                '}';
    }
}