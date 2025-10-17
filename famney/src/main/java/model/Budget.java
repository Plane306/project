package model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

// Budget class (Represents family monthly/yearly budgets)
// Core entity for F103 Budget Management feature
public class Budget implements Serializable {
    
    private String budgetId;
    private String familyId;
    private String budgetName;
    private int month; // 1-12
    private int year;
    private double totalAmount;
    private String description;
    private Date createdDate;
    private Date lastModifiedDate;
    private boolean isActive;
    private String createdBy; // userId who created this budget
    
    // Constructor for creating new budget
    public Budget(String familyId, String budgetName, int month, int year, double totalAmount, String createdBy) {
        this.familyId = familyId;
        this.budgetName = budgetName;
        this.month = month;
        this.year = year;
        this.totalAmount = totalAmount;
        this.createdBy = createdBy;
        this.createdDate = new Date();
        this.lastModifiedDate = new Date();
        this.isActive = true;
    }
    
    // Constructor with description
    public Budget(String familyId, String budgetName, String description, int month, int year, 
                  double totalAmount, String createdBy) {
        this.familyId = familyId;
        this.budgetName = budgetName;
        this.description = description;
        this.month = month;
        this.year = year;
        this.totalAmount = totalAmount;
        this.createdBy = createdBy;
        this.createdDate = new Date();
        this.lastModifiedDate = new Date();
        this.isActive = true;
    }

    // Full constructor (for database retrieval)
    public Budget(String budgetId, String familyId, String budgetName, String description, int month, 
                  int year, double totalAmount, Date createdDate, Date lastModifiedDate, 
                  boolean isActive, String createdBy) {
        this.budgetId = budgetId;
        this.familyId = familyId;
        this.budgetName = budgetName;
        this.description = description;
        this.month = month;
        this.year = year;
        this.totalAmount = totalAmount;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
        this.isActive = isActive;
        this.createdBy = createdBy;
    }
    
    // Default constructor
    public Budget() {
        this.createdDate = new Date();
        this.lastModifiedDate = new Date();
        this.isActive = true;
        this.totalAmount = 0.0;
    }
    
    // Getters and Setters
    public String getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(String budgetId) {
        this.budgetId = budgetId;
        this.lastModifiedDate = new Date();
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
        this.lastModifiedDate = new Date();
    }

    public String getBudgetName() {
        return budgetName;
    }

    public void setBudgetName(String budgetName) {
        this.budgetName = budgetName;
        this.lastModifiedDate = new Date();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.lastModifiedDate = new Date();
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
        this.lastModifiedDate = new Date();
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
        this.lastModifiedDate = new Date();
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        this.lastModifiedDate = new Date();
    }
    
    // Business logic methods
    
    // Get formatted total amount
    public String getFormattedTotalAmount() {
        return String.format("$%.2f", totalAmount);
    }
    
    // Get month name
    public String getMonthName() {
        String[] months = {"", "January", "February", "March", "April", "May", "June",
                          "July", "August", "September", "October", "November", "December"};
        if (month >= 1 && month <= 12) {
            return months[month];
        }
        return "Invalid Month";
    }
    
    // Get budget period display
    public String getBudgetPeriodDisplay() {
        return getMonthName() + " " + year;
    }
    
    // Check if budget is for current month
    public boolean isCurrentMonth() {
        Calendar now = Calendar.getInstance();
        int currentMonth = now.get(Calendar.MONTH) + 1; // Calendar months are 0-11, so +1
        int currentYear = now.get(Calendar.YEAR);
        
        return this.month == currentMonth && this.year == currentYear;
    }

    // Check if budget period is in the past
    public boolean isPastBudget() {
        Calendar now = Calendar.getInstance();
        int currentYear = now.get(Calendar.YEAR);
        int currentMonth = now.get(Calendar.MONTH) + 1; // +1 because Calendar months are 0-11
        
        return this.year < currentYear || 
            (this.year == currentYear && this.month < currentMonth);
    }

    // Check if budget period is in the future
    public boolean isFutureBudget() {
        Calendar now = Calendar.getInstance();
        int currentYear = now.get(Calendar.YEAR);
        int currentMonth = now.get(Calendar.MONTH) + 1; // +1 because Calendar months are 0-11
        
        return this.year > currentYear || 
            (this.year == currentYear && this.month > currentMonth);
    }
    
    // Validate budget data
    public boolean isValid() {
        return familyId != null && !familyId.trim().isEmpty() &&
               budgetName != null && !budgetName.trim().isEmpty() &&
               month >= 1 && month <= 12 &&
               year >= 2020 && year <= 2050 &&
               totalAmount > 0 &&
               createdBy != null && !createdBy.trim().isEmpty();
    }
    
    // Get budget display name
    public String getDisplayName() {
        return budgetName + " - " + getBudgetPeriodDisplay();
    }
    
    @Override
    public String toString() {
        return "Budget{" +
                "budgetId='" + budgetId + '\'' +
                ", familyId='" + familyId + '\'' +
                ", budgetName='" + budgetName + '\'' +
                ", month=" + month +
                ", year=" + year +
                ", totalAmount=" + totalAmount +
                ", isActive=" + isActive +
                '}';
    }
}